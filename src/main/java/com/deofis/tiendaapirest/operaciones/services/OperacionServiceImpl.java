package com.deofis.tiendaapirest.operaciones.services;

import com.deofis.tiendaapirest.autenticacion.dto.UsuarioDTO;
import com.deofis.tiendaapirest.autenticacion.exceptions.AutenticacionException;
import com.deofis.tiendaapirest.autenticacion.services.AutenticacionService;
import com.deofis.tiendaapirest.checkout.services.CheckoutService;
import com.deofis.tiendaapirest.clientes.domain.Cliente;
import com.deofis.tiendaapirest.emails.dto.NotificationEmail;
import com.deofis.tiendaapirest.emails.services.MailService;
import com.deofis.tiendaapirest.globalservices.RoundService;
import com.deofis.tiendaapirest.notificaciones.services.NotificacionService;
import com.deofis.tiendaapirest.operaciones.domain.DetalleOperacion;
import com.deofis.tiendaapirest.operaciones.domain.EstadoOperacion;
import com.deofis.tiendaapirest.operaciones.domain.EventoOperacion;
import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.operaciones.dto.OperacionRequest;
import com.deofis.tiendaapirest.operaciones.exceptions.OperacionException;
import com.deofis.tiendaapirest.operaciones.repositories.OperacionRepository;
import com.deofis.tiendaapirest.pagos.domain.MedioPago;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoMapping;
import com.deofis.tiendaapirest.pagos.repositories.MedioPagoRepository;
import com.deofis.tiendaapirest.perfiles.domain.Perfil;
import com.deofis.tiendaapirest.perfiles.services.AdministradorService;
import com.deofis.tiendaapirest.perfiles.services.PerfilService;
import com.deofis.tiendaapirest.perfiles.services.ValidadorItems;
import com.deofis.tiendaapirest.productos.domain.Sku;
import com.deofis.tiendaapirest.productos.services.SkuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class OperacionServiceImpl implements OperacionService {
    public static final String NRO_OPERACION_HEADER = "nroOperacion";

    private final StateMachineService stateMachineService;

    private final AutenticacionService autenticacionService;
    private final AdministradorService administradorService;

    private final MailService mailService;
    private final NotificacionService notificacionService;

    private final OperacionRepository operacionRepository;
    private final MedioPagoRepository medioPagoRepository;
    private final SkuService skuService;
    private final PerfilService perfilService;

    private final CheckoutService checkoutService;

    private final OperacionPagoMapping operacionPagoMapping;
    private final RoundService roundService;
    private final ValidadorItems validadorItems;

    @Transactional
    @Override
    public OperacionPagoInfo registrarNuevaOperacion(Operacion operacion) {

        this.validarAutenticacion();
        Cliente cliente = this.perfilService.obtenerDatosCliente();
        MedioPago medioPago = this.obtenerMedioPagoGuardado(operacion.getMedioPago().getId());

        Operacion nuevaOperacion = Operacion.builder()
                .cliente(cliente)
                .direccionEnvio(operacion.getDireccionEnvio())
                .fechaOperacion(new Date(new Date().getTime()))
                .fechaEnviada(null)
                .fechaRecibida(null)
                .medioPago(medioPago)
                .pago(null)
                .estado(EstadoOperacion.PAYMENT_PENDING)
                .total(0.0)
                .items(operacion.getItems())
                .build();

        boolean hayDisponibilidad = true;
        boolean hayItemSinCantidad = false;
        boolean itemNoVendible = false;
        for (DetalleOperacion item: operacion.getItems()) {
            Sku sku = this.skuService.obtenerSku(item.getSku().getId());
            // Validamos que el SKU no sea default sku si su producto NO es vendible sin propiedades (no tiene
            // skus adicionales)
            if (this.validadorItems.esItemNoVendible(sku)) {
                itemNoVendible = true;
                break;
            }

            // Seteamos el SKU completo al item (lo que esta guardado en la BD).
            item.setSku(sku);

            if (sku.getDisponibilidad() - item.getCantidad() < 0) {
                hayDisponibilidad = false;
                break;
            }

            if (item.getCantidad() <= 0) {
                hayItemSinCantidad = true;
                break;
            }

            // Calculamos el precio de venta el producto (sku) de acuerdo a si está en promoción o no.
            item.setPrecioVenta(this.calcularPrecioVenta(sku));

            // Seteamos la nueva disponibilidad del SKU al finalizar la operación.
            sku.setDisponibilidad(sku.getDisponibilidad() - item.getCantidad());

            // Calculamos y guardamos el subtotal del item.
            item.setSubtotal(item.getPrecioVenta() * item.getCantidad().doubleValue());
            // Calculamos dentro del ciclo el TOTAL de la operación, para evitarnos calcularlo fuera y volverlo
            // a recorrer. Redondeamos el total y guardamos.
            nuevaOperacion.setTotal(this.roundService.round(this.calcularTotal(nuevaOperacion.getTotal(),
                    item.getSubtotal())));

            // Se guarda el SKU con la disponibilidad actualizada.
            this.skuService.save(sku);
        }

        // Si no hay disponibilidad de un producto, se cancela la operación y tira excepción informando.
        if (!hayDisponibilidad) throw new OperacionException("Error al completar la compra: " +
                "La cantidad de productos vendidos no puede ser menor a la disponibilidad actual");

        if (hayItemSinCantidad)
            throw new OperacionException("Error al completar la compra: La cantidad de productos no puede ser" +
                    " menor o igual que 0");

        if (itemNoVendible)
            throw new OperacionException("Hay al menos un item no vendible: algún item posee un sku por defecto y" +
                    " el producto tiene skus adicionales.");

        this.save(nuevaOperacion);

        // Agregar nueva operación (COMPRA) al array de compras del perfil del usuario.
        Perfil perfil = this.perfilService.obtenerPerfil();
        perfil.getCompras().add(nuevaOperacion);
        this.perfilService.vaciarCarrito();
        this.perfilService.save(perfil);

        // Llamamos a los métodos para enviar mails al usuario final, y todos los admins del sistema.
        this.enviarEmailUsuario(nuevaOperacion, cliente.getEmail());
        this.enviarEmailsAdmins(nuevaOperacion);

        log.info("total guardado -> " + nuevaOperacion.getTotal());
        // Delegar la creación del PAGO de operación al PagoStrategy correspondiente.
        OperacionPagoInfo operacionPagoInfo = this.checkoutService.iniciarCheckout(nuevaOperacion);

        // Persistir el pago creado y pendiente de pagar asociado a la operación recientemente registrada.
        this.guardarOperacionPago(nuevaOperacion, operacionPagoInfo);

        // Enviar la notificación al usuario que realizó la compra
        this.notificacionService.enviarNotificacion(nuevaOperacion, nuevaOperacion.getCliente().getEmail());
        return operacionPagoInfo;
    }

    private MedioPago obtenerMedioPagoGuardado(Long medioPagoId) {
        return this.medioPagoRepository.findById(medioPagoId)
                .orElseThrow(() -> new OperacionException("Medio de pago no implementado en el sistema"));
    }

    private void validarAutenticacion() {
        if (!this.autenticacionService.estaLogueado())
            throw new AutenticacionException("Usuario no logueado en el sistema");
    }

    @Transactional
    @Override
    public OperacionPagoInfo registrarComprarYa(OperacionRequest operacionRequest) {

        this.validarAutenticacion();
        Cliente clienteActual = this.perfilService.obtenerDatosCliente();
        MedioPago medioPago = this.obtenerMedioPagoGuardado(operacionRequest.getMedioPago().getId());

        Operacion nuevaOperacion = Operacion.builder()
                .cliente(clienteActual)
                .direccionEnvio(operacionRequest.getDireccionEnvio())
                .fechaOperacion(new Date(new Date().getTime()))
                .fechaEnviada(null)
                .fechaRecibida(null)
                .medioPago(medioPago)
                .pago(null)
                .estado(EstadoOperacion.PAYMENT_PENDING)
                .total(0.0)
                .items(new ArrayList<>())
                .build();

        nuevaOperacion.getItems().add(operacionRequest.getItem());
        return this.registrarNuevaOperacion(nuevaOperacion);
    }

    @Transactional
    @Override
    public Operacion registrarEnvio(Long nroOperacion) {
        Operacion operacion = this.findById(nroOperacion);
        StateMachine<EstadoOperacion, EventoOperacion> sm = this.stateMachineService.build(nroOperacion);

        sm.getExtendedState().getVariables().put("operacion", operacion);

        this.stateMachineService.enviarEvento(nroOperacion, sm, EventoOperacion.SEND);
        return operacion;
    }

    @Transactional
    @Override
    public Operacion registrarRecibo(Long nroOperacion) {
        Operacion operacion = this.findById(nroOperacion);
        StateMachine<EstadoOperacion, EventoOperacion> sm = this.stateMachineService.build(nroOperacion);

        sm.getExtendedState().getVariables().put("operacion", operacion);

        this.stateMachineService.enviarEvento(nroOperacion, sm, EventoOperacion.RECEIVE);
        return operacion;
    }

    @Transactional
    @Override
    public Operacion cancelar(Long nroOperacion) {
        Operacion operacion = this.findById(nroOperacion);
        StateMachine<EstadoOperacion, EventoOperacion> sm = this.stateMachineService.build(nroOperacion);

        this.stateMachineService.enviarEvento(nroOperacion, sm, EventoOperacion.CANCEL);
        return operacion;
    }

    private Double calcularTotal(Double total, Double subTotal) {
        return total + subTotal;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> findAll() {
        return this.operacionRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Operacion findById(Long id) {
        return this.operacionRepository.findById(id)
                .orElseThrow(() -> new OperacionException("No existe la operacion con numero: " + id));
    }

    @Transactional
    @Override
    public Operacion save(Operacion object) {
        return this.operacionRepository.save(object);
    }

    @Override
    public void delete(Operacion object) {

    }

    @Override
    public void deleteById(Long aLong) {

    }

    private void guardarOperacionPago(Operacion nuevaOperacion, OperacionPagoInfo operacionPagoInfo) {
        nuevaOperacion.setPago(this.operacionPagoMapping.mapToOperacionPago(operacionPagoInfo));
        nuevaOperacion.getPago().setFechaCreacion(new Date());
        this.save(nuevaOperacion);
    }

    private Double calcularPrecioVenta(Sku sku) {
        if (sku.getPromocion() != null && sku.getPromocion().getEstaVigente())
            return sku.getPromocion().getPrecioOferta();

        return sku.getPrecio();
    }

    private void enviarEmailsAdmins(Operacion operacion) {
        List<UsuarioDTO> admins = this.administradorService.obtenerAdministradores();

        for (UsuarioDTO admin: admins) {
            NotificationEmail notificationEmail = new NotificationEmail();
            String body = "Se ha registrado una nueva venta en el sistema. Revisar la venta con " +
                    "número de operacion: " + operacion.getNroOperacion() +
                    " para realizar la facturación correspondiente al cliente: " +
                    operacion.getCliente().getNombre() + ", " + operacion.getCliente().getApellido();

            notificationEmail.setBody(body);
            notificationEmail.setSubject("Nueva venta registrada :: Deofis");

            notificationEmail.setRecipient(admin.getEmail());
            this.mailService.sendEmail(notificationEmail);

            log.info(admin.getEmail());
        }
    }

    private void enviarEmailUsuario(Operacion operacion, String emailUsuario) {
        NotificationEmail notificationEmail = new NotificationEmail();
        String body = "Gracias por realizar la compra! En la brevedad recibirá a su correo la facturación " +
                "realizada por la tienda.\n" +
                "Número de compra: " + operacion.getNroOperacion() + "\n" +
                "\nPorfavor, revise los datos de la compra en su perfil de usuario!";
        notificationEmail.setBody(body);
        notificationEmail.setRecipient(emailUsuario);
        notificationEmail.setSubject("Nueva compra registrada");

        log.info(emailUsuario);

        this.mailService.sendEmail(notificationEmail);
    }
}

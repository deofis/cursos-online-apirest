package com.deofis.tiendaapirest.operaciones.services;

import com.deofis.tiendaapirest.clientes.domain.Cliente;
import com.deofis.tiendaapirest.clientes.services.ClienteService;
import com.deofis.tiendaapirest.operaciones.domain.EstadoOperacion;
import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.operaciones.exceptions.OperacionException;
import com.deofis.tiendaapirest.operaciones.repositories.OperacionRepository;
import com.deofis.tiendaapirest.pagos.domain.OperacionPago;
import com.deofis.tiendaapirest.perfiles.services.PerfilService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Service
@AllArgsConstructor
public class CompraServiceImpl implements CompraService {

    private final OperacionRepository operacionRepository;
    private final ClienteService clienteService;
    private final PerfilService perfilService;

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> historialCompras() {

        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findAllByClienteOrderByFechaOperacionAsc(cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasPendientePago() {
        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findAllByEstadoAndCliente(EstadoOperacion.PAYMENT_PENDING, cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasCompletadoPago() {
        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findAllByEstadoAndCliente(EstadoOperacion.PAYMENT_DONE, cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasEnviadas() {
        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findAllByEstadoAndCliente(EstadoOperacion.SENT, cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasRecibidas() {
        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findAllByEstadoAndCliente(EstadoOperacion.RECEIVED, cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasCanceladas() {
        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findAllByEstadoAndCliente(EstadoOperacion.CANCELLED, cliente);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasYear(Integer year) {
        Cliente cliente = this.getClienteDelPerfilActual();

        /*
        Implementación vieja.
        LocalDate localDate = LocalDate.of(year, 1, 1);
        Date dateOfYear = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
         */

        return this.operacionRepository.operacionesByYear(cliente.getId(), year);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Operacion> comprasMonth(Integer month) {
        Cliente cliente = this.getClienteDelPerfilActual();

        int actualYear = Calendar.getInstance().get(Calendar.YEAR);
        return this.operacionRepository.operacionesByMonth(cliente.getId(), actualYear, month);
    }

    @Transactional(readOnly = true)
    @Override
    public Operacion verCompra(Long nroOperacion) {
        Cliente cliente = this.getClienteDelPerfilActual();

        return this.operacionRepository.findByNroOperacionAndCliente(nroOperacion, cliente)
                .orElseThrow(() -> new OperacionException("No existe la operación del cliente seleccionado"));
    }

    @Transactional(readOnly = true)
    @Override
    public OperacionPago obtenerPagoCompra(Long nroOperacion) {
        Operacion operacion = this.verCompra(nroOperacion);
        return operacion.getPago();
    }

    /**
     * Obtiene el {@link Cliente} del perfil logueado actualmente en el sistema.
     * @return Cliente actual.
     */
    private Cliente getClienteDelPerfilActual() {
        return this.perfilService.obtenerPerfil().getCliente();
    }
}

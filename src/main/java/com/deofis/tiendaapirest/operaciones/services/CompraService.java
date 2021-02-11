package com.deofis.tiendaapirest.operaciones.services;

import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.pagos.domain.OperacionPago;

import java.util.List;

/**
 * Este servicio se encarga de la lógica para el manejo de {@link Operacion}es desde el lado
 * del usuario comprador de productos del sistema, es decir, viendo las {@link Operacion}es como COMPRAS.
 */

public interface CompraService {

    /**
     * Obtener un listado con el historial de compras del cliente del perfil ordenado por fecha de
     * menor a mayor.
     * @return List operaciones de compra del perfil.
     */
    List<Operacion> historialCompras();

    /**
     * Obtiene un listado con las compras del usuario que estan en el estado pendiente de pago.
     * @return List con operaciones del usuario actual en estado PAYMENT_PENDING.
     */
    List<Operacion> comprasPendientePago();

    /**
     * Obtiene un listado con las compras del usuario que estan en estado pago completado.
     * @return List con operaciones del usuario actual en estado PAYMENT_DONE.
     */
    List<Operacion> comprasCompletadoPago();

    /**
     * Obtiene un listado con las compras del usuario que estan en estado enviada.
     * @return List con operaciones del usuario actual en estado SENT.
     */
    List<Operacion> comprasEnviadas();

    /**
     * Obtiene un listado con las compras del usuario que estan en estado recibida.
     * @return List con operaciones del usuario actual en estado RECEIVED.
     */
    List<Operacion> comprasRecibidas();

    /**
     * Obtiene un listado con las compras del usuario que estan en estado cancelada.
     * @return List con operaciones del usuario actual en estado CANCELLED.
     */
    List<Operacion> comprasCanceladas();

    /**
     * Obtiene un listado con las compras del usuario realizadas en el año requerido.
     * @param year Integer año solicitado.
     * @return List con operaciones del usuario actual en el año solicitada.
     */
    List<Operacion> comprasYear(Integer year);

    /**
     * Obtiene un listado con las compras del usuario realizadas en el mes requerido del año actual.
     * @param month Integer mes solicitado.
     * @return List con operaciones del usuario actual en el mes solicitado.
     */
    List<Operacion> comprasMonth(Integer month);

    /**
     * Obtiene una operación de compra.
     * @param nroOperacion Long numero de operación.
     * @return Operacion.
     */
    Operacion verCompra(Long nroOperacion);

    /**
     * Obtiene el pago de una compra en particular de un usuario.
     * @param nroOperacion Long id de la operacion a obtener su pago.
     * @return {@link OperacionPago} correspondiente a la compra.
     */
    OperacionPago obtenerPagoCompra(Long nroOperacion);
}

package com.deofis.tiendaapirest.checkout.services;

import com.deofis.tiendaapirest.checkout.dto.CheckoutPayload;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;

/**
 * Servicio que se encarga de ejecutar el checkout de una operación, completando el pago
 * de la misma.
 */
public interface CheckoutService {

    /**
     * Completa el pago para una operación, lo que implica hacer una petición a la API de pago
     * correspondiente para obtener los datos y completar el pago.
     * <br>
     * Una vez completado el pago, se le asigna a la operación el objeto OperacionPago con
     * la información del pago completado, y se debe transitar al estado de operación
     * correspondiente.
     * @param checkoutPayload {@link CheckoutPayload} con el nroOperacion y el paymentId, que será
     *                                               distinto según medio de pago.
     * @return {@link OperacionPagoInfo} con los datos del pago COMPLETADO/APROBADO.
     */
    OperacionPagoInfo ejecutarCheckoutSuccess(CheckoutPayload checkoutPayload);

    /**
     * Interfaz todavía no diseñada.
     */
    void ejecutarCheckoutFailure();
}

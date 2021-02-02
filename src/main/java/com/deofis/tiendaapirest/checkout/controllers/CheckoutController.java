package com.deofis.tiendaapirest.checkout.controllers;

import com.deofis.tiendaapirest.checkout.dto.CheckoutPayload;
import com.deofis.tiendaapirest.checkout.services.CheckoutService;
import com.deofis.tiendaapirest.operaciones.exceptions.OperacionException;
import com.deofis.tiendaapirest.pagos.PaymentException;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class CheckoutController {

    private final CheckoutService checkoutService;

    /**
     * Completa el pago (checkout) para una operación requerida.
     * URL: ~/api/checkout/completar/pago
     * HttpMethod: POST
     * HttpStatus: CREATED
     * @param checkoutPayload {@link CheckoutPayload} con nroOperacion y paymentId.
     * @return ResponseEntity con la información del pago completado.
     */
    @PostMapping("/checkout/completar/pago")
    public ResponseEntity<?> ejecutarCheckoutOperacion(@RequestBody CheckoutPayload checkoutPayload) {
        Map<String, Object> response = new HashMap<>();
        OperacionPagoInfo pagoInfo;

        try {
            pagoInfo = this.checkoutService.ejecutarCheckoutSuccess(checkoutPayload);
        } catch (OperacionException | PaymentException e) {
            response.put("mensaje", "Error al completar el pago para la operación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (pagoInfo == null) {
            response.put("mensaje", "Error al completar el pago para la operación");
            response.put("error", "El pago debe ser aprobado por el cliente antes de completarlo");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("pago", pagoInfo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}

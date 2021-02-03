package com.deofis.tiendaapirest.pagos.services.paypal;

import com.deofis.tiendaapirest.operaciones.domain.DetalleOperacion;
import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.pagos.PaymentException;
import com.deofis.tiendaapirest.pagos.dto.AmountPayload;
import com.deofis.tiendaapirest.pagos.dto.PayerPayload;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfoFactory;
import com.deofis.tiendaapirest.pagos.services.strategy.PagoStrategy;
import com.deofis.tiendaapirest.productos.domain.Sku;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementación de {@link com.deofis.tiendaapirest.pagos.services.strategy.PagoStrategy} que realiza el pago
 * conectandose con la API de PayPal, para crear la orden y capturarla al completar el checkout.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PayPalStrategy implements PagoStrategy {

    private static final String CURRENCY = "USD";
    private static final String INTENT = "CAPTURE";

    private final PayPalHttpClient client;
    private final String clientUrl;

    @Override
    public OperacionPagoInfo crearPago(Operacion operacion) {
        // Creamos la request con el body de acuerdo a la operación.
        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(this.buildOrderRequestBody(operacion));
        // Atributos para nuestra clase OperacionPagoInfo
        Map<String, Object> atributosPago = new HashMap<>();

        try {
            Order order;
            HttpResponse<Order> response = this.client.execute(request);
            String approveUrl = null;

            order = response.result();

            for (LinkDescription link: order.links()) {
                if (link.rel().equals("approve")) {
                    approveUrl = link.href();
                    break;
                }
            }

            order.links().forEach(link -> log.info(link.rel() + " => " + link.method() + ":" + link.href()));

            // Completamos los datos para nuestra clase de pago.
            atributosPago.put("orderId", order.id());
            atributosPago.put("nroOperacion", operacion.getNroOperacion());
            atributosPago.put("status", order.status());
            atributosPago.put("approveUrl", approveUrl);
            atributosPago.put("amount", null);
            atributosPago.put("payer", null);
        } catch (IOException e) {
            throw new PaymentException("Error al crear con paypal la orden de pago: " +
                    e.getMessage());
        }

        log.info("Pago creado con éxito");
        return OperacionPagoInfoFactory
                .getOperacionPagoInfo(String.valueOf(operacion.getMedioPago().getNombre()), atributosPago);
    }

    @Override
    public OperacionPagoInfo completarPago(Operacion operacion, String orderId, String preferenceId) {
        OrderRequest orderRequest = new OrderRequest();
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        request.requestBody(orderRequest);

        // Creamos mapa de atributos para nuestra clase de pago.
        Map<String, Object> atributosPago = new HashMap<>();
        Order order;
        try {
            HttpResponse<Order> response = this.client.execute(request);
            order = response.result();
        } catch (IOException e) {
            throw new PaymentException("Error al completar el pago con PayPal: " +
                    e.getMessage());
        }

        PayerPayload payer = PayerPayload.builder()
                .payerId(order.payer().payerId())
                .payerEmail(order.payer().email())
                .payerFullName(order.payer().name().givenName().concat(" ").concat(order.payer().name().surname()))
                .build();

        AmountPayload amount = AmountPayload.builder()
                .totalBruto(order.purchaseUnits().get(0).payments().captures().get(0)
                        .sellerReceivableBreakdown().grossAmount().value())
                .totalNeto(order.purchaseUnits().get(0).payments().captures().get(0)
                        .sellerReceivableBreakdown().netAmount().value())
                .fee(order.purchaseUnits().get(0).payments().captures().get(0)
                        .sellerReceivableBreakdown().paypalFee().value())
                .build();

        // Completamos los datos de los atributos para nuestro pago

        atributosPago.put("orderId", order.id());
        atributosPago.put("nroOperacion", operacion.getNroOperacion());
        atributosPago.put("status", order.status());
        atributosPago.put("payer", payer);
        atributosPago.put("amount", amount);

        log.info("Pago completado con éxito");
        return OperacionPagoInfoFactory
                .getOperacionPagoInfo(String.valueOf(operacion.getMedioPago().getNombre()), atributosPago);
    }

    /**
     * Método que se encarga de armar el body de la order, recibiendo la operación como parámetro para completar
     * los datos de la PayPal order.
     * @param operacion Operacion a crear pago.
     * @return OrderRequest creado.
     */
    private OrderRequest buildOrderRequestBody(Operacion operacion) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent(INTENT);

        // Creamos las URIs de redirección
        String targetCancelUrl = this.clientUrl.concat("/paypal/redirect/cancel");
        String CANCEL_REDIRECT_URL = UriComponentsBuilder.fromUriString(targetCancelUrl)
                .queryParam("nroOperacion", operacion.getNroOperacion()).build().toString();

        String targetApprovedUri = this.clientUrl.concat("/paypal/redirect/approved");
        String APPROVED_REDIRECT_URL = UriComponentsBuilder.fromUriString(targetApprovedUri)
                .queryParam("nroOperacion", operacion.getNroOperacion()).build().toString();

        ApplicationContext context = new ApplicationContext()
                .landingPage("BILLING")
                .cancelUrl(CANCEL_REDIRECT_URL).userAction("CANCEL")
                .returnUrl(APPROVED_REDIRECT_URL).userAction("CONTINUE");

        orderRequest.applicationContext(context);

        List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();

        PurchaseUnitRequest unitRequest = new PurchaseUnitRequest();

        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode(CURRENCY)
                .value(String.valueOf(operacion.getTotal()))
                .amountBreakdown(new AmountBreakdown()
                        .itemTotal(new Money().currencyCode(CURRENCY).value(String.valueOf(operacion.getTotal()))));

        List<Item> items = this.agregarItems(operacion);

        unitRequest.amountWithBreakdown(amount).items(items);

        purchaseUnitRequests.add(unitRequest);
        orderRequest.purchaseUnits(purchaseUnitRequests);

        return orderRequest;
    }

    private List<Item> agregarItems(Operacion operacion) {
        List<Item> items = new ArrayList<>();

        for (DetalleOperacion item: operacion.getItems()) {
            Sku sku = item.getSku();
            String descripcion = sku.getDescripcion();

            if (descripcion.length() > 100)
                descripcion = descripcion.substring(0, 100).replace("\n", "");

            log.info(descripcion);
            items.add(new Item()
                    .name(sku.getNombre())
                    .description(descripcion)
                    .unitAmount(new Money().currencyCode(CURRENCY).value(String.valueOf(item.getPrecioVenta())))
                    .quantity(String.valueOf(item.getCantidad()))
                    .category("PHYSICAL_GOODS"));
        }

        return items;
    }
}

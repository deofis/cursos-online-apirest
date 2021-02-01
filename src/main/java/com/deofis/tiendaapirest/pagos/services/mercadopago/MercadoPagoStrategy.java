package com.deofis.tiendaapirest.pagos.services.mercadopago;

import com.deofis.tiendaapirest.operaciones.domain.DetalleOperacion;
import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.pagos.PaymentException;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfoFactory;
import com.deofis.tiendaapirest.pagos.services.strategy.PagoStrategy;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.Preference;
import com.mercadopago.resources.datastructures.preference.BackUrls;
import com.mercadopago.resources.datastructures.preference.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class MercadoPagoStrategy implements PagoStrategy {

    private final String clientUrl;

    @Override
    public OperacionPagoInfo crearPago(Operacion operacion) {
        Preference preference = this.buildPreferenceBody(operacion);
        Map<String, Object> atributosPago = new HashMap<>();

        Preference response;
        try {
            response = preference.save();
            log.info("Approve url --> " + response.getInitPoint());

            atributosPago.put("preferenceId", response.getId());
            atributosPago.put("nroOperacion", operacion.getNroOperacion());
            atributosPago.put("status", "CREATED");
            atributosPago.put("initPoint", response.getInitPoint());
            atributosPago.put("amount", null);
            atributosPago.put("payer", null);
        } catch (MPException e) {
            throw new PaymentException("Error al crear el pago con Mercado Pago: " +
                    e.getMessage());
        }

        log.info("Pago con MP creado con éxito");
        return OperacionPagoInfoFactory
                .getOperacionPagoInfo(String.valueOf(operacion.getMedioPago().getNombre()), atributosPago);
    }

    @Override
    public OperacionPagoInfo completarPago(Operacion operacion) {
        return null;
    }

    private Preference buildPreferenceBody(Operacion operacion) {
        Preference preference = new Preference();

        String targetCancelUrl = this.clientUrl.concat("/mercado-pago/redirect/cancel");
        String CANCEL_REDIRECT_URL = UriComponentsBuilder.fromUriString(targetCancelUrl)
                .queryParam("nroOperacion", operacion.getNroOperacion()).build().toString();
        String targetSuccessUrl = this.clientUrl.concat("/mercado-pago/redirect/approved");
        String APPROVED_REDIRECT_URL = UriComponentsBuilder.fromUriString(targetSuccessUrl)
                .queryParam("nroOperacion", operacion.getNroOperacion()).build().toString();

        preference.setBackUrls(new BackUrls()
                .setFailure(CANCEL_REDIRECT_URL)
                .setSuccess(APPROVED_REDIRECT_URL));

        List<DetalleOperacion> items = operacion.getItems();

        // Creamos array list con los items de Mercado Pago, y por cada
        // item de la operación, lo añadimos al array list de items MP.
        ArrayList<Item> mpItems = new ArrayList<>();

        for (DetalleOperacion item: items) {
            Item itemMp = new Item();

            itemMp.setId(String.valueOf(item.getId()))
                    .setTitle(item.getSku().getNombre())
                    .setQuantity(item.getCantidad())
                    .setUnitPrice(item.getPrecioVenta().floatValue());
            mpItems.add(itemMp);
        }

        preference.setItems(mpItems);

        return preference;
    }
}

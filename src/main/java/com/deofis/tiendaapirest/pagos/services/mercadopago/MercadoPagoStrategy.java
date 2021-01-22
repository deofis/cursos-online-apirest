package com.deofis.tiendaapirest.pagos.services.mercadopago;

import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;
import com.deofis.tiendaapirest.pagos.services.strategy.PagoStrategy;
import com.mercadopago.resources.Preference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MercadoPagoStrategy implements PagoStrategy {

    private final String clientUrl;

    @Override
    public OperacionPagoInfo crearPago(Operacion operacion) {
        Preference preference = this.buildPreferenceBody(operacion);
        return null;
    }

    @Override
    public OperacionPagoInfo completarPago(Operacion operacion) {
        return null;
    }

    private Preference buildPreferenceBody(Operacion operacion) {
        Preference preference = new Preference();

        String targetCancelUrl = this.clientUrl.concat("/mercado-pago/redirect/");
        String targetSuccessUrl;

        return null;
    }
}

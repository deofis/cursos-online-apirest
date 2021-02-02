package com.deofis.tiendaapirest.pagos.services.cash;

import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.pagos.dto.AmountPayload;
import com.deofis.tiendaapirest.pagos.dto.PayerPayload;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfo;
import com.deofis.tiendaapirest.pagos.factory.OperacionPagoInfoFactory;
import com.deofis.tiendaapirest.pagos.services.strategy.PagoStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CashStrategy implements PagoStrategy {

    @Override
    public OperacionPagoInfo crearPago(Operacion operacion) {
        Map<String, Object> atributosPago = new HashMap<>();

        atributosPago.put("nroPago", UUID.randomUUID().toString());
        atributosPago.put("nroOperacion", operacion.getNroOperacion());
        atributosPago.put("estado", "CREADA");
        atributosPago.put("monto", null);
        atributosPago.put("pagador", null);

        log.info("Pago creado con éxito");
        return OperacionPagoInfoFactory
                .getOperacionPagoInfo(String.valueOf(operacion.getMedioPago().getNombre()), atributosPago);
    }

    @Override
    public OperacionPagoInfo completarPago(Operacion operacion, String pagoId) {
        Map<String, Object> atributosPago = new HashMap<>();

        PayerPayload payer = PayerPayload.builder()
                .payerId(String.valueOf(operacion.getCliente().getId()))
                .payerEmail(operacion.getCliente().getEmail())
                .payerFullName(operacion.getCliente().getNombre().concat(" ").concat(operacion.getCliente().getApellido()))
                .build();

        AmountPayload amount = AmountPayload.builder()
                .totalBruto(null)
                .totalNeto(String.valueOf(operacion.getTotal()))
                .fee(null)
                .build();

        atributosPago.put("nroPago", operacion.getPago().getId());
        atributosPago.put("nroOperacion", operacion.getNroOperacion());
        atributosPago.put("estado", "COMPLETED");
        atributosPago.put("monto", amount);
        atributosPago.put("pagador", payer);

        return OperacionPagoInfoFactory
                .getOperacionPagoInfo(String.valueOf(operacion.getMedioPago().getNombre()), atributosPago);
    }
}

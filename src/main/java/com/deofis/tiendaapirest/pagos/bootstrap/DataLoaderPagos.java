package com.deofis.tiendaapirest.pagos.bootstrap;

import com.deofis.tiendaapirest.pagos.domain.MedioPago;
import com.deofis.tiendaapirest.pagos.domain.MedioPagoEnum;
import com.deofis.tiendaapirest.pagos.repositories.MedioPagoRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class DataLoaderPagos implements CommandLineRunner {

    private final MedioPagoRepository medioPagoRepository;

    @Transactional
    @Override
    public void run(String... args) throws Exception {

        if (this.medioPagoRepository.findAll().size() == 0) {
            List<MedioPago> mediosPago = new ArrayList<>();

            MedioPago efectivo = new MedioPago();
            efectivo.setNombre(MedioPagoEnum.EFECTIVO);
            mediosPago.add(efectivo);

            MedioPago paypal = new MedioPago();
            paypal.setNombre(MedioPagoEnum.PAYPAL);
            mediosPago.add(paypal);

            MedioPago mercadoPago = new MedioPago();
            paypal.setNombre(MedioPagoEnum.MERCADO_PAGO);
            mediosPago.add(mercadoPago);

            this.medioPagoRepository.saveAll(mediosPago);
        }
    }
}

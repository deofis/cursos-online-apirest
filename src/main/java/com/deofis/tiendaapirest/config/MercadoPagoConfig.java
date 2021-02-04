package com.deofis.tiendaapirest.config;

import com.mercadopago.MercadoPago;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mercadopago")
@Data
public class MercadoPagoConfig {

    private String publicKey;
    private String accessKey;

    @Bean
    void setMercadoPagoAccessToken() throws Exception {
        if (this.publicKey == null || this.accessKey == null)
            throw new IllegalAccessException("MercadoPago access and public key must be provided");
        MercadoPago.SDK.setAccessToken(this.accessKey);
    }
}

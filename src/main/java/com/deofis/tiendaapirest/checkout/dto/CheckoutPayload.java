package com.deofis.tiendaapirest.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutPayload {
    private Long nroOperacion;
    private String paymentId;
    private String preferenceId;
}

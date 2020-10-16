package com.deofis.tiendaapirest.operaciones.dto.paypal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayerPayload {

    private String payerId;
    private String payerEmail;
    private String payerFullName;
}

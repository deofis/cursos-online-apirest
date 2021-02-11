package com.deofis.tiendaapirest.operaciones.dto;

import com.deofis.tiendaapirest.clientes.domain.Direccion;
import com.deofis.tiendaapirest.operaciones.domain.DetalleOperacion;
import com.deofis.tiendaapirest.pagos.domain.MedioPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperacionRequest {
    private Long nroOperacion;
    private DetalleOperacion item;
    private Direccion direccionEnvio;
    private MedioPago medioPago;
}

package com.deofis.tiendaapirest.operaciones.controllers;

import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.operaciones.exceptions.OperacionException;
import com.deofis.tiendaapirest.operaciones.services.OperacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RegistrarReciboController {

    private final OperacionService operacionService;

    /**
     * Registra que el pedido de una operación ha sido recibida por parte del usuario.
     * URL: ~/api/operaciones/recibir
     * HttpMethod: POST
     * HttpStatus: OK
     * @param nroOperacion @RequestParam Long con el numero de operacion a recibir.
     * @return ResponseEntity con la operacion y su nuevo estado.
     */
    @Secured("ROLE_ADMIN")
    @PostMapping("/operaciones/recibir")
    public ResponseEntity<?> registrarRecibo(@RequestParam Long nroOperacion) {
        Map<String, Object> response = new HashMap<>();
        Operacion operacionRecibida;

        try {
            operacionRecibida = this.operacionService.registrarRecibo(nroOperacion);
        } catch (OperacionException e) {
            response.put("mensaje", "Error al registrar el recibo de esta operación");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("operacionRecibida", operacionRecibida);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

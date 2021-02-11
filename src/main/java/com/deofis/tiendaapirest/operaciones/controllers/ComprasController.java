package com.deofis.tiendaapirest.operaciones.controllers;

import com.deofis.tiendaapirest.autenticacion.exceptions.AutenticacionException;
import com.deofis.tiendaapirest.clientes.exceptions.ClienteException;
import com.deofis.tiendaapirest.operaciones.domain.EstadoOperacion;
import com.deofis.tiendaapirest.operaciones.domain.Operacion;
import com.deofis.tiendaapirest.operaciones.exceptions.OperacionException;
import com.deofis.tiendaapirest.operaciones.services.CompraService;
import com.deofis.tiendaapirest.pagos.domain.OperacionPago;
import com.deofis.tiendaapirest.perfiles.exceptions.PerfilesException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ComprasController {

    private final CompraService compraService;

    /**
     * Ver el historial de compras del cliente del perfil actual.
     * URL: ~/api/perfiles/compras/ver
     * HttpMethod: GET
     * HttpStatus OK
     * @return ResponseEntity List con las operaciones del perfil.
     */
    @GetMapping("/perfil/compras/historial")
    public ResponseEntity<?> verHistorialCompras() {
        Map<String, Object> response = new HashMap<>();
        List<Operacion> compras;

        try {
            compras = this.compraService.historialCompras();
        } catch (PerfilesException | AutenticacionException | ClienteException | OperacionException e) {
            response.put("mensaje", "Error al obtener el historial de compras del perfil");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (compras.size() == 0) {
            response.put("error", "El historial de compras del perfil esta vacío");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("compras", compras);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene las compras del usuario logueado en el sistema de acuerdo a un estado requerido.
     * URL: ~/api/perfil/compras/estados
     * HttpMethod: GET
     * HttpStatus: OK
     * @param estado RequestParam String nombre del estado solicitado.
     * @return ResponseEntity con el listado de operaciones del estado requerido.
     */
    @GetMapping("/perfil/compras/estados")
    public ResponseEntity<?> comprasPorEstado(@RequestParam(name = "estado") String estado) {
        Map<String, Object> response = new HashMap<>();
        List<Operacion> comprasEstado;

        try {
            if (estado.equalsIgnoreCase(String.valueOf(EstadoOperacion.PAYMENT_PENDING)))
                comprasEstado = this.compraService.comprasPendientePago();
            else if (estado.equalsIgnoreCase(String.valueOf(EstadoOperacion.PAYMENT_DONE)))
                comprasEstado = this.compraService.comprasCompletadoPago();
            else if (estado.equalsIgnoreCase(String.valueOf(EstadoOperacion.SENT)))
                comprasEstado = this.compraService.comprasEnviadas();
            else if (estado.equalsIgnoreCase(String.valueOf(EstadoOperacion.RECEIVED)))
                comprasEstado = this.compraService.comprasRecibidas();
            else if (estado.equalsIgnoreCase(String.valueOf(EstadoOperacion.CANCELLED)))
                comprasEstado = this.compraService.comprasCanceladas();
            else comprasEstado = null;
        } catch (PerfilesException | AutenticacionException | ClienteException | OperacionException e) {
            response.put("mensaje", "Error al obtener las compras por estado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (comprasEstado == null) {
            response.put("mensaje", "Error al obtener las compras por estado");
            response.put("error", "El estado solicitado no existe o ha sido tipeado de manera equivocada");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("compras", comprasEstado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene las compras del usuario logueado en el sistema que pertenecen a un año requerido.
     * URL: ~/api/perfil/compras/year
     * HttpMethod: GET
     * HttpStatus: OK
     * @param year RequestParam Integer año solicitado.
     * @return ResponseEntity listado de operaciones.
     */
    @GetMapping("/perfil/compras/year")
    public ResponseEntity<?> comprasPorYear(@RequestParam(name = "year") Integer year) {
        Map<String, Object> response = new HashMap<>();
        List<Operacion> comprasYear;

        try {
            comprasYear = this.compraService.comprasYear(year);
        } catch (PerfilesException | AutenticacionException | ClienteException | OperacionException e) {
            response.put("mensaje", "Error al obtener las compras por año");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("compras", comprasYear);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene las compras del usuario logueado en el sistema que pertenecen a un mes requerido. en el
     * año actual.
     * URL: ~/api/perfil/compras/month
     * HttpMethod: GET
     * HttpStatus: OK
     * @param month RequestParam Integer mes solicitado.
     * @return ResponseEntity listado de operaciones.
     */
    @GetMapping("/perfil/compras/month")
    public ResponseEntity<?> comprasPorMonth(@RequestParam(name = "month") Integer month) {
        Map<String, Object> response = new HashMap<>();
        List<Operacion> comprasMonth;

        try {
            comprasMonth = this.compraService.comprasMonth(month);
        } catch (PerfilesException | AutenticacionException | ClienteException | OperacionException e) {
            response.put("mensaje", "Error al obtener las compras por mes");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("compras", comprasMonth);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Ver una compra en particular.
     * URL: ~/api/perfiles/compras/1
     * HttpMethod: GET
     * HttpStatus: OK
     * @param nroOperacion @PathVariable Long numero de operación.
     * @return ResponseEntity Operacion seleccionada.
     */
    @GetMapping("/perfil/compras/{nroOperacion}")
    public ResponseEntity<?> verCompra(@PathVariable Long nroOperacion) {
        Map<String, Object> response = new HashMap<>();
        Operacion compra;

        try {
            compra = this.compraService.verCompra(nroOperacion);
        } catch (PerfilesException | AutenticacionException | ClienteException | OperacionException e) {
            response.put("mensaje", "Error al obtener la compra");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("compra", compra);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/perfil/compras/{nroOperacion}/pago")
    public ResponseEntity<OperacionPago> obtenerPagoCompra(@PathVariable Long nroOperacion) {
        return new ResponseEntity<>(this.compraService.obtenerPagoCompra(nroOperacion), HttpStatus.OK);
    }
}

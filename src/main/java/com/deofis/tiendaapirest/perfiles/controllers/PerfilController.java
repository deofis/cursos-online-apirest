package com.deofis.tiendaapirest.perfiles.controllers;

import com.deofis.tiendaapirest.autenticacion.exceptions.AutenticacionException;
import com.deofis.tiendaapirest.clientes.domain.Cliente;
import com.deofis.tiendaapirest.clientes.domain.Direccion;
import com.deofis.tiendaapirest.clientes.exceptions.ClienteException;
import com.deofis.tiendaapirest.perfiles.domain.Carrito;
import com.deofis.tiendaapirest.perfiles.domain.Favorito;
import com.deofis.tiendaapirest.perfiles.dto.PerfilDTO;
import com.deofis.tiendaapirest.perfiles.exceptions.CarritoException;
import com.deofis.tiendaapirest.perfiles.exceptions.PerfilesException;
import com.deofis.tiendaapirest.perfiles.services.PerfilService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API dirigida a la administración de perfiles, usuarios y sus datos para transacciones dentro
 * del sistema. Rol asignado para manejo de perfiles: 'ROLE_USER'. (*ver: admins tambien pueden, o tienen
 * que crearse un usuario para realizar compras en el sistema?).
 */

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    /*
    @PostMapping("/perfil/cargar-cliente")
    public ResponseEntity<?> cargarDatos(@Valid @RequestBody Cliente cliente, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        PerfilDTO perfil;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("error", "Bad Request");
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            perfil = this.perfilService.cargarPerfil(cliente);
        } catch (ClienteException | AutenticacionException | PerfilesException e) {
            response.put("mensaje", "Error al cargar datos al perfil");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Datos de cliente cargados exitosamente en el perfil");
        response.put("perfil", perfil);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

     */


    /**
     * Como usuario, quiero actualizar mis datos de cliente en mi perfil.
     * URL: ~/api/perfil/cliente/actualizar
     * HttpMethod: PUT
     * HttpStatus: CREATED
     * @param cliente Cliente actualizado.
     * @return ResponseEntity Perfil con los datos del cliente actualizados.
     */
    @PutMapping("/perfil/cliente/actualizar")
    public ResponseEntity<?> actualizarDatos(@Valid @RequestBody Cliente cliente, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        PerfilDTO perfil;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.toList());
            response.put("error", "Bad Request");
            response.put("errors", errors);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            perfil = this.perfilService.actualizarPerfil(cliente);
        } catch (ClienteException | AutenticacionException | PerfilesException e) {
            response.put("mensaje", "Error al actualizar datos del cliente");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("mensaje", "Datos actualizados con éxito.");
        response.put("perfil", perfil);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Obtiene los datos del cliente del usuario logueado.
     * URL: ~/api/perfil/cliente
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity Cliente con los datos del cliente.
     */
    @GetMapping("/perfil/cliente")
    public ResponseEntity<?> obtenerCliente() {
        Map<String, Object> response = new HashMap<>();
        Cliente cliente;

        try {
            cliente = this.perfilService.obtenerDatosCliente();
        } catch (ClienteException | PerfilesException | AutenticacionException e) {
            response.put("mensaje", "Error al obtener los datos del cliente para el usuario logueado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("cliente", cliente);
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    /**
     * API que actualiza la dirección de un cliente. Los datos del cliente se obtienen del perfil
     * del usuario logueado en el sistema.
     * URL: ~/api/perfil/cliente/direccion
     * HttpMethod: PUT
     * HttpStatus: OK
     * @param direccion {@link Direccion} dirección del cliente actualizada.
     * @return ResponseEntity con el {@link Cliente} actualizado.
     */
    @PutMapping("/perfil/cliente/direccion")
    public ResponseEntity<?> actualizarDireccionCliente(@Valid @RequestBody Direccion direccion) {
        Map<String, Object> response = new HashMap<>();
        Cliente clienteActualizado;

        try {
            clienteActualizado = this.perfilService.actualizarDireccionCliente(direccion);
        } catch (PerfilesException | ClienteException e) {
            response.put("mensaje", "Error al actualizar la dirección del cliente");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("cliente", clienteActualizado);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Permite ver el perfil completo del usuario logueado.
     * URL: ~/api/perfil
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity PerfilDTO datos del perfil.
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfil() {
        Map<String, Object> response = new HashMap<>();
        PerfilDTO perfil;

        try {
            perfil = this.perfilService.verPerfil();
        } catch (PerfilesException | AutenticacionException e) {
            response.put("mensaje", "Error al obtener el perfil del usuario logueado");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("perfil", perfil);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Ver el carrito del perfil del usuario actual.
     * URL: ~/api/perfil/carrito
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity Carrito del perfil.
     */
    @GetMapping("/perfil/carrito")
    public ResponseEntity<?> verCarrito() {
        Map<String, Object> response = new HashMap<>();
        Carrito carrito;

        try {
            carrito = this.perfilService.obtenerCarrito();
        } catch (PerfilesException | CarritoException | AutenticacionException e) {
            response.put("mensaje", "Error al obtener el carrito del perfil");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("carrito", carrito);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene el listado de productos favoritos pertenecientes al perfil actual.
     * URL: ~/api/perfil/favoritos
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity Favoritos.
     */
    @GetMapping("/perfil/favoritos")
    public ResponseEntity<?> verFavoritos() {
        Map<String, Object> response = new HashMap<>();
        Favorito favorito;

        try {
            favorito = this.perfilService.obtenerFavoritos();
        } catch (PerfilesException | AutenticacionException e) {
            response.put("mensaje", "Error al obtener los favoritos del perfil");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("favoritos", favorito);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

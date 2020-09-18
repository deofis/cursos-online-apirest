package com.deofis.tiendaapirest.productos.controllers.producto;

import com.deofis.tiendaapirest.productos.domain.Producto;
import com.deofis.tiendaapirest.productos.domain.UnidadMedida;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.services.ProductoService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador que se encarga de obtener los productos de distintas formas.
 */

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ObtenerProductosController {

    private final ProductoService productoService;

    /**
     * Obtiene los productos ordenados alfabéticamente.
     * URL: ~/api/productos
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity con un listado de todos los productos.
     */
    @GetMapping("/productos")
    public ResponseEntity<?> obtenerProductos() {
        Map<String, Object> response = new HashMap<>();
        List<Producto> productos;

        try {
            productos = this.productoService.obtenerProductos();
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener los productos de la Base de Datos");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (productos.size() == 0) {
            response.put("error", "No existen productos registrados en la Base de Datos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    /**
     * Obtiene un producto específico.
     * URL: http://localhost:8080/api/productos/ver/1
     * HttpStatus: OK
     * HttpMethod: GET
     * @param id PathVarible Long con el id solicitado.
     * @return ResponseEntity con el Producto.
     */
    @GetMapping("/productos/ver/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        Producto producto;

        try {
            producto = this.productoService.obtenerProducto(id);
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener el producto");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(producto, HttpStatus.OK);
    }

    /**
     * Obtiene listado de las unidades de medida.
     * URL: ~/api/productos/unidades-medida
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity con el listado de unidades de medida.
     */
    @GetMapping("/productos/unidades-medida")
    public ResponseEntity<?> obtenerUnidadesMedida() {
        Map<String, Object> response = new HashMap<>();
        List<UnidadMedida> unidadesMedida;

        try {
            unidadesMedida = this.productoService.obtenerUnidadesMedida();
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener las unidades de medida");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(unidadesMedida, HttpStatus.OK);
    }
}

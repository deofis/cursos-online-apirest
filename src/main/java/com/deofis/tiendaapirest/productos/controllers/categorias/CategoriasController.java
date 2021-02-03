package com.deofis.tiendaapirest.productos.controllers.categorias;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.deofis.tiendaapirest.productos.domain.Categoria;
import com.deofis.tiendaapirest.productos.domain.Imagen;
import com.deofis.tiendaapirest.productos.domain.PropiedadProducto;
import com.deofis.tiendaapirest.productos.domain.Subcategoria;
import com.deofis.tiendaapirest.productos.exceptions.CategoriaException;
import com.deofis.tiendaapirest.productos.exceptions.FileException;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.services.CategoriaService;
import com.deofis.tiendaapirest.productos.services.SubcategoriaService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador que se encarga de listar y obtener las categorías y subcategorías, así
 * como las propiedades de las subcategorías.
 */

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CategoriasController {

    private final CategoriaService categoriaService;
    private final SubcategoriaService subcategoriaService;

    /**
     * Obtener un todas las categorias ordenadas por nombre.
     * URL: ~/api/categorias
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity con todas las categorias.
     */
    @GetMapping("/categorias")
    public ResponseEntity<?> obtenerCategorias() {
        Map<String, Object> response = new HashMap<>();
        List<Categoria> categorias;

        try {
            categorias = this.categoriaService.obtenerCategorias();
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener el listado de categorias");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (categorias.size() == 0) {
            response.put("mensaje", "No existen categorías en la base de datos");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("categorias", categorias);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtener una categoría específica.
     * URL: ~/api/categorias/ver/1
     * HttpMethod: GET
     * HttpStatus: OK
     * @param categoriaId PathVariable Long del id de la categoría a obtener.
     * @return ResponseEntity con la categoría.
     */
    @GetMapping("/categorias/{categoriaId}")
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long categoriaId) {
        Map<String, Object> response = new HashMap<>();
        Categoria categoria;

        try {
            categoria = this.categoriaService.obtenerCategoria(categoriaId);
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener la categoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("categoria", categoria);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene el todas las subcategorias que pertenecen a la categoria solicitada.
     * URL: ~/api/categorias/1/subcategorias
     * HttpMethod: GET
     * HttpStatus: OK
     * @param categoriaId PathVariable Long con el id de la categoria a consultar sus subcategorias.
     * @return ResponseEntity con las subcategorias.
     */
    @GetMapping("/categorias/{categoriaId}/subcategorias")
    public ResponseEntity<?> obtenerSubcategoriasDeCategoria(@PathVariable Long categoriaId) {
        Map<String, Object> response = new HashMap<>();
        List<Subcategoria> subcategorias;
        String categoria;

        try {
            subcategorias = this.categoriaService.obtenerSubcategorias(categoriaId);
            categoria = this.categoriaService.obtenerCategoria(categoriaId).getNombre();
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener las subcategorias de la categoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("categoria", categoria);
        response.put("subcategorias", subcategorias);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene una {@link Subcategoria} requerida, que pertenece a una {@link Categoria} requerida.
     * URL: ~/api/categorias/1/subcategorias/1
     * HttpMethod: GET
     * HttpStatus: OK
     * @param categoriaId PathVariable Long id de la categoría a la que pertenece la subcategoría.
     * @param subcategoriaId PathVariable Long id de la subcategoría.
     * @return ResponseEntity con la subcategoría y el nombre de la categoría.
     */
    @GetMapping("/categorias/{categoriaId}/subcategorias/{subcategoriaId}")
    public ResponseEntity<?> obtenerSubcategoriaDeCategoria(@PathVariable Long categoriaId,
                                                            @PathVariable Long subcategoriaId) {

        Map<String, Object> response = new HashMap<>();
        Subcategoria subcategoria;
        String categoria;

        try {
            subcategoria = this.categoriaService.obtenerSubcategoriaDeCategoria(categoriaId, subcategoriaId);
            categoria = this.categoriaService.obtenerCategoria(categoriaId).getNombre();
        } catch (ProductoException | CategoriaException e) {
            response.put("mensaje", "Error al obtener la subcategoría de la categoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("categoria", categoria);
        response.put("subcategoria", subcategoria);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene todas las subcategorias.
     * URL: ~/api/subcategorias
     * HttpMethod: GET
     * HttpStatus: OK
     * @return ResponseEntity con las subcategorias.
     */
    @GetMapping("/subcategorias")
    public ResponseEntity<?> obtenerSubcategorias() {
        Map<String, Object> response = new HashMap<>();
        List<Subcategoria> subcategorias;

        try {
            subcategorias = this.subcategoriaService.obtenerSubcategorias();
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener las subcategorias");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("subcategorias", subcategorias);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene una subcategoria especifica.
     * URL: ~/api/subcategorias/1
     * HttpMethod: GET
     * HttpStatus: OK
     * @param subcategoriaId Long id de la subcategoria requerida.
     * @return ResponsEntity con la subcategoria.
     */
    @GetMapping("/subcategorias/{subcategoriaId}")
    public ResponseEntity<?> obtenerSubcategoria(@PathVariable Long subcategoriaId) {
        Map<String, Object> response = new HashMap<>();
        Subcategoria subcategoria;

        try {
            subcategoria = this.subcategoriaService.obtenerSubcategoria(subcategoriaId);
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener la subcategoria");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("subcategoria", subcategoria);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Obtiene una lista con todas las propiedades de una subcategoría requerida.
     * URL: ~/api/subcategorias/1/propiedades
     * HttpMethod: GET
     * HttpStatus: OK
     * @param subcategoriaId Long id de la subcategoria requerida.
     * @return ResponseEntity con el nombre de la subcategoria y la lista de propiedades obtenidas.
     */
    @GetMapping("/subcategorias/{subcategoriaId}/propiedades")
    public ResponseEntity<?> obtenerPropiedadesSubcategoria(@PathVariable Long subcategoriaId) {
        Map<String, Object> response = new HashMap<>();
        List<PropiedadProducto> propiedades;
        String subcategoria;

        try {
            propiedades = this.subcategoriaService.obtenerPropiedadesSubcategoria(subcategoriaId);
            subcategoria = this.subcategoriaService.obtenerSubcategoria(subcategoriaId).getNombre();
        } catch (ProductoException e) {
            response.put("mensaje", "Error al obtener las propiedades de la subcategoria");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("subcategoria", subcategoria);
        response.put("propiedades", propiedades);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Sube y asocia foto a una {@link Categoria} requerida.
     * URL: ~/api/categorias/1/fotos
     * HttpMethod: POST
     * HttpStatus: CREATED
     * @param categoriaId PathVariable Long id de la categoría.
     * @param foto RequestParam MultipartFile archivo que contiene la foto de categoría a crear y subir.
     * @return ResponseEntity {@link Imagen} con los datos de la foto creada y subida.
     */
    @PostMapping("/categorias/{categoriaId}/fotos")
    public ResponseEntity<?> subirFotoCategoria(@PathVariable Long categoriaId,
                                                @RequestParam(name = "foto")MultipartFile foto) {
        Map<String, Object> response = new HashMap<>();
        Imagen fotoCategoria;

        try {
            fotoCategoria = this.categoriaService.subirFotoCategoria(categoriaId, foto);
        } catch (CategoriaException | FileException | AmazonS3Exception e) {
            response.put("mensaje", "Error al subir foto de categoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("foto", fotoCategoria);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Sube y asocia foto a una {@link Subcategoria} requerida.
     * URL: ~/api/subcategorias/1/fotos
     * HttpMethod: POST
     * HttpStatus: CREATED
     * @param subcategoriaId PathVariable Long id de la subcategoría.
     * @param foto RequestParam MultipartFile archivo que contiene la foto de subcategoría a crear y subir.
     * @return ResponseEntity {@link Imagen} con los datos de la foto creada y subida.
     */
    @PostMapping("/subcategorias/{subcategoriaId}/fotos")
    public ResponseEntity<?> subirFotoSubcategoria(@PathVariable Long subcategoriaId,
                                                   @RequestParam(name = "foto")MultipartFile foto) {
        Map<String, Object> response = new HashMap<>();
        Imagen fotoSubcategoria;

        try {
            fotoSubcategoria = this.subcategoriaService.subirFotoSubcategoria(subcategoriaId, foto);
        } catch (CategoriaException | FileException | AmazonS3Exception e) {
            response.put("mensaje", "Error al subir foto de subcategoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("foto", fotoSubcategoria);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Descarga y obtiene la foto asociada a una {@link Categoria}.
     * URL: ~/api/categorias/1/fotos
     * HttpMethod: GET
     * HttpStatus: OK
     * @param categoriaId PathVariable Long id de la categoría a obtener foto.
     * @return ResponseEntity con la imagen como recurso.
     */
    @GetMapping("/categorias/{categoriaId}/fotos")
    public ResponseEntity<?> obtenerFotoCategoria(@PathVariable Long categoriaId) {
        Map<String, Object> response = new HashMap<>();
        byte[] fotoBytes;
        ByteArrayResource fotoAsResource;
        String imagePath;

        try {
            fotoBytes = this.categoriaService.obtenerFotoCategoria(categoriaId);
            imagePath = this.categoriaService.obtenerPathFotoCategoria(categoriaId);
            fotoAsResource = new ByteArrayResource(fotoBytes);
        } catch (CategoriaException | FileException | AmazonS3Exception e) {
            response.put("mensaje", "Error al obtener foto de la categoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity
                .ok()
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + imagePath + "\"")
                .body(fotoAsResource);
    }

    /**
     * Descarga y obtiene la foto asociada a una {@link Subcategoria}.
     * URL: ~/api/subcategorias/1/fotos
     * HttpMethod: GET
     * HttpStatus: OK
     * @param subcategoriaId PathVariable Long id de la subcategoría a obtener foto.
     * @return ResponseEntity con la imagen como recurso.
     */
    @GetMapping("/subcategorias/{subcategoriaId}/fotos")
    public ResponseEntity<?> obtenerFotoSubcategoria(@PathVariable Long subcategoriaId) {
        Map<String, Object> response = new HashMap<>();
        byte[] fotoBytes;
        ByteArrayResource fotoAsResource;
        String imagePath;

        try {
            fotoBytes = this.subcategoriaService.obtenerFotoSubcategoria(subcategoriaId);
            imagePath = this.subcategoriaService.obtenerPathFotoSubcategoria(subcategoriaId);
            fotoAsResource = new ByteArrayResource(fotoBytes);
        } catch (CategoriaException | FileException | AmazonS3Exception e) {
            response.put("mensaje", "Error al obtener foto de la subcategoría");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity
                .ok()
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + imagePath + "\"")
                .body(fotoAsResource);
    }

    /**
     * Elimina la foto asociada a un {@link Categoria}.
     * URL: ~/api/categorias/1/fotos
     * HttpMethod: DELETE
     * HttpStatus: OK
     * @param categoriaId PathVariable Long id de la categoría a eliminar foto.
     * @return ResponseEntity con mensaje éxito/error sobre la eliminación de la foto de la categoría.
     */
    @DeleteMapping("/categorias/{categoriaId}/fotos")
    public ResponseEntity<?> eliminarFotoCategoria(@PathVariable Long categoriaId) {
        Map<String, Object> respose = new HashMap<>();
        String msg;

        try {
            this.categoriaService.eliminarFotoCategoria(categoriaId);
            msg = "Foto de categoría eliminada con éxito";
        } catch (CategoriaException | FileException | AmazonS3Exception e) {
            respose.put("mensaje", "Error al eliminar foto de categoría");
            respose.put("error", e.getMessage());
            return new ResponseEntity<>(respose, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        respose.put("mensaje", msg);
        return new ResponseEntity<>(respose, HttpStatus.OK);
    }

    /**
     * Elimina la foto asociada a un {@link Subcategoria}.
     * URL: ~/api/subcategorias/1/fotos
     * HttpMethod: DELETE
     * HttpStatus: OK
     * @param subcategoriaId PathVariable Long id de la subcategoría a eliminar foto.
     * @return ResponseEntity con mensaje éxito/error sobre la eliminación de la foto de la subcategoría.
     */
    @DeleteMapping("/subcategorias/{subcategoriaId}/fotos")
    public ResponseEntity<?> eliminarFotoSubcategoria(@PathVariable Long subcategoriaId) {
        Map<String, Object> respose = new HashMap<>();
        String msg;

        try {
            this.subcategoriaService.eliminarFotoSubcategoria(subcategoriaId);
            msg = "Foto de subcategoría eliminada con éxito";
        } catch (CategoriaException | FileException | AmazonS3Exception e) {
            respose.put("mensaje", "Error al eliminar foto de subcategoría");
            respose.put("error", e.getMessage());
            return new ResponseEntity<>(respose, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        respose.put("mensaje", msg);
        return new ResponseEntity<>(respose, HttpStatus.OK);
    }

    /**
     * API que obtiene la {@link Categoria} a la que pertenece una {@link Subcategoria}.
     * URL: ~/api/categorias/por-subcategoria/1
     * HttpMethod: GET
     * HttpStatus: OK
     * @param subcategoriaId Long id de la subcategoría a obtener la categoría a la que pertenece.
     * @return ResponseEntity con la {@link Categoria} encontrada.
     */
    @GetMapping("/categorias/por-subcategoria/{subcategoriaId}")
    public ResponseEntity<?> encontrarCategoriaPorSubcategoria(@PathVariable Long subcategoriaId) {
        return null;
    }
}

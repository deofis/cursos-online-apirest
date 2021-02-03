package com.deofis.tiendaapirest.productos.services;

import com.deofis.tiendaapirest.productos.domain.*;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.repositories.ProductoRepository;
import com.deofis.tiendaapirest.productos.repositories.SkuRepository;
import com.deofis.tiendaapirest.productos.repositories.SubcategoriaRepository;
import com.deofis.tiendaapirest.productos.services.images.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class CatalogoAdminServiceImpl implements CatalogoAdminService {

    private final ProductoService productoService;
    private final ProductoRepository productoRepository;
    private final SkuService skuService;
    private final SkuRepository skuRepository;
    private final PropiedadProductoService propiedadProductoService;
    private final SubcategoriaService subcategoriaService;
    private final SubcategoriaRepository subcategoriaRepository;
    private final ImageService imageService;

    @Transactional
    @Override
    public Producto crearProducto(Producto producto) {
        return this.productoService.crearProducto(producto);
    }

    @Transactional
    @Override
    public Imagen subirFotoPpalProducto(Long productoId, MultipartFile foto) {
        Producto producto = this.productoService.obtenerProducto(productoId);

        if (producto.getFoto() != null) {
            this.eliminarFotoPpalProducto(producto.getId());
        }

        Imagen fotoProducto = this.imageService.subirImagen(foto);
        producto.setFoto(fotoProducto);
        producto.getDefaultSku().setFoto(fotoProducto);
        this.productoRepository.save(producto);

        return fotoProducto;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] obtenerFotoPpalProducto(Long productoId) {
        Producto producto = this.productoService.obtenerProducto(productoId);
        Imagen fotoProducto = producto.getFoto();

        if (fotoProducto == null) throw new ProductoException("El producto con id: " + productoId + " no tiene foto principal");

        return this.imageService.descargarImagen(fotoProducto);
    }

    @Transactional(readOnly = true)
    @Override
    public String obtenerPathFotoPpalProducto(Long productoId) {
        Producto producto = this.productoService.obtenerProducto(productoId);
        return producto.getFoto().getPath();
    }

    @Transactional
    @Override
    public void eliminarFotoPpalProducto(Long productoId) {
        Producto producto = this.productoService.obtenerProducto(productoId);

        if (producto.getFoto() == null) throw new  ProductoException("El producto con id: " + productoId + " no tiene foto principal");

        Imagen fotoProducto = producto.getFoto();
        producto.setFoto(null);
        producto.getDefaultSku().setFoto(null);
        this.productoRepository.save(producto);
        this.imageService.eliminarImagen(fotoProducto);
    }

    @Override
    public void cancelarCreacionProducto(Long productoId) {
        this.productoService.deleteById(productoId);
    }

    @Transactional
    @Override
    public Sku crearSku(Long productoId, Sku sku) {
        Producto producto = this.productoService.obtenerProducto(productoId);

        return this.skuService.crearNuevoSku(sku, producto);
    }

    @Transactional
    @Override
    public Imagen subirFotoSku(Long skuId, MultipartFile foto) {
        Sku sku = this.skuService.obtenerSku(skuId);

        if (sku.getFoto() != null) {
            this.eliminarFotoSku(sku.getId());
        }

        Imagen fotoSku = this.imageService.subirImagen(foto);
        sku.setFoto(fotoSku);
        this.skuRepository.save(sku);

        return fotoSku;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] obtenerFotoSku(Long skuId) {
        Sku sku = this.skuService.obtenerSku(skuId);
        Imagen fotoSku = sku.getFoto();

        if (fotoSku == null) throw new ProductoException("El sku con id: " + skuId
                + "notiene foto");

        return this.imageService.descargarImagen(fotoSku);
    }

    @Transactional(readOnly = true)
    @Override
    public String obtenerPathFotoSku(Long skuId) {
        Sku sku = this.skuService.obtenerSku(skuId);
        return sku.getFoto().getPath();
    }

    @Transactional
    @Override
    public void eliminarFotoSku(Long skuId) {
        Sku sku = this.skuService.obtenerSku(skuId);

        if (sku.getFoto() == null) throw new  ProductoException("El sku con id: " + skuId
                + " no tiene foto");

        Imagen fotoProducto = sku.getFoto();
        sku.setFoto(null);
        this.skuRepository.save(sku);
        this.imageService.eliminarImagen(fotoProducto);
    }

    @Transactional
    @Override
    public void eliminarSku(Long skuId) {
        this.skuService.eliminarSku(skuId);
    }

    @Transactional
    @Override
    public void eliminarSkusProducto(Long productoId) {
        Producto producto = this.productoService.obtenerProducto(productoId);

        producto.getSkus().clear();

        this.productoRepository.save(producto);
    }

    @Transactional
    @Override
    public PropiedadProducto crearPropiedadProducto(PropiedadProducto propiedad) {
        return this.propiedadProductoService.crearPropiedadProducto(propiedad);
    }

    @Transactional
    @Override
    public PropiedadProducto crearPropiedadProducto(Long productoId, PropiedadProducto propiedad) {
        Producto producto = this.productoService.obtenerProducto(productoId);
        PropiedadProducto propiedadNueva = this.propiedadProductoService.crearPropiedadProducto(propiedad);

        producto.getPropiedades().add(propiedadNueva);
        producto.getSubcategoria().getPropiedades().add(propiedadNueva);
        this.productoRepository.save(producto);

        return propiedadNueva;
    }

    @Transactional
    @Override
    public PropiedadProducto crearPropiedadProductoSubcategoria(Long subcategoriaId, PropiedadProducto propiedad) {
        Subcategoria subcategoria = this.subcategoriaService.obtenerSubcategoria(subcategoriaId);

        PropiedadProducto propiedadNueva = this.propiedadProductoService.crearPropiedadProducto(propiedad);

        subcategoria.getPropiedades().add(propiedadNueva);
        this.subcategoriaRepository.save(subcategoria);
        return propiedadNueva;
    }

    @Override
    public PropiedadProducto actualizarPropiedadProducto(Long propiedadId, PropiedadProducto propiedadActualizada) {
        return this.propiedadProductoService.actualizarPropiedadProducto(propiedadId, propiedadActualizada);
    }

    @Transactional
    @Override
    public void eliminarPropiedadProducto(Long propiedadId) {
        this.propiedadProductoService.eliminarPropiedadProducto(propiedadId);
    }

    @Transactional
    @Override
    public PropiedadProducto crearValorPropiedad(Long propiedadId, ValorPropiedadProducto valor) {
        return this.propiedadProductoService.crearValorPropiedad(propiedadId, valor);
    }

    @Override
    public PropiedadProducto actualizarValorPropiedad(Long propiedadId, Long valorId, ValorPropiedadProducto valor) {
        return this.propiedadProductoService.actualizarValorPropiedad(propiedadId, valorId, valor);
    }

    @Override
    public void eliminarValorPropiedad(Long propiedadId, Long valorId) {
        this.propiedadProductoService.eliminarValorPropiedad(propiedadId, valorId);
    }

    @Transactional
    @Override
    public Map<String, Object> generarSkusProducto(Long productoId) {
        Producto producto = this.productoService.obtenerProducto(productoId);
        return this.skuService.generarSkusProducto(producto);
    }

    @Transactional
    @Override
    public void asignarPropiedadAProducto(Long productoId, Long propiedadId) {
        Producto producto = this.productoService.obtenerProducto(productoId);
        PropiedadProducto propiedad = this.propiedadProductoService.obtenerPropiedadProducto(propiedadId);

        if (this.validarPropiedad(producto.getSubcategoria(), propiedad)) throw new ProductoException("La propiedad: ".concat(propiedad.getNombre()).concat(" " +
                "no pertenece a la subcategoría: ".concat(producto.getSubcategoria().getNombre())));

        if (!this.validarPropiedad(producto, propiedad)) throw new ProductoException("La propiedad: "
                .concat(propiedad.getNombre()).concat(" ya está asignada al producto: " )
                .concat(producto.getNombre()));

        producto.getPropiedades().add(propiedad);
        this.productoRepository.save(producto);
    }

    @Transactional
    @Override
    public void asignarPropiedadASubcategoria(Long subcategoriaId, Long propiedadId) {
        Subcategoria subcategoria = this.subcategoriaService.obtenerSubcategoria(subcategoriaId);
        PropiedadProducto propiedad = this.propiedadProductoService.obtenerPropiedadProducto(propiedadId);

        if (!this.validarPropiedad(subcategoria, propiedad)) throw new ProductoException("La propiedad: "
                .concat(propiedad.getNombre()).concat(" ya está asignada a la subcategoría: "
                + subcategoria.getNombre()));

        subcategoria.getPropiedades().add(propiedad);
        this.subcategoriaRepository.save(subcategoria);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PropiedadProducto> obtenerPropiedadesProducto() {
        return this.propiedadProductoService.obtenerPropiedadesProducto();
    }

    @Transactional(readOnly = true)
    @Override
    public PropiedadProducto obtenerPropiedadProducto(Long propiedadId) {
        return this.propiedadProductoService.obtenerPropiedadProducto(propiedadId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ValorPropiedadProducto> obtenerValoresDePropiedad(Long propiedadId) {
        return this.propiedadProductoService.obtenerValoresDePropiedad(propiedadId);
    }

    @Override
    public Categoria obtenerCategoriaPorSubcategoria(Long subcategoriaId) {
        return null;
    }

    private boolean validarPropiedad(Subcategoria subcategoria, PropiedadProducto propiedad) {
        return !subcategoria.getPropiedades().contains(propiedad);
    }

    private boolean validarPropiedad(Producto producto, PropiedadProducto propiedad) {
        return !producto.getPropiedades().contains(propiedad);
    }
}

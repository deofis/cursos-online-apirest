package com.deofis.tiendaapirest.productos.services;

import com.deofis.tiendaapirest.globalservices.RoundService;
import com.deofis.tiendaapirest.productos.domain.*;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.repositories.ProductoRepository;
import com.deofis.tiendaapirest.productos.repositories.UnidadMedidaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    private final RoundService roundService;

    @Override
    @Transactional
    public Producto crearProducto(Producto producto) {
        List<PropiedadProducto> propiedades = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(producto.getPropiedades())) propiedades.addAll(producto.getPropiedades());

        if (producto.getDisponibilidadGeneral() < 0)
            throw new ProductoException("La disponibilidad no puede ser menor a 0");

        Producto nuevoProducto = Producto.builder()
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(this.roundService.truncate(producto.getPrecio()))
                .fechaCreacion(new Date())
                .foto(null)
                .imagenes(new ArrayList<>())
                .activo(true)
                .destacado(producto.isDestacado())
                .subcategoria(producto.getSubcategoria())
                .marca(producto.getMarca())
                .disponibilidadGeneral(producto.getDisponibilidadGeneral())
                .unidadMedida(producto.getUnidadMedida())
                .propiedades(propiedades)
                .skus(new ArrayList<>())
                .build();

        nuevoProducto.setDefaultSku(Sku.builder()
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .disponibilidad(producto.getDisponibilidadGeneral())
                .fechaCreacion(new Date())
                .foto(null)
                .valores(null)
                .producto(null)
                .valoresData(null)
                .defaultProducto(nuevoProducto).build());

        return this.save(nuevoProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerProductos() {
        return this.productoRepository.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto obtenerProducto(Long id) {
        return this.findById(id);
    }

    @Override
    @Transactional
    public Producto actualizarDatosProducto(Producto producto, Long id) {

        Producto productoActual = this.obtenerProducto(id);

        productoActual.setNombre(producto.getNombre());
        productoActual.setDescripcion(producto.getDescripcion());
        productoActual.setSubcategoria(producto.getSubcategoria());
        productoActual.setMarca(producto.getMarca());
        productoActual.setUnidadMedida(producto.getUnidadMedida());

        productoActual.getDefaultSku().setNombre(producto.getNombre());
        productoActual.getDefaultSku().setDescripcion(producto.getDescripcion());

        if (!productoActual.isVendibleSinPropiedades()) {
            this.actualizarDatosSkusProductos(productoActual);
        }

        return this.save(productoActual);
    }

    private void actualizarDatosSkusProductos(Producto producto) {
        for (Sku skuAdicional: producto.getSkus()) {
            skuAdicional.setNombre(producto.getNombre());
            skuAdicional.setDescripcion(producto.getDescripcion());
        }
    }

    @Transactional
    @Override
    public Producto actualizarDisponibilidadGeneralProducto(Integer disponibilidadGral, Long productoId) {
        Producto productoActual = this.obtenerProducto(productoId);

        productoActual.setDisponibilidadGeneral(disponibilidadGral);
        productoActual.getDefaultSku().setDisponibilidad(disponibilidadGral);

        return this.save(productoActual);
    }

    @Transactional
    @Override
    public Producto actualizarPrecioBaseProducto(Double precio, Long productoId) {
        Producto productoActual = this.obtenerProducto(productoId);

        productoActual.setPrecio(this.roundService.truncate(precio));
        productoActual.getDefaultSku().setPrecio(precio);

        return this.save(productoActual);
    }

    @Override
    @Transactional
    public void darDeBaja(Producto producto, Long id) {
        Producto productoActual = this.obtenerProducto(id);

        if (!productoActual.isActivo()) {
            throw new ProductoException("Este producto ya está dado de baja");
        }

        productoActual.setActivo(false);
        this.save(productoActual);
    }

    @Override
    @Transactional
    public void darDeAlta(Producto producto, Long id) {
        Producto productoActual = this.obtenerProducto(id);

        if (productoActual.isActivo()) {
            throw new ProductoException("Este producto ya está activo");
        }

        productoActual.setActivo(true);
        this.save(productoActual);
    }

    @Override
    @Transactional
    public void destacar(Producto producto, Long id) {
        Producto productoActual = this.obtenerProducto(id);

        productoActual.setDestacado(!producto.isDestacado());
        this.save(productoActual);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UnidadMedida> obtenerUnidadesMedida() {
        return this.unidadMedidaRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public List<PropiedadProducto> obtenerPropiedadesDeProducto(Long productoId) {
        Producto producto = this.obtenerProducto(productoId);

        return producto.getPropiedades();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Sku> obtenerSkusProducto(Long productoId) {
        Producto producto = this.obtenerProducto(productoId);

        return producto.getSkus();
    }

    @Transactional(readOnly = true)
    @Override
    public Sku obtenerSkuProducto(Long productoId, Long skuId) {
        Producto producto = this.obtenerProducto(productoId);
        Sku sku = null;

        boolean existeSku = false;
        //Verificamos si no es el default sku
        if (producto.getDefaultSku().getId().equals(skuId)) {
            sku = producto.getDefaultSku();
            existeSku = true;
        }
        for (Sku skuEnProducto: producto.getSkus()) {
            if (skuEnProducto.getId().equals(skuId)) {
                sku = skuEnProducto;
                existeSku = true;
                break;
            }
        }

        if (!existeSku) throw new ProductoException("El sku con id: " + skuId + " no pertenece " +
                "al producto: " + producto.getNombre().concat(" (ID: ")
                .concat(String.valueOf(producto.getId())).concat(")"));

        return sku;
    }

    @Transactional(readOnly = true)
    @Override
    public Sku obtenerSkuDefectoProducto(Long productoId) {
        return this.obtenerProducto(productoId).getDefaultSku();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Producto> productosEnSubcategoria(Subcategoria subcategoria) {
        return this.productoRepository.findAllBySubcategoria(subcategoria);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Producto> findAll() {
        return this.productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Producto findById(Long aLong) {
        return this.productoRepository.findById(aLong)
                .orElseThrow(() -> new ProductoException("No existe el producto con id: " + aLong));
    }

    @Transactional
    @Override
    public Producto save(Producto object) {
        return this.productoRepository.save(object);
    }

    @Transactional
    @Override
    public void delete(Producto object) {
        this.deleteById(object.getId());
    }

    @Transactional
    @Override
    public void deleteById(Long aLong) {
        if (this.productoRepository.findById(aLong).isEmpty())
            throw new ProductoException("No existe el producto con id: " + aLong);

        try {
            this.productoRepository.deleteById(aLong);
        } catch (DataAccessException e) {
            throw new ProductoException("No se pudo eliminar el producto " + aLong + "ya que posee referencias" +
                    " con otros objetos del sistema : " + e.getMessage());
        }
    }
}

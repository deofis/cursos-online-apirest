package com.deofis.tiendaapirest.productos.services;

import com.deofis.tiendaapirest.productos.domain.Producto;
import com.deofis.tiendaapirest.productos.domain.Sku;
import com.deofis.tiendaapirest.productos.domain.ValorPropiedadProducto;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.exceptions.SkuException;
import com.deofis.tiendaapirest.productos.repositories.SkuRepository;
import com.deofis.tiendaapirest.productos.repositories.ValorPropiedadProductoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class SkuServiceImpl implements SkuService {

    private final SkuRepository skuRepository;
    private final ValorPropiedadProductoRepository valorPropiedadProductoRepository;
    private final GeneradorSkus generadorSkus;

    @Transactional
    @Override
    public Sku crearNuevoSku(Sku sku, Producto producto) {

        Sku nuevoSku = Sku.builder()
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(sku.getPrecio())
                .precioOferta(sku.getPrecioOferta())
                .disponibilidad(sku.getDisponibilidad())
                .fechaCreacion(new Date())
                .foto(null)
                .defaultProducto(null)
                .producto(producto)
                .valores(new ArrayList<>())
                .valoresData("").build();

        String valoresData = "";
        List<ValorPropiedadProducto> valoresReales = new ArrayList<>();
        for (ValorPropiedadProducto valor: sku.getValores()) {
            ValorPropiedadProducto valorActual = this.getValor(valor.getId());
            valoresReales.add(valorActual);

            valoresData = valoresData.concat(valorActual.getValor().concat("/"));
        }

        nuevoSku.setValores(valoresReales);
        nuevoSku.setValoresData(valoresData);

        return this.skuRepository.save(nuevoSku);
    }

    @Override
    public Map<String, Object> generarSkusProducto(Producto producto) {
        Map<String, Object> map = new HashMap<>();
        int cantCombinacionesGeneradas = this.generadorSkus.generarSkusProducto(producto);

        if (cantCombinacionesGeneradas == -1) {
            throw new SkuException("No se generaron combinaciones: El producto seleccionado" +
                    " no posee propiedades.");
        }

        if (cantCombinacionesGeneradas == -2) {
            throw new SkuException("No se generaron combinaciones: Las propiedades de el producto " +
                    "seleccionado no poseen valores asociados");
        }

        map.put("combinaciones", cantCombinacionesGeneradas);
        map.put("skus", producto.getSkus());
        return map;
    }

    @Transactional(readOnly = true)
    @Override
    public Sku obtenerSku(Long skuId) {
        return this.skuRepository.findById(skuId)
                .orElseThrow(() -> new SkuException("No existe el SKU con id: " + skuId));
    }

    @Transactional
    @Override
    public Sku actualizarSku(Long skuId, Sku sku) {
        Sku skuActual = this.obtenerSku(skuId);

        skuActual.setNombre(sku.getNombre());
        skuActual.setDescripcion(sku.getDescripcion());
        skuActual.setPrecio(sku.getPrecio());
        skuActual.setPrecioOferta(sku.getPrecioOferta());
        skuActual.setDisponibilidad(sku.getDisponibilidad());
        return this.skuRepository.save(skuActual);
    }

    @Transactional
    @Override
    public Sku actualizarDisponibilidad(Long skuId, Integer disponibilidad) {
        Sku skuActual = this.obtenerSku(skuId);

        skuActual.setDisponibilidad(disponibilidad);
        return this.skuRepository.save(skuActual);
    }

    @Transactional
    @Override
    public Sku actualizarPrecio(Long skuId, Double precio) {
        Sku sku = this.obtenerSku(skuId);
        sku.setPrecio(precio);
        return this.skuRepository.save(sku);
    }

    @Transactional
    @Override
    public Sku actualizarPrecioOferta(Long skuId, Double precioOferta) {
        Sku sku = this.obtenerSku(skuId);
        sku.setPrecioOferta(precioOferta);
        return this.skuRepository.save(sku);
    }

    @Transactional
    @Override
    public void eliminarSku(Long skuId) {
        this.skuRepository.deleteById(skuId);
    }

    private ValorPropiedadProducto getValor(Long valorId) {
        return this.valorPropiedadProductoRepository.findById(valorId)
                .orElseThrow(() -> new ProductoException("No existe el valor de propiedad"));
    }
}
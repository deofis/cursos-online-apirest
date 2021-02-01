package com.deofis.tiendaapirest.productos.services;

import com.deofis.tiendaapirest.globalservices.RoundService;
import com.deofis.tiendaapirest.productos.domain.Producto;
import com.deofis.tiendaapirest.productos.domain.Promocion;
import com.deofis.tiendaapirest.productos.domain.Sku;
import com.deofis.tiendaapirest.productos.domain.Subcategoria;
import com.deofis.tiendaapirest.productos.exceptions.PromocionException;
import com.deofis.tiendaapirest.productos.repositories.PromocionRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PromocionServiceImpl implements PromocionService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(PromocionServiceImpl.class);
    private final ProductoService productoService;
    private final SkuService skuService;
    private final SubcategoriaService subcategoriaService;
    private final PromocionRepository promocionRepository;

    private final RoundService roundService;

    public PromocionServiceImpl(ProductoService productoService, SkuService skuService, SubcategoriaService subcategoriaService, PromocionRepository promocionRepository, RoundService roundService) {
        this.productoService = productoService;
        this.skuService = skuService;
        this.subcategoriaService = subcategoriaService;
        this.promocionRepository = promocionRepository;
        this.roundService = roundService;
    }

    @Transactional
    @Override
    public Producto programarOfertaProducto(Long productoId, Promocion promocion) {
        Producto producto = this.productoService.obtenerProducto(productoId);
        Promocion promoActual = producto.getPromocion();

        // Eliminamos de la base de datos la promoción anterior, para no cargar la base de datos
        // con datos que ya NO sirven.
        if (promoActual != null) {
            this.promocionRepository.deleteById(promoActual.getId());
            producto.setPromocion(null);
        }

        // Si no se manda porcentaje y el producto tiene SKUS adicionales, se tira excepción al cargar promoción.
        if (promocion.getPorcentaje() == null && !producto.isVendibleSinPropiedades())
            throw new PromocionException("No se puede cargar promoción: No se puede asignar un precio fijo al producto" +
                    " porque posee skus adicionales");

        // Seteamos el precio de oferta/porcentaje adecuado al PRODUCTO.
        this.setearPreciosYPorcentajes(promocion, producto.getPrecio());

        Promocion promoProducto = Promocion.builder()
                .fechaDesde(promocion.getFechaDesde())
                .fechaHasta(promocion.getFechaHasta())
                .precioOferta(this.roundService.truncate(promocion.getPrecioOferta()))
                .porcentaje(promocion.getPorcentaje()).build();

        for (Sku sku: producto.getSkus()) {
            if (sku.getPromocion() != null) {
                this.promocionRepository.deleteById(sku.getPromocion().getId());
                sku.setPromocion(null);
            }

            // Seteamos el precio de oferta/porcentaje adecuado al SKU actual en la iteración.
            this.setearPreciosYPorcentajes(promocion, sku.getPrecio());

            Promocion promoSku = Promocion.builder()
                    .fechaDesde(promocion.getFechaDesde())
                    .fechaHasta(promocion.getFechaHasta())
                    .precioOferta(this.roundService.truncate(promocion.getPrecioOferta()))
                    .porcentaje(promocion.getPorcentaje()).build();

            sku.setPromocion(promoSku);
        }

        producto.setPromocion(promoProducto);
        producto.getDefaultSku().setPromocion(promoProducto);
        return this.productoService.save(producto);
    }

    /**
     * Método que setea el precio o porcentaje de acuerdo a lo que llega de la promoción.
     * @param promocion {@link Promocion} promoción recibida a setear su precio o porcentaje.
     * @param precio Double precio a utilizar como base.
     */
    private void setearPreciosYPorcentajes(Promocion promocion, Double precio) {
        if (promocion.getPorcentaje() != null)
            promocion.setPrecioOferta(this.calcularPrecioOferta(promocion.getPorcentaje(),
                    precio));
        else if (promocion.getPrecioOferta() != null)
            promocion.setPorcentaje(this.calcularPorcentajeOferta(promocion.getPrecioOferta(), precio));
    }

    @Transactional
    @Override
    public Sku programarOfertaSku(Long skuId, Promocion promocion) {
        Sku sku = this.skuService.obtenerSku(skuId);
        Promocion promoActual = sku.getPromocion();

        if (promoActual != null) {
            this.promocionRepository.deleteById(promoActual.getId());
            sku.setPromocion(null);
        }

        this.setearPreciosYPorcentajes(promocion, sku.getPrecio());

        Promocion nuevaPromocion = Promocion.builder()
                .fechaDesde(promocion.getFechaDesde())
                .fechaHasta(promocion.getFechaHasta())
                .precioOferta(this.roundService.truncate(promocion.getPrecioOferta()))
                .porcentaje(promocion.getPorcentaje()).build();

        sku.setPromocion(nuevaPromocion);
        return this.skuService.save(sku);
    }

    @Transactional
    @Override
    public Integer programarOfertaProductosSubcategoria(Long subcategoriaId, Promocion promocion) {
        Subcategoria subcategoria = this.subcategoriaService.obtenerSubcategoria(subcategoriaId);
        List<Producto> productosDeSubcategoria = this.productoService.productosEnSubcategoria(subcategoria);
        Integer productosPromocionados = productosDeSubcategoria.size();
        log.info("Cantidad de productos a promocionar: " + productosPromocionados);

        // En este caso, el porcentaje es requerido NOT NULL.
        // El precio oferta es IRRELEVANTE, porque, en caso de recibir, se sobreescribe.
        if (promocion.getPorcentaje() == null) throw new PromocionException("El porcentaje de" +
                " promoción es requerido para aplicar la promoción");

        for (Producto producto: productosDeSubcategoria) {
            // Lógica llamando a programar oferta producto por cada producto del array list.
            this.programarOfertaProducto(producto.getId(), promocion);

            /* LÓGICA PROPIA DEL METODO
            if (producto.getPromocion() != null) this.promocionRepository
                    .deleteById(producto.getPromocion().getId());

            Promocion promoProducto = Promocion.builder()
                    .fechaDesde(promocion.getFechaDesde())
                    .fechaHasta(promocion.getFechaHasta())
                    .porcentaje(promocion.getPorcentaje())
                    .precioOferta(this.roundService.truncate(this.calcularPrecioOferta(promocion.getPorcentaje(),
                            producto.getPrecio()))).build();

            producto.setPromocion(promoProducto);
            producto.getDefaultSku().setPromocion(promoProducto);

            for (Sku sku: producto.getSkus()) {
                if (sku.getPromocion() != null) this.promocionRepository
                        .deleteById(sku.getPromocion().getId());

                Promocion promoSku = Promocion.builder()
                        .fechaDesde(promocion.getFechaDesde())
                        .fechaHasta(promocion.getFechaHasta())
                        .porcentaje(promocion.getPorcentaje())
                        .precioOferta(this.calcularPrecioOferta(promocion.getPorcentaje(),
                                sku.getPrecio())).build();

                sku.setPromocion(promoSku);
            }

            this.productoService.save(producto);

             */
        }

        return productosPromocionados;
    }

    @Transactional(readOnly = true)
    @Override
    public Promocion obtenerPromocionProducto(Long productoId) {
        return this.productoService.obtenerProducto(productoId).getPromocion();
    }

    @Transactional(readOnly = true)
    @Override
    public Promocion obtenerPromocionSku(Long skuId) {
        return this.skuService.obtenerSku(skuId).getPromocion();
    }

    private Double calcularPrecioOferta(Double porcentaje, Double precioBase) {
        return precioBase - (precioBase * porcentaje);
    }

    private Double calcularPorcentajeOferta(Double precioOferta, Double precioBase) {
        if (precioBase < precioOferta) throw new PromocionException("El precio de la promoción" +
                " no puede ser mayor al del precio base del producto");
        return (precioBase - precioOferta) / precioBase;
    }

}

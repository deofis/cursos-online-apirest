package com.deofis.tiendaapirest.perfiles.services;

import com.deofis.tiendaapirest.perfiles.domain.Carrito;
import com.deofis.tiendaapirest.perfiles.domain.DetalleCarrito;
import com.deofis.tiendaapirest.perfiles.exceptions.CarritoException;
import com.deofis.tiendaapirest.perfiles.repositories.CarritoRepository;
import com.deofis.tiendaapirest.perfiles.repositories.DetalleCarritoRepository;
import com.deofis.tiendaapirest.productos.domain.Sku;
import com.deofis.tiendaapirest.productos.services.SkuService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final DetalleCarritoRepository detalleCarritoRepository;
    private final PerfilService perfilService;
    private final SkuService skuService;

    private final ValidadorItems validadorItems;

    @Transactional
    @Override
    public Carrito agregarItem(Long skuId, Integer cantidad) {
        boolean existeItem = false;
        Carrito carrito = this.perfilService.obtenerCarrito();
        Sku sku = this.skuService.obtenerSku(skuId);

        if (this.validadorItems.esItemNoVendible(sku))
            throw new CarritoException("El item no es vendible: el item posee un sku por defecto y" +
                    " el producto tiene skus adicionales.");

        for (DetalleCarrito item: carrito.getItems()) {
            if (item.getSku().equals(sku)) {
                existeItem = true;
                item.setCantidad(item.getCantidad() + cantidad);
                log.info("Ya existe item con sku (" + sku.getId() + "). Sumar cantidad");
                break;
            }
        }

        if (!existeItem) {
            log.info("No existe item con sku (" + sku.getId() + "). agregar nuevo detalle");
            DetalleCarrito detalleCarrito = new DetalleCarrito();
            detalleCarrito.setSku(sku);
            detalleCarrito.setCantidad(cantidad);
            carrito.getItems().add(detalleCarrito);
        }

        return this.carritoRepository.save(carrito);
    }

    @Transactional
    @Override
    public Carrito actualizarCantidad(Long skuId, Integer cantidad) {
        Carrito carrito = this.perfilService.obtenerCarrito();
        Sku sku = this.skuService.obtenerSku(skuId);

        if (carrito.getItems().size() == 0) {
            throw new CarritoException("Carrito vacío.");
        }

        boolean existeItem = false;
        for (DetalleCarrito item: carrito.getItems()) {
            if (item.getSku().equals(sku)) {
                log.info("Existe item con sku en el carrito");
                item.setCantidad(cantidad);
                existeItem = true;
                break;
            }
        }

        if (!existeItem) {
            log.info("No existe el item con sku en el carrito");
            throw new CarritoException("Producto no agregado al carrito");
        }

        return this.carritoRepository.save(carrito);
    }

    @Transactional
    @Override
    public Carrito quitarItem(Long skuId) {
        Carrito carrito = this.perfilService.obtenerCarrito();
        Sku sku = this.skuService.obtenerSku(skuId);

        carrito.getItems().removeIf(item -> item.getSku().equals(sku));
        this.detalleCarritoRepository.deleteBySku(sku);

        return this.carritoRepository.save(carrito);
    }

    @Transactional
    @Override
    public void vaciar() {
        Carrito carrito = this.perfilService.obtenerCarrito();

        for (DetalleCarrito item: carrito.getItems()) {
            this.detalleCarritoRepository.delete(item);
        }

        carrito.getItems().clear();
        this.carritoRepository.save(carrito);
    }
}

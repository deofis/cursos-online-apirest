package com.deofis.tiendaapirest.perfiles.services;

import com.deofis.tiendaapirest.perfiles.domain.Carrito;

public interface CarritoService {

    /**
     * Agrega un Sku de Producto al carrito con una cantidad requerida. En caso de
     * que el item ya exista, esta cantidad se suma a la anterior; en caso de que no,
     * se crea el nuevo item con la cantidad seteada.
     * @param skuId Long id del sku a agregar.
     * @param cantidad Integer cantidad que tendrá el item.
     * @return Carrito actualizado.
     */
    Carrito agregarItem(Long skuId, Integer cantidad);

    /**
     * Actualizar la cantidad de skus de un carrito.
     * @param skuId Long id del sku a actualizar cantidad.
     * @param cantidad Integer nueva cantidad de sku.
     * @return Carrito actualizado.
     */
    Carrito actualizarCantidad(Long skuId, Integer cantidad);

    /**
     * Quita un Sku de Producto del carrito.
     * @param skuId Long id del sku a quitar del carrito.
     * @return Carrito actualizado.
     */
    Carrito quitarItem(Long skuId);

    /**
     * Vacía el carrito de compras, dejándolo sin items.
     */
    void vaciar();
}

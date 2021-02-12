package com.deofis.tiendaapirest.perfiles.services;

import com.deofis.tiendaapirest.productos.domain.Sku;

/**
 * Este servicio sirve para validar que un ITEM de carrito/operacion sea vendible para
 * formar parte de uno de estos objetos.
 */
public interface ValidadorItems {

    /**
     * Devuelve TRUE si el item puede venderse:
     *                       - sku.defaultProducto != null y el producto no tiene skus adicionales.
     *                       - sku.defaultProducto == null (si es un SKU adicional es VENDIBLE por si solo).
     * Si no se da esta condición, el item no puede venderse, por lo que el servicio devuelve FALSE.
     * @param sku {@link Sku} a validar.
     * @return boolean.
     */
    boolean esItemNoVendible(Sku sku);
}

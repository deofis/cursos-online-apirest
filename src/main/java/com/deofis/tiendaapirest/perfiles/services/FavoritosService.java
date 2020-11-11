package com.deofis.tiendaapirest.perfiles.services;

import com.deofis.tiendaapirest.perfiles.domain.Favoritos;

public interface FavoritosService {

    /**
     * Agrega un producto al listado de favoritos del perfil.
     * @param productoId Long id del producto a agregar.
     * @return Listado de favoritos actualizado.
     */
    Favoritos agregarFavorito(Long productoId);

    /**
     * Elimina un producto del listado de favoritos del perfil.
     * @param productoId Long id del producto a quitar.
     * @return Listado de favoritos actualizado.
     */
    Favoritos quitarFavorito(Long productoId);
}

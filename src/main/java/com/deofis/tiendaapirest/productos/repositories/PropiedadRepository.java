package com.deofis.tiendaapirest.productos.repositories;

import com.deofis.tiendaapirest.productos.domain.PropiedadProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated
public interface PropiedadRepository extends JpaRepository<PropiedadProducto, Long> {

}

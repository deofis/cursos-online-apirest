package com.deofis.tiendaapirest.ecommerce.repositories;

import com.deofis.tiendaapirest.ecommerce.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    boolean existsByOrden(Integer orden);

    @Query("SELECT MAX(b.orden) as Orden FROM Banner b")
    Integer ultimoOrden();

}

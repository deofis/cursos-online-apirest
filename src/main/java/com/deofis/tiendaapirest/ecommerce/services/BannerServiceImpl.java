package com.deofis.tiendaapirest.ecommerce.services;

import com.deofis.tiendaapirest.ecommerce.domain.Banner;
import com.deofis.tiendaapirest.ecommerce.dto.BannerDto;
import com.deofis.tiendaapirest.ecommerce.exceptions.BannerException;
import com.deofis.tiendaapirest.ecommerce.repositories.BannerRepository;
import com.deofis.tiendaapirest.productos.domain.Imagen;
import com.deofis.tiendaapirest.productos.services.images.ImageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    private final ImageService imageService;

    private final String clientUrl;

    @Override
    public Banner subirBanner(BannerDto bannerDto, MultipartFile imagen) {

        // int orden = bannerDto.getOrden();

        // Mantenemos referencia de cuantos banners existen para no superar el número
        // máximo.
        if (this.validarCantBanners())
            throw new BannerException("Ya existen 5 Banners en la aplicación cargados. Porfavor, " +
                    "actualice un banner existente");

        /*
        // Si un Banner ya tiene el número de orden que se desea asociar, se lanza
        // excepción.
        if (this.existeOrden(bannerDto.getOrden()))
            throw new BannerException("Ya existe un Banner con N° de Orden: " + orden);


        if (orden > 4 || orden <= 0)

         */

        Imagen imagenBanner = this.imageService.subirImagen(imagen);

        Banner banner = Banner.builder()
                .imagen(imagenBanner)
                .actionUrl(this.clientUrl.concat("/" + bannerDto.getActionUrl())).build();
        this.asignarOrden(banner);

        return this.save(banner);
    }

    private void asignarOrden(Banner banner) {
        int lastOrden;
        if (this.bannerRepository.ultimoOrden() == null){
            lastOrden = 0;
        } else {
                lastOrden = this.bannerRepository.ultimoOrden();
        }

        banner.setOrden(lastOrden + 1);
    }

    private void reordenarOrdenes(Banner banner) {
        List<Banner> banners = this.bannerRepository.findAll();

        int bannerOrden = banner.getOrden();
        for (Banner b: banners) {
            if (b.getOrden() > bannerOrden) {
                b.setOrden(b.getOrden() - 1);
            }
            this.save(b);
        }
    }

    @Override
    public Banner actualizarImagenBanner(MultipartFile imagen, Long bannerId) {
        Banner banner = this.findById(bannerId);
        this.eliminarFotoBanner(banner);

        Imagen nuevaImagen = this.imageService.subirImagen(imagen);
        banner.setImagen(nuevaImagen);
        return this.save(banner);
    }

    @Override
    public Banner actualizarActionUrl(Long bannerId, String actionUrl) {
        Banner banner = this.findById(bannerId);
        banner.setActionUrl(actionUrl);
        return this.save(banner);
    }

    @Override
    public void cambiarOrdenbanners(Long bannerId1, Long bannerId2) {
        log.info("Por implementar...");
    }

    @Override
    public List<Banner> obtenerBanners() {
        return this.findAll();
    }

    @Override
    public Banner obtenerBanner(Long bannerId) {
        return this.findById(bannerId);
    }

    @Override
    public void eliminarBanner(Long bannerId) {
        Banner banner = this.findById(bannerId);
        Imagen imagenBanner = banner.getImagen();
        banner.setImagen(null);

        this.imageService.eliminarImagen(imagenBanner);
        this.reordenarOrdenes(banner);
        this.delete(banner);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Banner> findAll() {
        return this.bannerRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Banner findById(Long aLong) {
        return this.bannerRepository.findById(aLong)
                .orElseThrow(() -> new BannerException("No existe el banner con id: " + aLong));
    }

    @Transactional
    @Override
    public Banner save(Banner object) {
        return this.bannerRepository.save(object);
    }

    @Transactional
    @Override
    public void delete(Banner object) {
        this.bannerRepository.delete(object);
    }

    @Transactional
    @Override
    public void deleteById(Long aLong) {
        this.bannerRepository.deleteById(aLong);
    }

    private boolean existeOrden(Integer orden) {
        return this.bannerRepository.existsByOrden(orden);
    }

    private boolean validarCantBanners() {
        return this.bannerRepository.findAll().size() > 4;
    }

    private void eliminarFotoBanner(Banner banner) {
        Imagen imagenBanner = banner.getImagen();
        banner.setImagen(null);
        this.imageService.eliminarImagen(imagenBanner);
    }
}

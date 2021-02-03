package com.deofis.tiendaapirest.productos.services;

import com.deofis.tiendaapirest.productos.domain.Categoria;
import com.deofis.tiendaapirest.productos.domain.Imagen;
import com.deofis.tiendaapirest.productos.domain.Subcategoria;
import com.deofis.tiendaapirest.productos.exceptions.CategoriaException;
import com.deofis.tiendaapirest.productos.repositories.CategoriaRepository;
import com.deofis.tiendaapirest.productos.repositories.SubcategoriaRepository;
import com.deofis.tiendaapirest.productos.services.images.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final ImageService imageService;

    @Override
    @Transactional
    public Categoria crear(Categoria categoria) {
        Categoria categoriaNueva = Categoria.builder()
                .nombre(categoria.getNombre())
                .build();

        return this.categoriaRepository.save(categoriaNueva);
    }

    @Override
    @Transactional
    public Categoria actualizar(Categoria categoria, Long id) {
        Categoria categoriaActual = this.obtenerCategoria(id);

        categoriaActual.setNombre(categoria.getNombre());

        return this.categoriaRepository.save(categoriaActual);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> obtenerCategorias() {
        return this.categoriaRepository.findAllByOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Categoria obtenerCategoria(Long id) {
        return this.categoriaRepository.findById(id)
                .orElseThrow(() -> new CategoriaException("Categoría no existente con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subcategoria> obtenerSubcategorias(Long categoriaId) {
        Categoria categoria = this.categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new CategoriaException("Categoria no existente con id: " + categoriaId));


        return categoria.getSubcategorias();
    }

    @Override
    @Transactional(readOnly = true)
    public Subcategoria obtenerSubcategoriaDeCategoria(Long categoriaId, Long subcategoriaId) {
        Categoria categoria = this.obtenerCategoria(categoriaId);
        Subcategoria subcategoria = null;

        boolean existeSub = false;
        for (Subcategoria sub: categoria.getSubcategorias()) {
            if (sub.getId().equals(subcategoriaId)) {
                existeSub = true;
                subcategoria = sub;
                break;
            }
        }

        if (!existeSub) throw new CategoriaException("La subcategoría no pertenece a la categoría: ".concat(categoria.getNombre()));

        return subcategoria;
    }

    @Transactional
    @Override
    public Imagen subirFotoCategoria(Long categoriaId, MultipartFile foto) {
        Categoria categoria = this.obtenerCategoria(categoriaId);

        if (categoria.getFoto() != null)
            this.eliminarFotoCategoria(categoria.getId());

        Imagen fotoCategoria = this.imageService.subirImagen(foto);
        categoria.setFoto(fotoCategoria);
        this.categoriaRepository.save(categoria);
        return fotoCategoria;
    }

    @Transactional(readOnly = true)
    @Override
    public byte[] obtenerFotoCategoria(Long categoriaId) {
        Categoria categoria = this.obtenerCategoria(categoriaId);
        Imagen fotoCategoria = categoria.getFoto();

        if (fotoCategoria == null) throw new CategoriaException("La categoría: " + categoria.getNombre()
                + " no tiene foto");

        return this.imageService.descargarImagen(fotoCategoria);
    }

    @Transactional(readOnly = true)
    @Override
    public String obtenerPathFotoCategoria(Long categoriaId) {
        Categoria categoria = this.obtenerCategoria(categoriaId);
        return categoria.getFoto().getPath();
    }

    @Transactional
    @Override
    public void eliminarFotoCategoria(Long categoriaId) {
        Categoria categoria = this.obtenerCategoria(categoriaId);

        if (categoria.getFoto() == null) throw new CategoriaException("La categoría: " + categoria.getNombre()
                + " no tiene foto");

        Imagen fotoCategoria = categoria.getFoto();
        categoria.setFoto(null);
        this.categoriaRepository.save(categoria);
        this.imageService.eliminarImagen(fotoCategoria);
    }

    @Transactional(readOnly = true)
    @Override
    public Categoria obtenerCategoriaPorSubcategoria(Subcategoria subcategoria) {
        return this.categoriaRepository.findBySubcategoriasContaining(subcategoria)
                .orElseThrow(() -> new CategoriaException("La subcategoría no pertenece a ninguna categoría"));
    }
}

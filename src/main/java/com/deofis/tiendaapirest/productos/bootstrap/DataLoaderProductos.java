package com.deofis.tiendaapirest.productos.bootstrap;

import com.deofis.tiendaapirest.productos.domain.*;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
@Profile({"qa", "qaheroku", "dev"})
public class DataLoaderProductos implements CommandLineRunner {

    private final UnidadMedidaRepository unidadMedidaRepository;
    private final MarcaRepository marcaRepository;

    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public void run(String... args) throws Exception {

        if (this.unidadMedidaRepository.findByNombre("Unidad").isEmpty()) {
            UnidadMedida unidad = UnidadMedida.builder()
                    .nombre("Unidad")
                    .codigo("U").build();

            try {
                this.unidadMedidaRepository.save(unidad);
            } catch (DataIntegrityViolationException e) {
                throw new ProductoException(e.getMessage());
            }
        }

        if (this.marcaRepository.findAll().size() == 0) {
            List<Marca> marcas = new ArrayList<>();

            Marca puma = Marca.builder().nombre("Puma").build();
            marcas.add(puma);

            Marca topper = Marca.builder().nombre("Topper").build();
            marcas.add(topper);

            Marca jaguar = Marca.builder().nombre("Jaguar").build();
            marcas.add(jaguar);

            Marca jFoos = Marca.builder().nombre("John Foos").build();
            marcas.add(jFoos);

            Marca converse = Marca.builder().nombre("Converse").build();
            marcas.add(converse);

            Marca adidas = Marca.builder().nombre("Adidas").build();
            marcas.add(adidas);

            Marca nike = Marca.builder().nombre("Nike").build();
            marcas.add(nike);

            this.marcaRepository.saveAll(marcas);
        }

        // Creación de propiedades y valores previo a la carga de prods y
        // subcategorias

        // Colores
        PropiedadProducto color = PropiedadProducto.builder()
                .nombre("Color").variable(true).valores(new ArrayList<>()).build();
        ValorPropiedadProducto blanco = ValorPropiedadProducto.builder()
                .valor("Blanco").build();
        ValorPropiedadProducto negro = ValorPropiedadProducto.builder()
                .valor("Negro").build();
        ValorPropiedadProducto amarillo = ValorPropiedadProducto.builder()
                .valor("Amarillo").build();
        ValorPropiedadProducto gris = ValorPropiedadProducto.builder()
                .valor("Gris").build();
        ValorPropiedadProducto rojo = ValorPropiedadProducto.builder()
                .valor("Rojo").build();
        color.getValores().add(blanco);
        color.getValores().add(negro);
        color.getValores().add(amarillo);
        color.getValores().add(gris);
        color.getValores().add(rojo);

        // Talles
        PropiedadProducto talle = PropiedadProducto.builder()
                .nombre("Talle").variable(true).valores(new ArrayList<>()).build();
        ValorPropiedadProducto eu36 = ValorPropiedadProducto.builder()
                .valor("36 EU").build();
        ValorPropiedadProducto eu37 = ValorPropiedadProducto.builder()
                .valor("37 EU").build();
        ValorPropiedadProducto eu38 = ValorPropiedadProducto.builder()
                .valor("38 EU").build();
        ValorPropiedadProducto eu39 = ValorPropiedadProducto.builder()
                .valor("39 EU").build();
        talle.getValores().add(eu36);
        talle.getValores().add(eu37);
        talle.getValores().add(eu38);
        talle.getValores().add(eu39);

        // Carga de categorías y subcategorías

        if (this.categoriaRepository.findAll().size() == 0) {
            // Listado de subcategorias
            Subcategoria tenisH = Subcategoria.builder()
                    .nombre("Tenis").codigo("TEN").propiedades(new ArrayList<>()).build();
            tenisH.getPropiedades().add(color);
            tenisH.getPropiedades().add(talle);

            Subcategoria tenisM = Subcategoria.builder()
                    .nombre("Tenis").codigo("TEN").propiedades(new ArrayList<>()).build();
            tenisM.getPropiedades().add(color);
            tenisM.getPropiedades().add(talle);

            Subcategoria tenisN = Subcategoria.builder()
                    .nombre("Tenis").codigo("TEN").propiedades(new ArrayList<>()).build();
            tenisN.getPropiedades().add(color);
            tenisN.getPropiedades().add(talle);

            Subcategoria deportivoH = Subcategoria.builder()
                    .nombre("Deportivo").codigo("DEP").propiedades(new ArrayList<>()).build();
            deportivoH.getPropiedades().add(color);
            deportivoH.getPropiedades().add(talle);

            Subcategoria deportivoM = Subcategoria.builder()
                    .nombre("Deportivo").codigo("DEP").propiedades(new ArrayList<>()).build();
            deportivoM.getPropiedades().add(color);
            deportivoM.getPropiedades().add(talle);

            Subcategoria deportivoN = Subcategoria.builder()
                    .nombre("Deportivo").codigo("DEP").propiedades(new ArrayList<>()).build();
            deportivoN.getPropiedades().add(color);
            deportivoN.getPropiedades().add(talle);

            Subcategoria botasH = Subcategoria.builder()
                    .nombre("Botas").codigo("BOT").propiedades(new ArrayList<>()).build();
            botasH.getPropiedades().add(color);
            botasH.getPropiedades().add(talle);

            Subcategoria botasM = Subcategoria.builder()
                    .nombre("Botas").codigo("BOT").propiedades(new ArrayList<>()).build();
            botasM.getPropiedades().add(color);
            botasM.getPropiedades().add(talle);

            Subcategoria calzadoTrabajoH = Subcategoria.builder()
                    .nombre("Calzado de trabajo").codigo("TRA").propiedades(new ArrayList<>()).build();
            calzadoTrabajoH.getPropiedades().add(color);
            calzadoTrabajoH.getPropiedades().add(talle);

            Subcategoria calzadoTrabajoM = Subcategoria.builder()
                    .nombre("Calzado de trabajo").codigo("TRA").propiedades(new ArrayList<>()).build();
            calzadoTrabajoM.getPropiedades().add(color);
            calzadoTrabajoM.getPropiedades().add(talle);

            Subcategoria zapatosH = Subcategoria.builder()
                    .nombre("Zapatos").codigo("ZAP").propiedades(new ArrayList<>()).build();
            zapatosH.getPropiedades().add(color);
            zapatosH.getPropiedades().add(talle);

            Subcategoria zapatosM = Subcategoria.builder()
                    .nombre("Zapatos").codigo("ZAP").propiedades(new ArrayList<>()).build();
            zapatosM.getPropiedades().add(color);
            zapatosM.getPropiedades().add(talle);

            Subcategoria zapatosN = Subcategoria.builder()
                    .nombre("Zapatos").codigo("ZAP").propiedades(new ArrayList<>()).build();
            zapatosN.getPropiedades().add(color);
            zapatosN.getPropiedades().add(talle);

            Subcategoria mocasines = Subcategoria.builder()
                    .nombre("Mocasines").codigo("MOC").propiedades(new ArrayList<>()).build();
            mocasines.getPropiedades().add(color);
            mocasines.getPropiedades().add(talle);

            Subcategoria sandaliasH = Subcategoria.builder()
                    .nombre("Sandalias").codigo("SAN").propiedades(new ArrayList<>()).build();
            sandaliasH.getPropiedades().add(color);
            sandaliasH.getPropiedades().add(talle);

            Subcategoria sandaliasM = Subcategoria.builder()
                    .nombre("Sandalias").codigo("SAN").propiedades(new ArrayList<>()).build();
            sandaliasM.getPropiedades().add(color);
            sandaliasM.getPropiedades().add(talle);

            Subcategoria sandaliasN = Subcategoria.builder()
                    .nombre("Sandalias").codigo("SAN").propiedades(new ArrayList<>()).build();
            sandaliasN.getPropiedades().add(color);
            sandaliasN.getPropiedades().add(talle);

            Subcategoria crocsH = Subcategoria.builder()
                    .nombre("Crocs").codigo("CRO").propiedades(new ArrayList<>()).build();
            crocsH.getPropiedades().add(color);
            crocsH.getPropiedades().add(talle);

            Subcategoria crocsM = Subcategoria.builder()
                    .nombre("Crocs").codigo("CRO").propiedades(new ArrayList<>()).build();
            crocsM.getPropiedades().add(color);
            crocsM.getPropiedades().add(talle);

            Subcategoria crocsN = Subcategoria.builder()
                    .nombre("Crocs").codigo("CRO").propiedades(new ArrayList<>()).build();
            crocsN.getPropiedades().add(color);
            crocsN.getPropiedades().add(talle);

            // Listado de categorias
            List<Categoria> categorias = new ArrayList<>();

            Categoria hombre = Categoria.builder()
                    .nombre("Hombre").subcategorias(new ArrayList<>()).build();
            hombre.getSubcategorias().add(tenisH);
            hombre.getSubcategorias().add(deportivoH);
            hombre.getSubcategorias().add(botasH);
            hombre.getSubcategorias().add(calzadoTrabajoH);
            hombre.getSubcategorias().add(zapatosH);
            hombre.getSubcategorias().add(mocasines);
            hombre.getSubcategorias().add(sandaliasH);
            hombre.getSubcategorias().add(crocsH);
            categorias.add(hombre);

            Categoria mujer = Categoria.builder()
                    .nombre("Mujer").subcategorias(new ArrayList<>()).build();
            mujer.getSubcategorias().add(tenisM);
            mujer.getSubcategorias().add(deportivoM);
            mujer.getSubcategorias().add(botasM);
            mujer.getSubcategorias().add(calzadoTrabajoM);
            mujer.getSubcategorias().add(zapatosM);
            mujer.getSubcategorias().add(sandaliasM);
            mujer.getSubcategorias().add(crocsM);
            categorias.add(mujer);

            Categoria ninos = Categoria.builder()
                    .nombre("Niños").subcategorias(new ArrayList<>()).build();
            ninos.getSubcategorias().add(tenisN);
            ninos.getSubcategorias().add(deportivoN);
            ninos.getSubcategorias().add(zapatosN);
            ninos.getSubcategorias().add(sandaliasN);
            ninos.getSubcategorias().add(crocsN);
            categorias.add(ninos);

            // Guardar categorias y subcategorias
            this.categoriaRepository.saveAll(categorias);
        }

        // Carga de productos

        if (this.productoRepository.findAll().size() == 0) {
            // Listado de productos
            List<Producto> productos = new ArrayList<>();

            Producto pumaStorm = Producto.builder()
                    .nombre("Tenis Puma Storm Mesh")
                    .descripcion("Conquista el mundo con estas deslumbrantes tenis de verano. Un diseño dinámico" +
                            " inspirado en formas naturales que se combina con una parte superior de malla y una" +
                            " entresuela de EVA moldeada por compresión para un verano liviano con" +
                            " impacto visual.")
                    .precio(67.99)
                    .disponibilidadGeneral(50)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(1L))
                    .marca(this.marcaRepository.getOne(1L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();
            pumaStorm.getPropiedades().add(color);
            pumaStorm.getPropiedades().add(talle);
            pumaStorm.setDefaultSku(Sku.builder()
                    .nombre(pumaStorm.getNombre())
                    .descripcion(pumaStorm.getDescripcion())
                    .fechaCreacion(pumaStorm.getFechaCreacion())
                    .precio(pumaStorm.getPrecio())
                    .disponibilidad(pumaStorm.getDisponibilidadGeneral())
                    .defaultProducto(pumaStorm).build());
            productos.add(pumaStorm);

            Producto pumaFlyer = Producto.builder()
                    .nombre("Tenis Deportivos Puma Flyer Runner")
                    .descripcion("Deportivos especiales para hacer running en tus tardes libres. Disfrute" +
                            " del calzado deportivo de punta de una marca reconocida como es Puma.")
                    .precio(57.99)
                    .disponibilidadGeneral(29)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(false)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(4L))
                    .marca(this.marcaRepository.getOne(1L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();
            pumaFlyer.getPropiedades().add(color);
            pumaFlyer.getPropiedades().add(talle);
            pumaFlyer.setDefaultSku(Sku.builder()
                    .nombre(pumaFlyer.getNombre())
                    .descripcion(pumaFlyer.getDescripcion())
                    .precio(pumaFlyer.getPrecio())
                    .fechaCreacion(pumaFlyer.getFechaCreacion())
                    .disponibilidad(pumaFlyer.getDisponibilidadGeneral())
                    .defaultProducto(pumaFlyer).build());
            productos.add(pumaFlyer);

            Producto pumaBasket = Producto.builder()
                    .nombre("Bota Puma Basket Fierce")
                    .descripcion("Bota puma basket fierce moda urbano")
                    .precio(47.99)
                    .disponibilidadGeneral(49)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(7L))
                    .marca(this.marcaRepository.getOne(1L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();
            pumaBasket.getPropiedades().add(color);
            pumaBasket.getPropiedades().add(talle);
            pumaBasket.setDefaultSku(Sku.builder()
                    .nombre(pumaBasket.getNombre())
                    .descripcion(pumaBasket.getDescripcion())
                    .precio(pumaBasket.getPrecio())
                    .fechaCreacion(pumaBasket.getFechaCreacion())
                    .disponibilidad(pumaBasket.getDisponibilidadGeneral())
                    .defaultProducto(pumaBasket).build());
            productos.add(pumaBasket);

            Producto topperCapitan = Producto.builder()
                    .nombre("Tenis Topper Capitán")
                    .descripcion("Ideales para tu look urbano, cool y atemporal." +
                            " Un clásico de Topper para todos los nostálgicos")
                    .precio(43.00)
                    .disponibilidadGeneral(49)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(2L))
                    .marca(this.marcaRepository.getOne(2L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();
            topperCapitan.getPropiedades().add(color);
            topperCapitan.getPropiedades().add(talle);
            topperCapitan.setDefaultSku(Sku.builder()
                    .nombre(topperCapitan.getNombre())
                    .descripcion(topperCapitan.getDescripcion())
                    .precio(topperCapitan.getPrecio())
                    .fechaCreacion(topperCapitan.getFechaCreacion())
                    .disponibilidad(topperCapitan.getDisponibilidadGeneral())
                    .defaultProducto(topperCapitan).build());
            productos.add(topperCapitan);

            Producto topperUltralight = Producto.builder()
                    .nombre("Tenis Deportivos Topper Ultralight")
                    .descripcion("")
                    .precio(38.99)
                    .disponibilidadGeneral(25)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .subcategoria(this.subcategoriaRepository.getOne(5L))
                    .marca(this.marcaRepository.getOne(2L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();
            topperUltralight.getPropiedades().add(color);
            topperUltralight.getPropiedades().add(talle);
            topperUltralight.setDefaultSku(Sku.builder()
                    .nombre(topperUltralight.getNombre())
                    .descripcion(topperUltralight.getDescripcion())
                    .precio(topperUltralight.getPrecio())
                    .fechaCreacion(topperUltralight.getFechaCreacion())
                    .disponibilidad(topperUltralight.getDisponibilidadGeneral())
                    .defaultProducto(topperUltralight).build());
            productos.add(topperUltralight);

            Producto adidasRunfalcon = Producto.builder()
                    .nombre("Tenis deportivos Adidas Runfalcon")
                    .descripcion("Capellada elaborada en malla textil con refuerzos de piel sintética. La construcción" +
                            " no presenta costuras en el upper, para que puedas notar una gran sensación de confort en" +
                            " todo el pie. Ligera entresuela de EVA, para aportar una amortiguación sencilla y" +
                            " efectiva, reduciendo el peso del calzado y consiguiendo una pisada confortable." +
                            " Suela exterior de goma que presenta un patrón con ranuras situadas de forma estratégica" +
                            " con las que se mejora la flexibilidad del calzado.")
                    .precio(62.00)
                    .disponibilidadGeneral(25)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(4L))
                    .marca(this.marcaRepository.getOne(6L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();
            adidasRunfalcon.getPropiedades().add(color);
            adidasRunfalcon.getPropiedades().add(talle);
            adidasRunfalcon.setDefaultSku(Sku.builder()
                    .nombre(adidasRunfalcon.getNombre())
                    .descripcion(adidasRunfalcon.getDescripcion())
                    .precio(adidasRunfalcon.getPrecio())
                    .fechaCreacion(adidasRunfalcon.getFechaCreacion())
                    .disponibilidad(adidasRunfalcon.getDisponibilidadGeneral())
                    .defaultProducto(adidasRunfalcon).build());
            productos.add(adidasRunfalcon);

            // Guardamos los productos y sus skus por defecto
            this.productoRepository.saveAll(productos);
        }
    }
}

package com.deofis.tiendaapirest.productos.bootstrap;

import com.deofis.tiendaapirest.productos.domain.*;
import com.deofis.tiendaapirest.productos.exceptions.ProductoException;
import com.deofis.tiendaapirest.productos.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
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

            Marca escuelaLATAM = Marca.builder().nombre("Escuela LATAM").build();
            marcas.add(escuelaLATAM);

            this.marcaRepository.saveAll(marcas);
        }

        // Carga de categorías y subcategorías
        if (this.categoriaRepository.findAll().size() == 0) {
            // Listado de subcategorias
            List<Subcategoria> subcategorias = new ArrayList<>();
            Subcategoria idiomas = Subcategoria.builder()
                    .nombre("Idiomas").codigo("IDI").propiedades(new ArrayList<>()).build();
            subcategorias.add(idiomas);

            Subcategoria talleres = Subcategoria.builder()
                    .nombre("Talleres").codigo("TAL").propiedades(new ArrayList<>()).build();
            subcategorias.add(talleres);

            Subcategoria seguridad = Subcategoria.builder()
                    .nombre("Seguridad").codigo("SEG").propiedades(new ArrayList<>()).build();
            subcategorias.add(seguridad);

            Subcategoria turismo = Subcategoria.builder()
                    .nombre("Turismo").codigo("TUR").propiedades(new ArrayList<>()).build();
            subcategorias.add(turismo);

            // Listado de categorias
            Categoria cursos = Categoria.builder()
                    .nombre("Cursos").subcategorias(new ArrayList<>()).build();
            cursos.getSubcategorias().addAll(subcategorias);

            // Guardar categorias y subcategorias
            this.categoriaRepository.save(cursos);
        }

        // Carga de productos

        if (this.productoRepository.findAll().size() == 0) {
            // Listado de productos
            List<Producto> productos = new ArrayList<>();

            Producto tripulacionCruceros = Producto.builder()
                    .nombre("Tripulación de Cruceros")
                    .descripcion("Una gran capacitación, para aprender todo lo necesario para calificar y trabajar en Cruceros.\n" +
                            "\n" +
                            "La principal finalidad del curso es la de capacitar al alumno para que pueda conocer en profundidad, con detalles, las prestaciones de Cruceros, su forma de trabajo, como armar su curriculum, calificar, y poder Trabajar en un Crucero.\n" +
                            "\n" +
                            "Otorga una profunda capacitación y amplia los conocimientos del turismo en Cruceros, ya que está dictado por los mejores profesionales del Rubro.\n" +
                            "\n" +
                            "Título: Auxiliar Tripulante de Cruceros\n" +
                            "\n" +
                            "CUPOS LIMITADOS❗\n" +
                            "DURACION DE LA CAPACITACION:\n" +
                            "\n" +
                            "– CLASE DE INTRODUCCION DE 1 HORA Y MEDIA\n" +
                            "\n" +
                            "– 4 CLASES DE 2hs CADA UNA + Bonus Track Devoluciones\n" +
                            "\n" +
                            "Total, 12hs de Capacitación / Cursados días Miércoles de 19hs a 21hs\n" +
                            "\n" +
                            "MODALIDAD: Online Vía Zoom")
                    .precio(9000.00)
                    .disponibilidadGeneral(50)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(4L))
                    .marca(this.marcaRepository.getOne(1L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();

            tripulacionCruceros.setDefaultSku(Sku.builder()
                    .nombre(tripulacionCruceros.getNombre())
                    .descripcion(tripulacionCruceros.getDescripcion())
                    .fechaCreacion(tripulacionCruceros.getFechaCreacion())
                    .precio(tripulacionCruceros.getPrecio())
                    .disponibilidad(tripulacionCruceros.getDisponibilidadGeneral())
                    .defaultProducto(tripulacionCruceros).build());
            productos.add(tripulacionCruceros);

            Producto portugues = Producto.builder()
                    .nombre("Portugés Turístico")
                    .descripcion("Enfocados en capacitar efectivamente, conseguimos que nuestros estudiantes se " +
                            "desenvuelvan naturalmente en el extranjero, logrando que inicien y finalicen una conversación fluida.\n" +
                            "\n" +
                            "El participante entrena variables necesarias en diálogos, canciones, escritos y diversas situaciones que se " +
                            "presentan en los viajes, como por ejemplo al momento de ubicarse en un lugar, comunicarse al llegar a un hotel," +
                            "manejarse en aeropuertos, solicitar un pedido en restaurantes, o bien visitar un banco para realizar un trámite. " +
                            "Estas y muchas situaciones que atravesamos viajando, podrán disfrutarse mucho más y así mejorar la condición del " +
                            "viajero en el extranjero.\n" +
                            "CANTIDAD DE CLASES: 16 clases + 1 MasterClass Introductoria: Miércoles 6 de enero 19 hs. (Sin cargo)\n" +
                            "DÍAS DE CURSADO: Lunes y Miércoles de 19:00 a 21:00 hs.\n" +
                            "INICIA: Lunes 18 de Marzo de 2021 de 19:00hs a 21:00hs.\n" +
                            "MODALIDAD: Online Vía Zoom")
                    .precio(6500.00)
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

            portugues.setDefaultSku(Sku.builder()
                    .nombre(portugues.getNombre())
                    .descripcion(portugues.getDescripcion())
                    .fechaCreacion(portugues.getFechaCreacion())
                    .precio(portugues.getPrecio())
                    .disponibilidad(portugues.getDisponibilidadGeneral())
                    .defaultProducto(portugues).build());
            productos.add(portugues);

            Producto covid19 = Producto.builder()
                    .nombre("Seguridad Covid-19 en Turismo")
                    .descripcion("En este entrenamiento podrás conocer de manera sencilla todos los sistemas basados en " +
                            "Metodología Suiza, en protocolos que se realizan en el marco de las acciones de prevención " +
                            "para evitar la propagación del Covid-19 en Turismo en Estados Unidos.\n" +
                            "CANTIDAD DE CLASES: 8 Encuentros de 2 horas\n" +
                            "DÍAS DE CURSADO: Martes y Jueves de 14 a 16 hs.\n" +
                            "PRÓXIMA EDICIÓN: 17 de Marzo" +
                            "MODALIDAD: Online Vía Zoom")
                    .precio(5000.00)
                    .disponibilidadGeneral(50)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(3L))
                    .marca(this.marcaRepository.getOne(1L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();

            covid19.setDefaultSku(Sku.builder()
                    .nombre(covid19.getNombre())
                    .descripcion(covid19.getDescripcion())
                    .fechaCreacion(covid19.getFechaCreacion())
                    .precio(covid19.getPrecio())
                    .disponibilidad(covid19.getDisponibilidadGeneral())
                    .defaultProducto(covid19).build());
            productos.add(covid19);

            Producto turismoEstudiantil = Producto.builder()
                    .nombre("Taller Integral de Turismo Estudiantil")
                    .descripcion("Iniciamos a todos los interesados en los Conocimientos profesionales, éticos y técnicos " +
                            "para desarrollar cualquier tipo de productos para turismo Estudiantil primario, secundario " +
                            "o educativo, observado desde todos los lugares donde se puedan desempeñar. Promoción, " +
                            "supervisión, jefe de zona, gerente, administración, coordinación y coordinación general de hotel\n." +
                            "\nInscripciones Próximamente.")
                    .precio(6000.00)
                    .disponibilidadGeneral(50)
                    .foto(null)
                    .imagenes(new ArrayList<>())
                    .activo(true)
                    .destacado(true)
                    .fechaCreacion(new Date())
                    .subcategoria(this.subcategoriaRepository.getOne(2L))
                    .marca(this.marcaRepository.getOne(1L))
                    .unidadMedida(this.unidadMedidaRepository.getOne(1L))
                    .propiedades(new ArrayList<>())
                    .skus(new ArrayList<>()).build();

            turismoEstudiantil.setDefaultSku(Sku.builder()
                    .nombre(turismoEstudiantil.getNombre())
                    .descripcion(turismoEstudiantil.getDescripcion())
                    .fechaCreacion(turismoEstudiantil.getFechaCreacion())
                    .precio(turismoEstudiantil.getPrecio())
                    .disponibilidad(turismoEstudiantil.getDisponibilidadGeneral())
                    .defaultProducto(turismoEstudiantil).build());
            productos.add(turismoEstudiantil);

            // Guardamos los productos y sus skus por defecto
            this.productoRepository.saveAll(productos);
        }
    }
}

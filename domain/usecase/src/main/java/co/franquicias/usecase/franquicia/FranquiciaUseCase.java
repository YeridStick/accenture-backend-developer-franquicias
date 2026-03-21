package co.franquicias.usecase.franquicia;

import co.franquicias.model.franquicia.Franquicia;
import co.franquicias.model.franquicia.FranquiciaRepositoryPort;
import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import co.franquicias.model.producto.ProductoRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranquiciaUseCase {

    private final FranquiciaRepositoryPort franquiciaRepository;
    private final SucursalRepositoryPort sucursalRepository;
    private final ProductoRepositoryPort productoRepository;

    public Mono<Franquicia> crearFranquicia(String nombre) {
        return franquiciaRepository.crearFranquicia(nombre);
    }

    public Mono<Franquicia> obtenerPorId(String id) {
        return franquiciaRepository.findById(id);
    }

    public Mono<Franquicia> obtenerFranquiciaPorNombre(String nombre) {
        return franquiciaRepository.findByNombre(nombre);
    }

    public Flux<Franquicia> obtenerFranquicias() {
        return franquiciaRepository.findAll();
    }

    public Mono<String> eliminarFranquiciaPorId(String id) {
        return franquiciaRepository.eliminarPorId(id);
    }

    public Mono<Franquicia> actualizarFranquicia(String franquiciaId, Franquicia cambios) {
        return franquiciaRepository.actualizarFranquicia(franquiciaId, cambios);
    }

}

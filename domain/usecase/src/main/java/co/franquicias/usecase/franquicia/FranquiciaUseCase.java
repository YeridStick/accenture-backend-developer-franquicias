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
        return franquiciaRepository.findById(id)
                .flatMap(f -> hydrateFranquicia(f, true));
    }

    public Mono<Franquicia> obtenerFranquiciaPorNombre(String nombre) {
        return franquiciaRepository.findByNombre(nombre)
                .flatMap(f -> hydrateFranquicia(f, true));
    }

    public Flux<Franquicia> obtenerFranquicias(boolean verProducto) {
        return franquiciaRepository.findAll()
                .flatMap(f -> hydrateFranquicia(f, verProducto));
    }

    public Mono<String> eliminarFranquiciaPorId(String id) {
        return franquiciaRepository.eliminarPorId(id);
    }

    public Mono<Franquicia> actualizarFranquicia(String franquiciaId, Franquicia cambios) {
        return franquiciaRepository.actualizarFranquicia(franquiciaId, cambios);
    }

    // ---------- Operaciones de Hidratación ----------

    private Mono<Franquicia> hydrateFranquicia(Franquicia f, boolean includeProductos) {
        return sucursalRepository.listarPorFranquicia(f.getId())
                .flatMap(suc -> includeProductos 
                        ? hydrateSucursal(suc) 
                        : Mono.just(suc))
                .collectList()
                .map(list -> f.toBuilder().sucursales(list).build());
    }

    private Mono<Sucursal> hydrateSucursal(Sucursal s) {
        return productoRepository.listarPorSucursal(s.getId())
                .collectList()
                .map(prods -> s.toBuilder().productos(prods).build());
    }
}

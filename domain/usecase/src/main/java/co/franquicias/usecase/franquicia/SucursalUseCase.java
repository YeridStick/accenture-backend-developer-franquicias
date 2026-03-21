package co.franquicias.usecase.franquicia;

import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SucursalUseCase {

    private final SucursalRepositoryPort repository;

    public Mono<Sucursal> agregarSucursal(String franquiciaId, String nombreSucursal) {
        return repository.crear(franquiciaId, nombreSucursal);
    }

    public Mono<Sucursal> obtenerSucursalPorId(String id) {
        return repository.findById(id);
    }

    public Flux<Sucursal> obtenerSucursalPorFranquiciaId(String franquiciaId) {
        return repository.listarPorFranquicia(franquiciaId);
    }

    public Mono<String> eliminarSucursalPorId(String id) {
        return repository.eliminarPorId(id);
    }

    public Mono<Sucursal> actualizarSucursal(String id, Sucursal cambios) {
        return repository.actualizarSucursal(id, cambios);
    }
}

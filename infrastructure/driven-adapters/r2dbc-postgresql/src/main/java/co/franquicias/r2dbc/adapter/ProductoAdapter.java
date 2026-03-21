package co.franquicias.r2dbc.adapter;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.error.NotFoundException;
import co.franquicias.model.producto.Producto;
import co.franquicias.model.producto.ProductoRepositoryPort;
import co.franquicias.r2dbc.entity.ProductoEntity;
import co.franquicias.r2dbc.helper.ReactiveAdapterOperations;
import co.franquicias.r2dbc.repository.ProductoRepository;
import co.franquicias.r2dbc.repository.SucursalRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Repository
public class ProductoAdapter extends ReactiveAdapterOperations<Producto, ProductoEntity, String, ProductoRepository> implements ProductoRepositoryPort {

    private final SucursalRepository sucursalRepository;

    public ProductoAdapter(ProductoRepository repository, ObjectMapper mapper, SucursalRepository sucursalRepository) {
        super(repository, mapper, d -> mapper.map(d, Producto.class));
        this.sucursalRepository = sucursalRepository;
    }

    @Override
    public Mono<Producto> crear(String sucursalId, String nombre, long precio, int stock) {
        var now = Instant.now();
        var data = ProductoEntity.builder()
                .id(UUID.randomUUID().toString())
                .sucursalId(sucursalId)
                .nombre(nombre)
                .precio(precio)
                .stock(stock)
                .createdAt(now)
                .updatedAt(now)
                .isNewRecord(true)
                .build();

        return sucursalRepository.existsById(sucursalId)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? repository.existsBySucursalIdAndNombre(sucursalId, nombre)
                        .flatMap(dup -> Boolean.TRUE.equals(dup)
                                ? Mono.error(new ConflictException("El producto '" + nombre + "' ya existe en esta sucursal"))
                                : repository.save(data))
                        : Mono.error(new NotFoundException("La sucursal destino no existe: " + sucursalId))
                )
                .map(this::toEntity);
    }

    @Override
    public Flux<Producto> listarPorSucursal(String sucursalId) {
        return repository.findBySucursalId(sucursalId).map(this::toEntity);
    }

    @Override
    public Flux<Producto> listarPorSucursal(String sucursalId, int page, int size) {
        return repository.findBySucursalId(sucursalId, PageRequest.of(page, size)).map(this::toEntity);
    }

    @Override
    public Flux<Producto> buscarPorNombreLike(String nombreLike) {
        return repository.findByNombreContainingIgnoreCase(nombreLike).map(this::toEntity);
    }

    @Override
    public Flux<Producto> buscarPorNombreLike(String nombreLike, int page, int size) {
        return repository.findByNombreContainingIgnoreCase(nombreLike, PageRequest.of(page, size)).map(this::toEntity);
    }

    @Override
    public Mono<Producto> actualizarStock(String id, int stock) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Producto no encontrado: " + id)))
                .flatMap(data -> {
                    data.setStock(stock);
                    data.setUpdatedAt(Instant.now());
                    return repository.save(data);
                })
                .map(this::toEntity);
    }

    @Override
    public Mono<String> eliminarPorId(String id) {
        return repository.deleteById(id).thenReturn("Producto eliminado correctamente");
    }

    @Override
    public Mono<Producto> actualizarProducto(String id, Producto cambios) {
        cambios.setUpdatedAt(Instant.now());
        return mergeNonNullAndSave(id, cambios);
    }

    @Override
    public Flux<Producto> findAll(int page, int size) {
        return repository.findAllBy(PageRequest.of(page, size)).map(this::toEntity);
    }

    @Override
    public Flux<Producto> findTopStockProductsByFranquicia(String franquiciaId) {
        return repository.findTopStockProductsByFranquicia(franquiciaId).map(this::toEntity);
    }
}

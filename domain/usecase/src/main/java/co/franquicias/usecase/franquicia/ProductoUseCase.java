package co.franquicias.usecase.franquicia;

import co.franquicias.model.error.ConflictException;
import co.franquicias.model.error.NotFoundException;
import co.franquicias.model.franquicia.FranquiciaRepositoryPort;
import co.franquicias.model.producto.Producto;
import co.franquicias.model.producto.ProductoRepositoryPort;
import co.franquicias.model.sucursal.SucursalRepositoryPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class ProductoUseCase {

    private final ProductoRepositoryPort productoRepository;
    private final SucursalRepositoryPort sucursalRepository;
    private final FranquiciaRepositoryPort franquiciaRepository;

    public Mono<Producto> agregarProducto(String franquiciaId, String sucursalId, String nombreProducto, long precio, int stock) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no existe: " + sucursalId)))
                .flatMap(s -> Objects.equals(franquiciaId, s.getFranquiciaId())
                        ? productoRepository.crear(sucursalId, nombreProducto, precio, stock)
                        : Mono.error(new ConflictException("La sucursal no pertenece a la franquicia '" + franquiciaId + "'")));
    }

    public Mono<Void> eliminarProducto(String franquiciaId, String sucursalId, String productoId) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no existe: " + sucursalId)))
                .flatMap(s -> Objects.equals(franquiciaId, s.getFranquiciaId())
                        ? productoRepository.eliminarPorId(productoId).then()
                        : Mono.error(new ConflictException("La sucursal no pertenece a la franquicia '" + franquiciaId + "'")));
    }

    public Mono<Producto> actualizarStock(String franquiciaId, String sucursalId, String productoId, int stock) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no existe: " + sucursalId)))
                .flatMap(s -> Objects.equals(franquiciaId, s.getFranquiciaId())
                        ? productoRepository.actualizarStock(productoId, stock)
                        : Mono.error(new ConflictException("La sucursal no pertenece a la franquicia '" + franquiciaId + "'")));
    }

    public Mono<Producto> actualizarProducto(String id, Producto cambios) {
        return productoRepository.actualizarProducto(id, cambios);
    }

    public Flux<Map<String, Object>> maxStockPorSucursal(String franquiciaId) {
        return productoRepository.findTopStockProductsByFranquicia(franquiciaId)
                .flatMap(prod -> sucursalRepository.findById(prod.getSucursalId())
                        .map(suc -> Map.<String, Object>of(
                                "sucursalId",     suc.getId(),
                                "sucursalNombre", suc.getNombre(),
                                "productoId",     prod.getId(),
                                "productoNombre", prod.getNombre(),
                                "stock",          prod.getStock()
                        ))
                );
    }

    public Flux<Producto> getAllProductos() {
        return productoRepository.findAll();
    }

    public Flux<Producto> getAllProductos(int page, int size) {
        return productoRepository.findAll(page, size);
    }

    public Mono<Map<String, Object>> getProductoGlobal(String productoId) {
        return productoRepository.findById(productoId)
                .switchIfEmpty(Mono.error(new NotFoundException("Producto no encontrado: " + productoId)))
                .zipWhen(p -> sucursalRepository.findById(p.getSucursalId())
                        .switchIfEmpty(Mono.error(new NotFoundException("Sucursal del producto no existe: " + p.getSucursalId()))))
                .flatMap(t -> franquiciaRepository.findById(t.getT2().getFranquiciaId())
                        .map(f -> Map.of(
                                "productoId",      t.getT1().getId(),
                                "productoNombre",  t.getT1().getNombre(),
                                "stock",           t.getT1().getStock(),
                                "precio",          t.getT1().getPrecio() != null ? t.getT1().getPrecio() : 0L,
                                "sucursalId",      t.getT2().getId(),
                                "sucursalNombre",  t.getT2().getNombre(),
                                "franquiciaId",    f.getId(),
                                "franquiciaNombre", f.getNombre()
                        )));
    }

    public Flux<Producto> searchProductosGlobal(String nombreLike) {
        return productoRepository.buscarPorNombreLike(nombreLike == null ? "" : nombreLike.trim());
    }

    public Flux<Producto> searchProductosGlobal(String nombreLike, int page, int size) {
        return productoRepository.buscarPorNombreLike(nombreLike == null ? "" : nombreLike.trim(), page, size);
    }

    public Flux<Object> getAllProductosViewRaw(int page, int size) {
        return productoRepository.findAll(page, size)
                .flatMap(p -> sucursalRepository.findById(p.getSucursalId())
                        .flatMap(s -> franquiciaRepository.findById(s.getFranquiciaId())
                                .map(f -> Map.<String, Object>of(
                                        "productoId",      p.getId(),
                                        "productoNombre",  p.getNombre(),
                                        "stock",           p.getStock(),
                                        "precio",          p.getPrecio() != null ? p.getPrecio() : 0L,
                                        "sucursalId",      s.getId(),
                                        "sucursalNombre",  s.getNombre(),
                                        "franquiciaId",    f.getId(),
                                        "franquiciaNombre", f.getNombre()
                                ))));
    }

    public Mono<Map<String, Object>> getProductoGlobalViewRaw(String productoId) {
        return getProductoGlobal(productoId);
    }

    public Flux<Producto> getProductosDeSucursal(String franquiciaId, String sucursalId) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no existe: " + sucursalId)))
                .flatMapMany(s -> {
                    if (!Objects.equals(franquiciaId, s.getFranquiciaId())) {
                        return Flux.error(new ConflictException("La sucursal no pertenece a la franquicia '" + franquiciaId + "'"));
                    }
                    return productoRepository.listarPorSucursal(sucursalId);
                });
    }

    public Flux<Producto> getProductosDeSucursal(String franquiciaId, String sucursalId, int page, int size) {
        return sucursalRepository.findById(sucursalId)
                .switchIfEmpty(Mono.error(new NotFoundException("Sucursal no existe: " + sucursalId)))
                .flatMapMany(s -> {
                    if (!Objects.equals(franquiciaId, s.getFranquiciaId())) {
                        return Flux.error(new ConflictException("La sucursal no pertenece a la franquicia '" + franquiciaId + "'"));
                    }
                    return productoRepository.listarPorSucursal(sucursalId, page, size);
                });
    }
}

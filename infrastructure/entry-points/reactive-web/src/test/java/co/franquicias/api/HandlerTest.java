package co.franquicias.api;

import co.franquicias.api.dto.franquicia.CreateFranquiciaRequest;
import co.franquicias.api.dto.franquicia.UpdateFranquiciaRequest;
import co.franquicias.api.dto.producto.CreateProductoRequest;
import co.franquicias.api.dto.producto.UpdateProductoRequest;
import co.franquicias.api.dto.producto.UpdateStockRequest;
import co.franquicias.api.dto.sucursal.CreateSucursalRequest;
import co.franquicias.api.dto.sucursal.UpdateSucursalRequest;
import co.franquicias.model.franquicia.Franquicia;
import co.franquicias.api.error.RequestValidator;
import co.franquicias.model.producto.Producto;
import co.franquicias.model.sucursal.Sucursal;
import co.franquicias.usecase.franquicia.FranquiciaUseCase;
import co.franquicias.usecase.franquicia.ProductoUseCase;
import co.franquicias.usecase.franquicia.SucursalUseCase;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class HandlerTest {

    @Mock
    FranquiciaUseCase franquiciaUseCase;
    @Mock
    SucursalUseCase sucursalUseCase;
    @Mock
    ProductoUseCase productoUseCase;
    @Mock
    RequestValidator validator;
    
    @InjectMocks
    Handler handler;

    WebTestClient client;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(validator.validate(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        RouterFunction<ServerResponse> router = buildRouter(handler);
        client = WebTestClient.bindToRouterFunction(router).build();
    }

    // ====================== Helpers dominio ======================
    private Franquicia f(String id, String nombre) {
        Franquicia ff = new Franquicia();
        ff.setId(id); ff.setNombre(nombre);
        return ff;
    }

    private Sucursal s(String id, String fid, String nombre) {
        Sucursal ss = new Sucursal();
        ss.setId(id); ss.setFranquiciaId(fid); ss.setNombre(nombre);
        return ss;
    }

    private Producto p(String id, String sid, String nombre, int stock) {
        Producto pp = new Producto();
        pp.setId(id); pp.setSucursalId(sid); pp.setNombre(nombre); pp.setStock(stock);
        return pp;
    }

    /** Router de prueba (orden explícito de rutas) */
    private static RouterFunction<ServerResponse> buildRouter(Handler h) {
        return RouterFunctions.route()
                // Franquicia
                .POST("/api/franquicias", h::crearFranquicia)
                .GET("/api/franquicias", h::obtenerFranquicias)
                .GET("/api/franquicias/by-name", h::obtenerFranquiciaPorNombre)
                .GET("/api/franquicias/{franquiciaId}", h::obtenerFranquicia)
                .DELETE("/api/franquicias/{franquiciaId}", h::eliminarFranquicia)
                .PATCH("/api/franquicias/{franquiciaId}", h::actualizarFranquicia)
                // Sucursal
                .POST("/api/franquicias/{franquiciaId}/sucursales", h::agregarSucursal)
                .GET("/api/franquicias/{franquiciaId}/sucursales", h::listarSucursalesDeFranquicia)
                .GET("/api/sucursales/{sucursalId}", h::obtenerSucursal)
                .DELETE("/api/sucursales/{sucursalId}", h::eliminarSucursal)
                .PATCH("/api/sucursales/{sucursalId}", h::actualizarSucursal)
                // Producto
                .POST("/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos", h::agregarProducto)
                .DELETE("/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}", h::eliminarProducto)
                .PATCH("/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos/{productoId}/stock", h::actualizarStock)
                .PATCH("/api/productos/{productoId}", h::actualizarProducto)
                // Reportes / consultas
                .GET("/api/productos/search", h::searchProductosGlobal)
                .GET("/api/productos/view", h::getAllProductosView)
                .GET("/api/productos/view/{productoId}", h::getProductoGlobalView)
                .GET("/api/franquicias/{franquiciaId}/max-stock-por-sucursal", h::maxStockPorSucursal)
                .GET("/api/productos", h::getAllProductos)
                .GET("/api/productos/{productoId}", h::getProductoGlobal)
                .GET("/api/franquicias/{franquiciaId}/sucursales/{sucursalId}/productos", h::getProductosDeSucursal)
                .build();
    }

    // ====================== Tests ======================

    @Test
    @DisplayName("POST /api/franquicias => 201 Created con Location y cuerpo")
    void crearFranquicia() {
        when(franquiciaUseCase.crearFranquicia("F1")).thenReturn(Mono.just(f("f1","F1")));

        client.post().uri("/api/franquicias")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranquiciaRequest("F1"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/franquicias/f1")
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("f1")
                .jsonPath("$.data.nombre").isEqualTo("F1");

        verify(franquiciaUseCase).crearFranquicia("F1");
    }

    @Test
    @DisplayName("GET /api/franquicias?includeProductos=1 => delega con true")
    void obtenerFranquicias_includeProductos() {
        when(franquiciaUseCase.obtenerFranquicias(true)).thenReturn(Flux.just(f("f1","F1")));

        client.get().uri("/api/franquicias?includeProductos=1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo("f1");

        verify(franquiciaUseCase).obtenerFranquicias(true);
    }

    @Test
    @DisplayName("GET /api/franquicias/{id} => 200 con franquicia")
    void obtenerFranquicia() {
        when(franquiciaUseCase.obtenerPorId("f1")).thenReturn(Mono.just(f("f1","F1")));

        client.get().uri("/api/franquicias/f1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("f1");
    }

    @Test
    @DisplayName("GET /api/franquicias/by-nombre?nombre=F1 => 200")
    void obtenerFranquiciaPorNombre() {
        when(franquiciaUseCase.obtenerFranquiciaPorNombre("F1")).thenReturn(Mono.just(f("f1","F1")));

        client.get().uri("/api/franquicias/by-name?nombre=F1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.nombre").isEqualTo("F1");

        verify(franquiciaUseCase, times(1)).obtenerFranquiciaPorNombre("F1");
        verify(franquiciaUseCase, never()).obtenerPorId(anyString());
    }

    @Test
    @DisplayName("DELETE /api/franquicias/{id} => 200 con mensaje")
    void eliminarFranquicia() {
        when(franquiciaUseCase.eliminarFranquiciaPorId("f1")).thenReturn(Mono.just("ok"));

        client.delete().uri("/api/franquicias/f1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.message").isEqualTo("ok");
    }

    @Test
    @DisplayName("PUT /api/franquicias/{id} => 200 con franquicia actualizada")
    void actualizarFranquicia() {
        when(franquiciaUseCase.actualizarFranquicia(eq("f1"), any(Franquicia.class)))
                .thenReturn(Mono.just(f("f1","Nueva")));

        client.patch().uri("/api/franquicias/f1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateFranquiciaRequest("Nueva"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.nombre").isEqualTo("Nueva");
    }

    @Test
    @DisplayName("POST /api/franquicias/{fId}/sucursales => 200 con sucursal")
    void agregarSucursal() {
        when(sucursalUseCase.agregarSucursal("f1","S1")).thenReturn(Mono.just(s("s1","f1","S1")));

        client.post().uri("/api/franquicias/f1/sucursales")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateSucursalRequest("S1"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("s1")
                .jsonPath("$.data.franquiciaId").isEqualTo("f1");
    }

    @Test
    @DisplayName("GET /api/franquicias/{fId}/sucursales => 200 lista")
    void listarSucursales() {
        when(sucursalUseCase.obtenerSucursalPorFranquiciaId("f1"))
                .thenReturn(Flux.just(s("s1","f1","S1")));

        client.get().uri("/api/franquicias/f1/sucursales")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].nombre").isEqualTo("S1");
    }

    @Test
    @DisplayName("GET /api/sucursales/{id} => 200 sucursal")
    void obtenerSucursal() {
        when(sucursalUseCase.obtenerSucursalPorId("s1")).thenReturn(Mono.just(s("s1","f1","S1")));

        client.get().uri("/api/sucursales/s1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("s1");
    }

    @Test
    @DisplayName("DELETE /api/sucursales/{id} => 200 con mensaje")
    void eliminarSucursal() {
        when(sucursalUseCase.eliminarSucursalPorId("s1")).thenReturn(Mono.just("ok"));

        client.delete().uri("/api/sucursales/s1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.message").isEqualTo("ok");
    }

    @Test
    @DisplayName("PUT /api/sucursales/{id} => 200 con sucursal actualizada")
    void actualizarSucursal() {
        when(sucursalUseCase.actualizarSucursal(eq("s1"), any(Sucursal.class)))
                .thenReturn(Mono.just(s("s1","f1","Nueva")));

        client.patch().uri("/api/sucursales/s1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateSucursalRequest("Nueva","f1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.nombre").isEqualTo("Nueva");
    }

    @Test
    @DisplayName("POST /api/.../productos => 200 producto")
    void agregarProducto() {
        when(productoUseCase.agregarProducto("f1","s1","P1",5))
                .thenReturn(Mono.just(p("p1","s1","P1",5)));

        client.post().uri("/api/franquicias/f1/sucursales/s1/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateProductoRequest("P1",5))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo("p1")
                .jsonPath("$.data.stock").isEqualTo(5);
    }

    @Test
    @DisplayName("DELETE /api/.../productos/{pId} => 204 No Content")
    void eliminarProducto() {
        when(productoUseCase.eliminarProducto("f1","s1","p1")).thenReturn(Mono.empty());

        client.delete().uri("/api/franquicias/f1/sucursales/s1/productos/p1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("PUT /api/.../productos/{pId}/stock => 200 y devuelve producto")
    void actualizarStock() {
        when(productoUseCase.actualizarStock("f1","s1","p1",9))
                .thenReturn(Mono.just(p("p1","s1","P",9)));

        client.patch().uri("/api/franquicias/f1/sucursales/s1/productos/p1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(9))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.stock").isEqualTo(9);
    }

    @Test
    @DisplayName("PUT /api/productos/{pId} => mapea stock=0 cuando viene null/ausente")
    void actualizarProducto_stockDefaultCero() {
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        when(productoUseCase.actualizarProducto(eq("p1"), any(Producto.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(1)));

        client.patch().uri("/api/productos/p1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateProductoRequest("Nuevo", null, 0, "s1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.nombre").isEqualTo("Nuevo")
                .jsonPath("$.data.stock").isEqualTo(0)
                .jsonPath("$.data.sucursalId").isEqualTo("s1");

        verify(productoUseCase).actualizarProducto(eq("p1"), captor.capture());
        Producto enviado = captor.getValue();
        Assertions.assertEquals(0, enviado.getStock());
        Assertions.assertEquals("Nuevo", enviado.getNombre());
        Assertions.assertEquals("s1", enviado.getSucursalId());
    }

    @Test
    @DisplayName("GET /api/franquicias/{fId}/reportes/max-stock => 200 con lista de mapas")
    void maxStockPorSucursal() {
        Map<String, Object> m1 = Map.of(
                "sucursalId", "s1",
                "productoId", "p2",
                "stock", 10
        );

        Map<String, Object> m2 = new java.util.HashMap<>();
        m2.put("sucursalId", "s2");
        m2.put("productoId", null);
        m2.put("stock", 0);

        when(productoUseCase.maxStockPorSucursal("f1"))
                .thenReturn(Flux.just(m1, m2));

        client.get().uri("/api/franquicias/f1/max-stock-por-sucursal")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].sucursalId").isEqualTo("s1")
                .jsonPath("$.data[0].productoId").isEqualTo("p2")
                .jsonPath("$.data[0].stock").isEqualTo(10)
                .jsonPath("$.data[1].productoId").value(org.hamcrest.Matchers.nullValue())
                .jsonPath("$.data[1].stock").isEqualTo(0);
    }

    @Test
    @DisplayName("GET /api/productos => 200 lista")
    void getAllProductos() {
        when(productoUseCase.getAllProductos()).thenReturn(Flux.just(p("p1","s1","A",1)));

        client.get().uri("/api/productos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].id").isEqualTo("p1");
    }

    @Test
    @DisplayName("GET /api/productos/{pId}/global => 200 con mapa")
    void getProductoGlobal() {
        when(productoUseCase.getProductoGlobal("p1"))
                .thenReturn(Mono.just(Map.of("productoId","p1","stock",3)));

        client.get().uri("/api/productos/p1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.productoId").isEqualTo("p1")
                .jsonPath("$.data.stock").isEqualTo(3);
    }

    @Test
    @DisplayName("GET /api/productos/search?nombreLike=ab => 200 con lista")
    void searchProductosGlobal() {
        when(productoUseCase.searchProductosGlobal("ab"))
                .thenReturn(Flux.just(p("p1","s1","ab",1)));
        when(productoUseCase.getAllProductos()).thenReturn(Flux.empty());

        client.get().uri("/api/productos/search?nombreLike=ab")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].nombre").isEqualTo("ab");
    }

    @Test
    @DisplayName("GET /api/franquicias/{fId}/sucursales/{sId}/productos => 200 con lista")
    void getProductosDeSucursal() {
        when(productoUseCase.getProductosDeSucursal("f1","s1"))
                .thenReturn(Flux.just(p("p1","s1","A",1)));

        client.get().uri("/api/franquicias/f1/sucursales/s1/productos")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].sucursalId").isEqualTo("s1");
    }

    @Test
    @DisplayName("by-nombre NO debe ser capturado por /{franquiciaId}")
    void routingPriority() {
        when(franquiciaUseCase.obtenerFranquiciaPorNombre("F1")).thenReturn(Mono.just(f("f1","F1")));

        client.get().uri("/api/franquicias/by-name?nombre=F1")
                .exchange()
                .expectStatus().isOk();

        verify(franquiciaUseCase, times(1)).obtenerFranquiciaPorNombre("F1");
        verify(franquiciaUseCase, never()).obtenerPorId(anyString());
    }

    @Test
    @DisplayName("GET /api/productos/view => 200 con lista ProductoViewDTO")
    void getAllProductosView() {
        Map<String, Object> mockMap = Map.of(
                "productoId", "p1",
                "productoNombre", "NombreP",
                "stock", 10,
                "precio", 500L,
                "franquiciaId", "f1",
                "franquiciaNombre", "Fr1",
                "sucursalId", "s1",
                "sucursalNombre", "Suc1"
        );
        when(productoUseCase.getAllProductosViewRaw()).thenReturn(Flux.just(mockMap));

        client.get().uri("/api/productos/view")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data[0].productoId").isEqualTo("p1")
                .jsonPath("$.data[0].productoNombre").isEqualTo("NombreP")
                .jsonPath("$.data[0].stock").isEqualTo(10)
                .jsonPath("$.data[0].franquiciaNombre").isEqualTo("Fr1");
    }

    @Test
    @DisplayName("GET /api/productos/view/{pId} => 200 con ProductoViewDTO")
    void getProductoGlobalView() {
        Map<String, Object> mockMap = Map.of(
                "productoId", "p1",
                "productoNombre", "NombreP",
                "stock", 10,
                "precio", 500L,
                "franquiciaId", "f1",
                "franquiciaNombre", "Fr1",
                "sucursalId", "s1",
                "sucursalNombre", "Suc1"
        );
        when(productoUseCase.getProductoGlobalViewRaw("p1")).thenReturn(Mono.just(mockMap));

        client.get().uri("/api/productos/view/p1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.productoId").isEqualTo("p1")
                .jsonPath("$.data.productoNombre").isEqualTo("NombreP");
    }
}

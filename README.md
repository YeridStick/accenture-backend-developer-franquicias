# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

---

# Guía de Inicio Rápido

## Requisitos
- **Java 21** (Temurin recomendado)
- **Gradle 8.x**
- **Podman** o **Docker** (para despliegue en contenedores)

##  Construcción y Ejecución Local
Para compilar y ejecutar el proyecto localmente:

```bash
# Otorgar permisos de ejecución (Linux/Mac)
chmod +x gradlew

# Construir y ejecutar
./gradlew bootRun
```
El servicio estará disponible en `http://localhost:8081`.

## Dockerización con Podman
Para construir y correr la aplicación en un contenedor:

1. **Generar el JAR**:
   ```bash
   ./gradlew :app-service:bootJar
   ```
2. **Construir la imagen**:
   ```bash
   podman build -t franquicias-back -f deployment/Dockerfile .
   ```
3. **Ejecutar el contenedor**:
   ```bash
   podman run -d --name franquicias-app -p 8081:8081 -e PORT=8081 franquicias-back
   ```

## Documentación y Salud
- **Swagger UI**: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Health Check**: [http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)

---

# API Documentation

### Franquicias
- **Crear Franquicia**: `POST /api/franquicias`
  - Body: `{ "nombre": "Nombre de la Franquicia" }`
- **Actualizar Nombre**: `PATCH /api/franquicias/{fid}`
  - Body: `{ "nombre": "Nuevo Nombre" }`

### Sucursales
- **Agregar Sucursal**: `POST /api/franquicias/{fid}/sucursales`
  - Body: `{ "nombre": "Nombre de la Sucursal" }`
- **Actualizar Nombre**: `PATCH /api/sucursales/{sid}`
  - Body: `{ "nombre": "Nuevo Nombre" }`

### Productos
- **Agregar Producto**: `POST /api/franquicias/{fid}/sucursales/{sid}/productos`
  - Body: `{ "nombre": "Producto A", "stock": 100, "precio": 5000 }`
- **Eliminar Producto**: `DELETE /api/franquicias/{fid}/sucursales/{sid}/productos/{pid}`
- **Modificar Stock**: `PATCH /api/franquicias/{fid}/sucursales/{sid}/productos/{pid}/stock`
  - Body: `{ "stock": 50 }`
- **Actualizar Datos (Nombre/Precio)**: `PATCH /api/productos/{pid}`
  - Body: `{ "nombre": "Nuevo Nombre", "precio": 6000 }`

### Consultas y Reportes
- **Vista Global de Productos (Paginada)**: `GET /api/productos/view?page=0&size=10`
- **Reporte de Stock Máximo (Punto 7)**: `GET /api/franquicias/{fid}/max-stock-por-sucursal`
  - *Muestra el producto con mayor stock por cada sucursal de la franquicia.*

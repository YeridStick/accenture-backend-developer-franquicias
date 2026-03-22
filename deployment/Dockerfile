# Dockerfile para Franquicias-Back
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Crear usuario/grupo no root por seguridad
RUN addgroup -S app && adduser -S app -G app

# Directorios útiles y permisos
RUN mkdir -p /app /cache /tmp && chown -R app:app /app /cache /tmp

# Copia el JAR generado por Gradle (ejecutar ./gradlew :app-service:bootJar antes)
COPY applications/app-service/build/libs/franquicias-back.jar app.jar

# Ajustes JVM seguros en contenedores
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# Puerto (informativo)
EXPOSE 8081

# Cambia a usuario no root
USER app

# Respetar $PORT si viene seteado (Railway/Cloud Run), por defecto 8081
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8081}"]

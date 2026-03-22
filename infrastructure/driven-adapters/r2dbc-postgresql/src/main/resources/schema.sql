-- Create tables for Franquicias migration

CREATE TABLE IF NOT EXISTS franquicias (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sucursales (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    franquicia_id VARCHAR(36) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_franquicia FOREIGN KEY (franquicia_id) REFERENCES franquicias(id) ON DELETE CASCADE,
    CONSTRAINT ux_sucursal_franquicia_nombre UNIQUE (franquicia_id, nombre)
);

CREATE TABLE IF NOT EXISTS productos (
    id VARCHAR(36) PRIMARY KEY DEFAULT gen_random_uuid()::TEXT,
    sucursal_id VARCHAR(36) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    precio BIGINT NOT NULL DEFAULT 0,
    stock INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE CASCADE,
    CONSTRAINT ux_producto_sucursal_nombre UNIQUE (sucursal_id, nombre)
);

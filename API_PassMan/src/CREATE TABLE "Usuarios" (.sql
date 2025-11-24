CREATE TABLE "Usuarios" (
    id_usuario UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre_usuario VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    salt VARCHAR(100) NOT NULL,
    iteraciones INT NOT NULL,
    nombre_cifrado TEXT,
    apellido_cifrado TEXT,
    rut_cifrado TEXT,
    fecha_nac_cifrada TEXT,
    iv_personales VARCHAR(100)
);

CREATE TABLE "Credenciales" (
    id_credencial UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_usuario UUID NOT NULL,
    servicio VARCHAR(255) NOT NULL,
    usuario_servicio VARCHAR(255),
    password_cifrada TEXT NOT NULL,
    iv VARCHAR(100),
    CONSTRAINT fk_usuario FOREIGN KEY(id_usuario) REFERENCES "Usuarios"(id_usuario) ON DELETE CASCADE
);

CREATE INDEX idx_credenciales_usuario ON "Credenciales"(id_usuario);


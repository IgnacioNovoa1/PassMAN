#  PassMan - API Backend (Servidor)

Este repositorio contiene el **Backend** del sistema PassMan. Es una API REST construida con **Java Spring Boot** que actúa como el núcleo lógico y de seguridad del sistema.

Su función es orquestar la comunicación entre los clientes de escritorio y la infraestructura de datos, asegurando que ningún secreto (claves, contraseñas de BD) salga del entorno seguro del servidor.

## Arquitectura y Tecnologías

El backend se despliega utilizando **Docker Compose** y consta de tres servicios interconectados:

1.  **API REST (Spring Boot):**
    * Expone endpoints HTTP (Puerto 8080) para el cliente.
    * Maneja la lógica de negocio, autenticación y cifrado.
2.  **Base de Datos (PostgreSQL 15):**
    * Almacena usuarios y credenciales.
    * Configurada con volúmenes persistentes para evitar pérdida de datos.
    * **Seguridad:** No expuesta a internet (Puerto cerrado), solo accesible por la API.
3.  **Gestión de Claves (LocalStack / KMS):**
    * Simula el servicio AWS KMS para el cifrado de grado militar.
    * Mantiene la "Llave Maestra" que cifra las contraseñas en la base de datos.

## Despliegue (Instalación)

### Prerrequisitos
* Servidor Linux (Ubuntu recomendado, ej. EC2 `t2.micro`).
* Docker y Docker Compose instalados (V2).

### Pasos para levantar el servicio

1.  **Clonar/Copiar** este repositorio al servidor.
2.  **Configuración de Entorno:**
    * Revisar el archivo `docker-compose.yml`.
    * **Importante:** Asegurarse de que la variable PASSMAN_KMS_ARN apunte al alias fijo alias/passman-key.
3. **Configurar Permisos (¡CRÍTICO!):** Para que la persistencia de claves funcione y el script de inicialización se ejecute correctamente, debes ejecutar estos comandos antes de levantar los servicios:
    ```bash
    # 1. Dar permisos de ejecución al script de auto-configuración de KMS
    chmod +x init-kms.sh
    
    # 2. Asegurar permisos de escritura para la persistencia de LocalStack
    # (Evita que se pierdan las claves al reiniciar el contenedor)
    mkdir -p localstack-data
    sudo chmod -R 777 localstack-data
    ```
4.  **Ejecutar:**
    ```bash
    sudo docker compose up -d --build
    ```
5.  **Verificación:**
    Ejecutar `sudo docker ps` y confirmar que los 3 contenedores (`passman-api`, `passman-db`, `passman-kms`) tienen estado "Up".

##  Endpoints Principales

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Autenticación de usuarios. |
| `POST` | `/api/auth/registro` | Creación de nuevas cuentas. |
| `GET` | `/api/credenciales/listar` | Obtiene la bóveda descifrada. |
| `POST` | `/api/credenciales/guardar` | Cifra y almacena una contraseña. |
| `POST` | `/api/credenciales/editar` | Actualiza una credencial existente. |
| `POST` | `/api/credenciales/eliminar` | Elimina una credencial. |
| `POST` | `/api/credenciales/evaluar` | Analiza fortaleza y filtraciones (HIBP). |

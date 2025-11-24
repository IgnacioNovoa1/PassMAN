# **Ramón:**
    Agrege todo lo necesario para manejo de datos, cifrado y hashing:

## **Modelos de Datos ('passman.modelo'):**
**Usuario.java:** Molde de Usuario tipo POJO / Representa la tabla 'Usuarios' de la DB. 
        IdUsuario INMUTABLE.
   * **EntradaCredecial.java:** Representa una fila de la tabla 'Credenciales',
        solo "servicio", "usuarioservicio", passwordCifrada" son mutables.

## **Cifrado ('passman.cifrado'):**
   * **ServicioHashing.java:** Hashea la contraseña maestra de inicio de sesion; 
        **Funciones Clave**:
        - `hashPassword()`: Genera un nuevo hash y salt en Base 64 para el registro.
        - `verificarPassword()`: Compara un intengo de login con el hash guardado en la DB.
   * **ServicioCifrado.java:** Cifra datos sensibles de usuario / Es el cliente que conecta con KMS EC2.
        **Funciones Clave:**
        - `cifrar()`: Toma un texto plano y devuelve un string Base64 cifrado.
        - `descifrar()`: Toma el string cifrado en Base64 de la DB y lo devuelve como texto plano.
   * **ServicioPersistencia.java:** Conecta con la BD.
        **Funciones Clave:**
        - `conectar()`: Establece una conexión con la BD.
        - `buscarUsuarioPorNombre()`: Realiza un 'SELECT' para el login de usuario.
        - `guardarUsuario()`: Realiza un 'INSERT' para el registro de usuario.
        - `guardarCredencial()`: Realiza un 'INSERT' en la tabla de "Credenciales".
        - `actualizarCredencial()`: Realiza un 'UPDATE' para editar una credencial.
        - `eliminarCredencial()`: Realiza un 'DELETE' para borrar una credencial.

## **CONFIGURACIÓN ('passman.Condig'):**
   * **Config.java:** Carga la configuracion segura. Lee variables de entorno del sistema para conectarse a los servicios AWS y DB.

### **CONSIDERAR: Variables aun no definidas, seran utilizadas en entonos de pruebas. Antes de ejecutar se deben configurar estas variables.** ###



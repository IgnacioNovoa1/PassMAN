package passman.model;

public class Usuario {
    private String nombreUsuario;
    private String contraseñaHash; 
    public Usuario(String nombreUsuario, String contraseñaHash, String salt) {
        this.nombreUsuario = nombreUsuario;
        this.contraseñaHash = contraseñaHash;
         // this.salt = salt; (vacio mientras falte hash)
    }

    public String getNombreUsaurio() { return nombreUsuario; }
    public String getContraseñaHash() { return contraseñaHash; }
    // public String getSalt() { return salt; }
}

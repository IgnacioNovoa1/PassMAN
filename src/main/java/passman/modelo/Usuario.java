package passman.modelo;
import java.util.UUID;
public class Usuario {
    //Datos Inicio Sesión
    private final UUID idUsuario;
    private String nombreUsuario;
    private String passwordHash;
    private String salt;
    private int iteraciones;
    //Datos personales
    private String nombreCifrado;
    private String apellidoCifrado;
    private String rutCifrado;
    private String fechaNacCifrada;
    private String ivPersonales;
    
    ///CONSTRUCTOR usuario NUEVO
    public Usuario(String nomUser, String passHash, String salt, int iterac){
        this.idUsuario = UUID.randomUUID();
        this.passwordHash = passHash;
        this.salt = salt;
        this.iteraciones = iterac;
    }
    ///CONSTRUCTOR usuario existente en BD
    public Usuario(UUID idUsuario, String nomUser, String passHash, String salt, int iterac){
        this.idUsuario = idUsuario;
        this.nombreUsuario = nomUser;
        this.passwordHash = passHash;
        this.salt = salt;
        this.iteraciones = iterac;
    }
    ///GETTERS y SETTERS
    public UUID getIdUsuario(){
        return idUsuario;
    }
    public String getNombreUsuario(){
        return nombreUsuario;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
    public String getSalt (){
        return salt;
    }
    public int iteraciones() {
        return iteraciones;
    }
    public String getNombreCifrado() {
        return nombreCifrado;
    }
    public String getApellidoCifrad(){
        return apellidoCifrado;
    }
    public String getRutCifrado(){
        return rutCifrado;
    }
    public String getFechaNacCifrada(){
        return fechaNacCifrada;
    }
    public String gerIvPersonales(){
        return ivPersonales;
    }
    public void setPasswordHash(String passHash){
        this.passwordHash = passHash;
    }
    public void setSalt(String salt){
        this.salt = salt;
    }
    public void setIteraciones(int iterac){
        this.iteraciones = iterac;
    }
    public void setNombreCifrado(String nom){
        this.nombreCifrado = nom;
    }
    public void setRutCifrado(String rutCifrado){
        this.rutCifrado = rutCifrado;
    }
    public void setFechaNacCifrado(String fechaNacCifrada){
        this.fechaNacCifrada = fechaNacCifrada;
    }
    public void setIvPersonales(String ivPersonales){
        this.ivPersonales = ivPersonales;
    }
}


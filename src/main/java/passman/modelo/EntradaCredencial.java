package passman.modelo;
import java.util.UUID;
public class EntradaCredencial {
    private final UUID idCredencial;
    private final UUID idUsuario;
    private String servicio;
    private String usuarioServicio;
    private String passwordCifrada;
    private String iv;

    ///CREDENCIAL NUEVA
    public EntradaCredencial(UUID idUsuario, String servicio, String usuarioServicio, String passCif, String iv){
        this.idCredencial = UUID.randomUUID();
        this.idUsuario = idUsuario;
        this.servicio = servicio;
        this.usuarioServicio = usuarioServicio;
        this.passwordCifrada = passCif;
        this.iv = iv;
    }
    ///CREDENCIAL existente en BD
    public EntradaCredencial(UUID idCredencial, UUID idUsuario, String servicio, String usuarioServicio, String passCif, String iv){
        this.idCredencial = idCredencial;
        this.idUsuario = idUsuario;
        this.servicio = servicio;
        this.usuarioServicio = usuarioServicio;
        this.passwordCifrada = passCif;
        this.iv = iv;
    }
    ///GETTERS y SETTERS solo MUTABLES
    public UUID getIdCredencial(){
        return idCredencial;
    }
    public UUID getIdUsuario(){
        return idUsuario;
    }
    public String getServicio(){
        return servicio;
    }
    public String getUsuarioServicio(){
        return usuarioServicio;
    }
    public String getPasswordCifrada(){
        return passwordCifrada;
    }
    public String getIv(){
        return iv;
    }
    public void setServicio(String servicio){
        this.servicio = servicio;
    }
    public void setUsuarioServicio(String userServicio){
        this.usuarioServicio = userServicio;
    }
    public void setPasswordCifrada(String passCif){
        this.passwordCifrada = passCif;
    }
    public void setIv(String iv){
        this.iv = iv;
    }
    @Override
    public String toString(){
        return String.format("Servicio: %s (Usuario: %s)", servicio, usuarioServicio);
    }


}
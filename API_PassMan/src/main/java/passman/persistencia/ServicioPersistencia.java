package passman.persistencia;

import passman.Config;
import passman.modelo.EntradaCredencial;
import passman.modelo.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ServicioPersistencia {

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(Config.DB_URL, Config.DB_USER, Config.DB_PASS);
    }
    ///MANEJO DE USUARIOS
    public Optional<Usuario> buscarUsuarioPorNombre(String nombreUsuario) {
        String sql = "SELECT * FROM \"Usuarios\" WHERE nombre_usuario = ?";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombreUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario(
                        rs.getObject("id_usuario", UUID.class),
                        rs.getString("nombre_usuario"),
                        rs.getString("password_hash"),
                        rs.getString("salt"),
                        rs.getInt("iteraciones")
                    );
                    usuario.setNombreCifrado(rs.getString("nombre_cifrado"));
                    usuario.setApellidoCifrado(rs.getString("apellido_cifrado"));
                    usuario.setRutCifrado(rs.getString("rut_cifrado"));
                    usuario.setFechaNacCifrado(rs.getString("fecha_nac_cifrada"));
                    usuario.setIvPersonales(rs.getString("iv_personales"));
                    return Optional.of(usuario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public boolean guardarUsuario(Usuario usuario) {
        String sql = "INSERT INTO \"Usuarios\" (id_usuario, nombre_usuario, password_hash, salt, iteraciones, " +
                    "nombre_cifrado, apellido_cifrado, rut_cifrado, fecha_nac_cifrada, iv_personales) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, usuario.getIdUsuario());
            pstmt.setString(2, usuario.getNombreUsuario());
            pstmt.setString(3, usuario.getPasswordHash());
            pstmt.setString(4, usuario.getSalt());
            pstmt.setInt(5, usuario.getIteraciones());
            pstmt.setString(6, usuario.getNombreCifrado());
            pstmt.setString(7, usuario.getApellidoCifrado());
            pstmt.setString(8, usuario.getRutCifrado());
            pstmt.setString(9, usuario.getFechaNacCifrada());
            pstmt.setString(10, usuario.getIvPersonales());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    ///MANEJO DE CREDENCIALES
    public boolean guardarCredencial(EntradaCredencial cred) {
        String sql = "INSERT INTO \"Credenciales\" (id_credencial, id_usuario, servicio, usuario_servicio, password_cifrada, iv) " + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, cred.getIdCredencial());
            pstmt.setObject(2, cred.getIdUsuario());
            pstmt.setString(3, cred.getServicio());
            pstmt.setString(4, cred.getUsuarioServicio());
            pstmt.setString(5, cred.getPasswordCifrada());
            pstmt.setString(6, cred.getIv());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE \"Usuarios\" SET " +
                    "password_hash = ?, salt = ?, iteraciones = ?, " +
                    "nombre_cifrado = ?, apellido_cifrado = ?, rut_cifrado = ?, " +
                    "fecha_nac_cifrada = ?, iv_personales = ? " +
                    "WHERE id_usuario = ?";
        
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario.getPasswordHash());
            pstmt.setString(2, usuario.getSalt());
            pstmt.setInt(3, usuario.getIteraciones());
            pstmt.setString(4, usuario.getNombreCifrado());
            pstmt.setString(5, usuario.getApellidoCifrado());
            pstmt.setString(6, usuario.getRutCifrado());
            pstmt.setString(7, usuario.getFechaNacCifrada());
            pstmt.setString(8, usuario.getIvPersonales());
            pstmt.setObject(9, usuario.getIdUsuario()); 

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<EntradaCredencial> cargarCredenciales(UUID idUsuario) {
        List<EntradaCredencial> lista = new ArrayList<>();
        String sql = "SELECT * FROM \"Credenciales\" WHERE id_usuario = ?";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, idUsuario);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new EntradaCredencial(
                        rs.getObject("id_credencial", UUID.class),
                        rs.getObject("id_usuario", UUID.class),
                        rs.getString("servicio"),
                        rs.getString("usuario_servicio"),
                        rs.getString("password_cifrada"),
                        rs.getString("iv")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean actualizarCredencial(EntradaCredencial cred) {
        String sql = "UPDATE \"Credenciales\" SET servicio = ?, usuario_servicio = ?, password_cifrada = ?, iv = ? " + "WHERE id_credencial = ?";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cred.getServicio());
            pstmt.setString(2, cred.getUsuarioServicio());
            pstmt.setString(3, cred.getPasswordCifrada());
            pstmt.setString(4, cred.getIv());
            pstmt.setObject(5, cred.getIdCredencial());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarCredencial(UUID idCredencial) {
        String sql = "DELETE FROM \"Credenciales\" WHERE id_credencial = ?";
        try (Connection conn = conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, idCredencial);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

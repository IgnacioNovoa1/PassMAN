package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class LoginVentana extends JFrame implements ActionListener {

    private final ControladorPrincipal controlador;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;

    public LoginVentana(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setTitle("PassMan - Inicio de Sesión");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoUsuario = new JTextField(20);
        campoPassword = new JPasswordField(20);
        btnLogin = new JButton("Ingresar");
        btnRegistrar = new JButton("Registrarse"); 
        etiquetaMensaje = new JLabel(" "); 
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Arial", Font.BOLD, 12));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(campoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(campoPassword, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistrar);
        
        JButton btnRecuperar = new JButton("¿Olvidaste tu contraseña?");
        btnRecuperar.setBorderPainted(false);
        btnRecuperar.setContentAreaFilled(false);
        btnRecuperar.setForeground(Color.BLUE);
        btnRecuperar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRecuperar.addActionListener(e -> controlador.abrirRecuperacion(this));

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        panel.add(panelBotones, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnRecuperar, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; 
        panel.add(etiquetaMensaje, gbc);

        add(panel, BorderLayout.CENTER);
        
        btnLogin.addActionListener(this); 
        btnRegistrar.addActionListener(this); 

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String usuario = campoUsuario.getText().trim();
            String password = new String(campoPassword.getPassword()).trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                mostrarMensaje("Debe ingresar usuario y contraseña.", Color.RED);
                return;
            }
            
            mostrarMensaje("Conectando...", Color.GRAY.darker()); 

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnLogin.setEnabled(false);

            new SwingWorker<Map<String, String>, Void>() {
                @Override
                protected Map<String, String> doInBackground() throws Exception {
                    return controlador.autenticarUsuarioMap(usuario, password);
                }
                
                @Override
                protected void done() {
                    try {
                        Map<String, String> respuesta = get();
                        String status = respuesta.get("status");
                        String mensaje = respuesta.get("mensaje");

                        if ("ok".equals(status)) {
                            mostrarMensaje("¡" + mensaje + "!", new Color(0, 100, 0)); 
                            
                            Timer timer = new Timer(1000, new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    controlador.abrirMenuPrincipal(usuario);
                                    dispose();
                                }
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            mostrarMensaje("Error: " + mensaje, Color.RED);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        mostrarMensaje("Error de conexión. Revisa el servidor.", Color.RED);
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                        btnLogin.setEnabled(true);
                    }
                }
            }.execute();

        } else if (e.getSource() == btnRegistrar) {
            controlador.abrirRegistro(this);
        }
    }
    
    private void mostrarMensaje(String texto, Color color) {
        etiquetaMensaje.setText(texto);
        etiquetaMensaje.setForeground(color);
    }
}

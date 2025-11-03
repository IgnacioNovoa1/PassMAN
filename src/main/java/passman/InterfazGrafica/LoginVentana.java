package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        setSize(400, 250); 
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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistrar);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        panel.add(panelBotones, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; 
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
            
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            btnLogin.setEnabled(false);

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                
                    return controlador.autenticarUsuario(usuario, password);
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            mostrarMensaje("¡Inicio de sesión exitoso!", Color.BLUE);
                            controlador.abrirMenuPrincipal(usuario);
                            dispose();
                        } else {
                            mostrarMensaje("Error: Usuario o contraseña incorrectos.", Color.RED);
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
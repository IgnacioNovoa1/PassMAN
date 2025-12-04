package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.io.IOException;

public class LoginVentana extends JFrame implements ActionListener {

    private final ControladorPrincipal controlador;
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;

    // Paleta visual dark profesional
    private static final Color COLOR_FONDO = new Color(35, 35, 35);
    private static final Color COLOR_TARJETA = new Color(50, 50, 50);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);
    private static final Color COLOR_PRIMARIO = new Color(45, 120, 255);
    private static final Color COLOR_BOTON_SECUNDARIO = new Color(80, 80, 80);

    public LoginVentana(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setTitle("PassMan - Inicio de Sesión");
        setSize(420, 310);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Fondo general
        JPanel fondo = new JPanel(new GridBagLayout());
        fondo.setBackground(COLOR_FONDO);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(COLOR_TARJETA);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoUsuario = new JTextField(20);
        campoPassword = new JPasswordField(20);

        // Estilo de los inputs
        campoUsuario.setBackground(new Color(30, 30, 30));
        campoUsuario.setForeground(COLOR_TEXTO);
        campoUsuario.setCaretColor(COLOR_TEXTO);
        campoUsuario.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        campoPassword.setBackground(new Color(30, 30, 30));
        campoPassword.setForeground(COLOR_TEXTO);
        campoPassword.setCaretColor(COLOR_TEXTO);
        campoPassword.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        btnLogin = new JButton("Ingresar");
        btnRegistrar = new JButton("Registrarse");

        // Estilo de botones principales
        estilizarBoton(btnLogin, COLOR_PRIMARIO, Color.WHITE);
        estilizarBoton(btnRegistrar, COLOR_BOTON_SECUNDARIO, COLOR_TEXTO);

        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiquetaMensaje.setForeground(COLOR_TEXTO);

        // Labels estilo dark
        JLabel labelUsuario = new JLabel("Usuario:");
        labelUsuario.setForeground(COLOR_TEXTO);

        JLabel labelPass = new JLabel("Contraseña:");
        labelPass.setForeground(COLOR_TEXTO);

        gbc.gridx = 0; gbc.gridy = 0;
        tarjeta.add(labelUsuario, gbc);

        gbc.gridx = 1;
        tarjeta.add(campoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        tarjeta.add(labelPass, gbc);

        gbc.gridx = 1;
        tarjeta.add(campoPassword, gbc);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        panelBotones.setBackground(COLOR_TARJETA);
        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistrar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        tarjeta.add(panelBotones, gbc);

        // Botón de recuperación estilo link minimalista
        JButton btnRecuperar = new JButton("¿Olvidaste tu contraseña?");
        btnRecuperar.setForeground(new Color(130, 160, 255));
        btnRecuperar.setBackground(COLOR_TARJETA);
        btnRecuperar.setBorderPainted(false);
        btnRecuperar.setFocusPainted(false);
        btnRecuperar.setContentAreaFilled(false);
        btnRecuperar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRecuperar.addActionListener(e -> controlador.abrirRecuperacion(this));

        gbc.gridy = 3;
        tarjeta.add(btnRecuperar, gbc);

        gbc.gridy = 4;
        tarjeta.add(etiquetaMensaje, gbc);

        fondo.add(tarjeta);

        add(fondo, BorderLayout.CENTER);

        btnLogin.addActionListener(this);
        btnRegistrar.addActionListener(this);

        setVisible(true);
    }

    private void estilizarBoton(JButton boton, Color bg, Color fg) {
        boton.setBackground(bg);
        boton.setForeground(fg);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(bg.darker()));
        boton.setPreferredSize(new Dimension(120, 32));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

            mostrarMensaje("Conectando...", Color.LIGHT_GRAY);

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
                            mostrarMensaje("¡" + mensaje + "!", new Color(0, 180, 0));

                            Timer timer = new Timer(1000, evt -> {
                                controlador.abrirMenuPrincipal(usuario);
                                dispose();
                            });
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            mostrarMensaje("Error: " + mensaje, Color.RED);
                        }
                    } catch (java.util.concurrent.ExecutionException ex) {
                        Throwable causa = ex.getCause();

                        if (causa instanceof IOException) {
                            mostrarMensaje("Error de conexión. El servidor no responde o hay problemas de red.", Color.RED);
                        } else if (causa != null) {
                            mostrarMensaje("Error interno del sistema: " + causa.getMessage(), Color.RED);
                            causa.printStackTrace();
                        } else {
                            mostrarMensaje("Ocurrió un error desconocido durante la autenticación.", Color.RED);
                        }

                    } catch (InterruptedException ex) {
                        mostrarMensaje("La operación de autenticación fue interrumpida.", Color.ORANGE);
                        Thread.currentThread().interrupt();
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

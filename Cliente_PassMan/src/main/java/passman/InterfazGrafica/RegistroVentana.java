package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class RegistroVentana extends JDialog implements ActionListener {

    private final ControladorPrincipal controlador;
    private JTextField campoUsuario, campoRut, campoCumpleanos;
    private JPasswordField campoPassword;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;

    // Mismo dark theme definido para LoginVentana
    private static final Color COLOR_FONDO = new Color(35, 35, 35);
    private static final Color COLOR_TARJETA = new Color(50, 50, 50);
    private static final Color COLOR_INPUT = new Color(30, 30, 30);
    private static final Color COLOR_TEXTO = new Color(230, 230, 230);
    private static final Color COLOR_BORDE_INPUT = new Color(70, 70, 70);
    private static final Color COLOR_PRIMARIO = new Color(45, 120, 255);

    public RegistroVentana(LoginVentana loginVentana, ControladorPrincipal controlador) {
        super(loginVentana, "PassMan - Registro", true);
        this.controlador = controlador;
        
        setSize(500, 450);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(loginVentana);

        // === PANEL PRINCIPAL ===
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(COLOR_FONDO);

        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        tarjeta.setBackground(COLOR_TARJETA);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // === CAMPOS DE TEXTO ===
        campoUsuario = crearInput();
        campoRut = crearInput();
        campoCumpleanos = crearInput();
        campoPassword = new JPasswordField();
        estilizarCampoPassword(campoPassword);

        campoUsuario.setPreferredSize(new Dimension(150, 20));
        campoRut.setPreferredSize(new Dimension(150, 20));
        campoCumpleanos.setPreferredSize(new Dimension(150, 20));
        campoPassword.setPreferredSize(new Dimension(150, 20));


        btnRegistrar = new JButton("Completar Registro");
        estilizarBoton(btnRegistrar);

        etiquetaMensaje = new JLabel("Ingrese sus datos.");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("SansSerif", Font.ITALIC, 13));
        etiquetaMensaje.setForeground(COLOR_TEXTO);

        int row = 0;

        // === FILAS ===
        agregarFila(tarjeta, gbc, row++, "Usuario:", campoUsuario);
        agregarFila(tarjeta, gbc, row++, "RUT (Ej: 12345678-9):", campoRut);
        agregarFila(tarjeta, gbc, row++, "Cumpleaños (DDMMYYYY):", campoCumpleanos);
        agregarFila(tarjeta, gbc, row++, "Contraseña Maestra:", campoPassword);

        // Espacio
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        tarjeta.add(Box.createVerticalStrut(10), gbc);

        // Botón registrar
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        tarjeta.add(btnRegistrar, gbc);

        // Mensaje inferior
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        tarjeta.add(etiquetaMensaje, gbc);

        panel.add(tarjeta);
        add(panel);

        btnRegistrar.addActionListener(this);
    }

    // ====================================================
    // ESTILOS
    // ====================================================

    private JTextField crearInput() {
        JTextField campo = new JTextField();
        campo.setBackground(COLOR_INPUT);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_INPUT));
        return campo;
    }

    private void estilizarCampoPassword(JPasswordField campo) {
        campo.setBackground(COLOR_INPUT);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_INPUT));
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(200, 35));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARIO.darker()));
    }

    private void agregarFila(JPanel panel, GridBagConstraints gbc, int row, String etiqueta, JComponent campo) {
        JLabel label = new JLabel(etiqueta);
        label.setForeground(COLOR_TEXTO);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        panel.add(campo, gbc);
    }

    // ====================================================
    // LÓGICA DE REGISTRO
    // ====================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegistrar) {
            
            String usuario = campoUsuario.getText().trim();
            String rut = campoRut.getText().trim();
            String cumpleanos = campoCumpleanos.getText().trim();
            String password = new String(campoPassword.getPassword()).trim();

            btnRegistrar.setEnabled(false);
            etiquetaMensaje.setText("Procesando registro...");
            etiquetaMensaje.setForeground(Color.LIGHT_GRAY);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            new SwingWorker<Map<String, String>, Void>() {
                @Override
                protected Map<String, String> doInBackground() {
                    return controlador.registrarUsuarioMap(usuario, rut, cumpleanos, password);
                }
                
                @Override
                protected void done() {
                    try {
                        Map<String, String> resp = get();
                        if ("ok".equals(resp.get("status"))) {
                            String codigo = resp.get("codigoRecuperacion");

                            JTextArea areaCodigo = new JTextArea(
                                "¡Registro Exitoso!\n\n" +
                                "POR FAVOR, GUARDA ESTE CÓDIGO:\n" +
                                "--------------------------------\n" +
                                "   " + codigo + "\n" +
                                "--------------------------------\n\n" +
                                "Es la única forma de recuperar tu cuenta."
                            );
                            areaCodigo.setEditable(false);
                            areaCodigo.setFont(new Font("Monospaced", Font.BOLD, 14));
                            areaCodigo.setBackground(new Color(240, 240, 240));

                            JOptionPane.showMessageDialog(
                                RegistroVentana.this,
                                areaCodigo,
                                "Registro Completado",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            dispose();
                        } else {
                            String errorMsg = resp.get("mensaje");
                            etiquetaMensaje.setText(errorMsg);
                            etiquetaMensaje.setForeground(Color.RED);

                            JOptionPane.showMessageDialog(
                                RegistroVentana.this,
                                errorMsg,
                                "Error de Registro",
                                JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (Exception ex) {
                        etiquetaMensaje.setText("Error de conexión.");
                        etiquetaMensaje.setForeground(Color.RED);
                    } finally {
                        btnRegistrar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        }
    }
}
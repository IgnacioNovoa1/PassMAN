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

    public RegistroVentana(LoginVentana loginVentana, ControladorPrincipal controlador) {
        super(loginVentana, "PassMan - Nuevo Registro", true);
        this.controlador = controlador;
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(loginVentana);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoUsuario = new JTextField(20);
        campoRut = new JTextField(20);
        campoCumpleanos = new JTextField(20);
        campoPassword = new JPasswordField(20);
        btnRegistrar = new JButton("Completar Registro");
        etiquetaMensaje = new JLabel("Ingrese sus datos.");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("RUT (Ej: 12345678-9):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoRut, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Cumpleaños (4 dígitos, ej: 2510):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoCumpleanos, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Contraseña Maestra:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoPassword, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; panel.add(btnRegistrar, gbc);
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; panel.add(etiquetaMensaje, gbc);

        add(panel);
        btnRegistrar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegistrar) {
            String usuario = campoUsuario.getText().trim();
            String rut = campoRut.getText().trim();
            String cumpleanos = campoCumpleanos.getText().trim();
            String password = new String(campoPassword.getPassword()).trim();

            btnRegistrar.setEnabled(false);
            etiquetaMensaje.setText("Procesando...");
            
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
                                "GUARDA ESTE CÓDIGO DE RECUPERACIÓN:\n\n" +
                                ">>>  " + codigo + "  <<<\n\n" +
                                "Es la única forma de recuperar tu cuenta."
                            );
                            areaCodigo.setEditable(false);
                            areaCodigo.setFont(new Font("Monospaced", Font.BOLD, 14));
                            JOptionPane.showMessageDialog(RegistroVentana.this, areaCodigo, "Registro Completado", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            String errorMsg = resp.get("mensaje");
                            etiquetaMensaje.setText("<html><center>" + errorMsg + "</center></html>");
                            etiquetaMensaje.setForeground(Color.RED);
                            JOptionPane.showMessageDialog(RegistroVentana.this, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        etiquetaMensaje.setText("Error de conexión.");
                    } finally {
                        btnRegistrar.setEnabled(true);
                    }
                }
            }.execute();
        }
    }
}

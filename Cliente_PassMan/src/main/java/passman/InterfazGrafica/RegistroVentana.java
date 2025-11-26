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
        super(loginVentana, "PassMan - Registro", true);
        this.controlador = controlador;
        
        setSize(500, 450);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(loginVentana);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 

        campoUsuario = new JTextField();
        campoRut = new JTextField();
        campoCumpleanos = new JTextField();
        campoPassword = new JPasswordField();
        btnRegistrar = new JButton("Completar Registro");
        
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnRegistrar.setPreferredSize(new Dimension(200, 35));

        etiquetaMensaje = new JLabel("Ingrese sus datos.");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Arial", Font.ITALIC, 12));

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3; 
        panel.add(new JLabel("Usuario:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.7; 
        panel.add(campoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("RUT (Ej: 12345678-9):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.7;
        panel.add(campoRut, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Cumpleaños (DDMM, ej: 2510):"), gbc);
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.7;
        panel.add(campoCumpleanos, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Contraseña Maestra:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.7;
        panel.add(campoPassword, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(Box.createVerticalStrut(10), gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnRegistrar, gbc);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(etiquetaMensaje, gbc);

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
            etiquetaMensaje.setText("Procesando registro...");
            etiquetaMensaje.setForeground(Color.BLACK);
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
                            
                            JOptionPane.showMessageDialog(RegistroVentana.this, areaCodigo, 
                                    "Registro Completado", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            String errorMsg = resp.get("mensaje");
                            etiquetaMensaje.setText(errorMsg);
                            etiquetaMensaje.setForeground(Color.RED);
                            JOptionPane.showMessageDialog(RegistroVentana.this, errorMsg, "Error de Registro", JOptionPane.ERROR_MESSAGE);
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

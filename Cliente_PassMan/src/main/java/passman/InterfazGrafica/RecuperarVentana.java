package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RecuperarVentana extends JDialog {
    private final ControladorPrincipal controlador;
    private JTextField txtUsuario, txtCodigo;
    private JPasswordField txtNuevaPass;
    private JButton btnRecuperar;

    public RecuperarVentana(LoginVentana owner, ControladorPrincipal controlador) {
        super(owner, "Recuperar Cuenta", true);
        this.controlador = controlador;
        setSize(400, 300);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtUsuario = new JTextField(15);
        txtCodigo = new JTextField(15);
        txtNuevaPass = new JPasswordField(15);
        btnRecuperar = new JButton("Restablecer Contraseña");

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Código Recuperación:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Nueva Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(txtNuevaPass, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; panel.add(btnRecuperar, gbc);

        add(panel);

        btnRecuperar.addActionListener(e -> {
            String u = txtUsuario.getText().trim();
            String c = txtCodigo.getText().trim();
            String p = new String(txtNuevaPass.getPassword()).trim();

            if (u.isEmpty() || c.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new SwingWorker<Map<String, String>, Void>() {
                @Override
                protected Map<String, String> doInBackground() {
                    return controlador.recuperarUsuarioMap(u, c, p);
                }
                @Override
                protected void done() {
                    try {
                        Map<String, String> resp = get();
                        if ("ok".equals(resp.get("status"))) {
                            JOptionPane.showMessageDialog(RecuperarVentana.this, "¡Contraseña cambiada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(RecuperarVentana.this, resp.get("mensaje"), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RecuperarVentana.this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }
}

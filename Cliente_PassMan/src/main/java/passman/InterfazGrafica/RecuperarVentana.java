package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class RecuperarVentana extends JDialog {

    private JTextField txtUsuario, txtCodigo;
    private JPasswordField txtNuevaPass;
    private JButton btnRecuperar;

    // Paleta de colores para dark theme
    private final Color BG_DARK = new Color(35, 35, 35);
    private final Color FG_TEXT = new Color(250, 250, 250);
    private final Color FG_LABEL = new Color(200, 200, 200);
    private final Color FIELD_BG = new Color(56, 56, 56);
    private final Color BUTTON_BG = new Color(70, 70, 70);
    private final Color BUTTON_HOVER = new Color(90, 90, 90);

    public RecuperarVentana(LoginVentana owner, ControladorPrincipal controlador) {
        super(owner, "PassMan - Recuperar Cuenta", true);
        setSize(450, 320);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos más largos
        txtUsuario = new JTextField();
        txtCodigo = new JTextField();
        txtNuevaPass = new JPasswordField();

        // Set tamaño recomendado
        Dimension fieldSize = new Dimension(150, 22);
        txtUsuario.setPreferredSize(fieldSize);
        txtCodigo.setPreferredSize(fieldSize);
        txtNuevaPass.setPreferredSize(fieldSize);

        // Aplicar estilo dark a los campos
        estilizarCampo(txtUsuario);
        estilizarCampo(txtCodigo);
        estilizarCampo(txtNuevaPass);

        btnRecuperar = new JButton("Restablecer Contraseña");
        estilizarBoton(btnRecuperar);

        int row = 0;

        // Etiquetas con color claro
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(crearLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(crearLabel("Código de Recuperación:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(txtCodigo, gbc);

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(crearLabel("Nueva Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++;
        panel.add(txtNuevaPass, gbc);

        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnRecuperar, gbc);

        add(panel);

        // Lógica de acción
        btnRecuperar.addActionListener(e -> {
            String u = txtUsuario.getText().trim();
            String c = txtCodigo.getText().trim();
            String p = new String(txtNuevaPass.getPassword()).trim();

            if (u.isEmpty() || c.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnRecuperar.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                            JOptionPane.showMessageDialog(
                                RecuperarVentana.this,
                                "¡Contraseña cambiada con éxito!",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(RecuperarVentana.this, resp.get("mensaje"), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RecuperarVentana.this, "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnRecuperar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        });
    }

    // -------------------------------
    // Funciones de estilo (dark theme)
    // -------------------------------

    private JLabel crearLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(FG_LABEL);
        l.setFont(new Font("Arial", Font.PLAIN, 13));
        return l;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(FIELD_BG);
        campo.setForeground(FG_TEXT);
        campo.setCaretColor(FG_TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void estilizarCampo(JPasswordField campo) {
        campo.setBackground(FIELD_BG);
        campo.setForeground(FG_TEXT);
        campo.setCaretColor(FG_TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }

    private void estilizarBoton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(BUTTON_BG);
        b.setForeground(FG_TEXT);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(220, 35));
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // Hover
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(BUTTON_HOVER);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(BUTTON_BG);
            }
        });
    }
}
package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cambiado a JDialog para que sea una ventana modal
public class EditarContrasenaVentana extends JDialog implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;
    private JTextField campoIndice;
    private JPasswordField campoNuevaContrasena;
    private JButton btnEditar;

    public EditarContrasenaVentana(MenuVentana owner, ControladorPrincipal controlador) {
        super(owner, "Editar Contraseña Guardada", true); // Modal
        this.controlador = controlador;
        this.usuarioAutenticado = owner.getUsuarioAutenticado();

        setSize(450, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoIndice = new JTextField(5);
        campoNuevaContrasena = new JPasswordField(20);
        btnEditar = new JButton("Actualizar Contraseña");
        btnEditar.addActionListener(this);

        JLabel instruccion = new JLabel("<html>Primero vea el índice (No.) en la pestaña 'Ver Contraseñas'.</html>");
        instruccion.setForeground(Color.BLUE.darker());

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(instruccion, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1;
        panel.add(new JLabel("Número (índice) a editar:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoIndice, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(new JLabel("Nueva Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoNuevaContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(btnEditar, gbc);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEditar) {
            editarContrasena();
        }
    }

    private void editarContrasena() {
        try {
            int indiceSeleccionado = Integer.parseInt(campoIndice.getText().trim());
            int indiceReal = indiceSeleccionado - 1; // Ajuste a índice base 0
            String nuevaContrasena = new String(campoNuevaContrasena.getPassword()).trim();

            if (nuevaContrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "La nueva contraseña no puede estar vacía.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            btnEditar.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controlador.editarContrasena(usuarioAutenticado, indiceReal, nuevaContrasena);
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(EditarContrasenaVentana.this,
                                    "Contraseña #" + indiceSeleccionado + " actualizada con éxito.",
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            dispose(); // Cierra la ventana modal
                        } else {
                            JOptionPane.showMessageDialog(EditarContrasenaVentana.this,
                                    "Error: El número (índice) no es válido.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditarContrasenaVentana.this,
                                "Error de red al editar: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnEditar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, ingrese un número válido para el índice.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
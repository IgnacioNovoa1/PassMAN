package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EditarContrasenaVentana extends JDialog implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;
    private JTextField campoIndice;
    private JPasswordField campoNuevaContrasena;
    private JButton btnEditar;

    public EditarContrasenaVentana(MenuVentana owner, ControladorPrincipal controlador) {
        super(owner, "Editar Contraseña Guardada", true);
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
            int indiceReal = indiceSeleccionado - 1;
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
                            
                            if (getOwner() instanceof MenuVentana) {
                                ((MenuVentana) getOwner()).cargarBoveda();
                            }

                            dispose();
                            
                        } else {
                            JOptionPane.showMessageDialog(EditarContrasenaVentana.this,
                                    "Error: El número (índice) no es válido o la operación fue rechazada.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ExecutionException ex) {
                        Throwable causa = ex.getCause();
                        String errorMsg = "Error al comunicarse con el servidor.";
                        
                        if (causa instanceof IOException) {
                             errorMsg = "Error de Conexión: El servidor no responde al intentar editar.";
                             System.err.println("Error de I/O al editar: " + causa.getMessage());
                        } else if (causa != null) {
                            errorMsg = "Error Interno al editar: " + causa.getMessage();
                            causa.printStackTrace();
                        }
                        
                        JOptionPane.showMessageDialog(EditarContrasenaVentana.this, errorMsg, "Error de Red", JOptionPane.ERROR_MESSAGE);
                        
                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(EditarContrasenaVentana.this, "Operación de edición interrumpida.", "Error", JOptionPane.ERROR_MESSAGE);
                        Thread.currentThread().interrupt();
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
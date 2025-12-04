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

    // Paleta visual consistente con Login
    private final Color fondo = new Color(35, 35, 35);
    private final Color panelOscuro = new Color(40, 40, 40);
    private final Color textoClaro = new Color(250, 250, 250);
    private final Color azulResaltado = new Color(80, 150, 255);
    private final Font fuenteGeneral = new Font("SansSerif", Font.PLAIN, 14);
    private final Font fuenteNegrita = new Font("SansSerif", Font.BOLD, 14);

    public EditarContrasenaVentana(MenuVentana owner, ControladorPrincipal controlador) {
        super(owner, "Editar Contraseña Guardada", true);
        this.controlador = controlador;
        this.usuarioAutenticado = owner.getUsuarioAutenticado();

        setSize(480, 450);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        // Panel principal
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(fondo);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos estilizados
        campoIndice = new JTextField();
        campoIndice.setPreferredSize(new Dimension(80, 32));
        campoIndice.setFont(fuenteGeneral);
        campoIndice.setBackground(panelOscuro);
        campoIndice.setForeground(textoClaro);
        campoIndice.setCaretColor(textoClaro);
        campoIndice.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        campoNuevaContrasena = new JPasswordField();
        campoNuevaContrasena.setPreferredSize(new Dimension(220, 32));
        campoNuevaContrasena.setFont(fuenteGeneral);
        campoNuevaContrasena.setBackground(panelOscuro);
        campoNuevaContrasena.setForeground(textoClaro);
        campoNuevaContrasena.setCaretColor(textoClaro);
        campoNuevaContrasena.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));

        btnEditar = new JButton("Actualizar Contraseña");
        btnEditar.setFont(fuenteNegrita);
        btnEditar.setBackground(azulResaltado);
        btnEditar.setForeground(Color.WHITE);
        btnEditar.setFocusPainted(false);
        btnEditar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnEditar.addActionListener(this);

        JLabel instruccion = new JLabel(
                "<html>Primero vea el índice (No.) en la pestaña <b>Ver Contraseñas</b>.</html>"
        );
        instruccion.setFont(fuenteGeneral);
        instruccion.setForeground(azulResaltado);

        JLabel labelIndice = new JLabel("Número (índice) a editar:");
        labelIndice.setFont(fuenteGeneral);
        labelIndice.setForeground(textoClaro);

        JLabel labelNueva = new JLabel("Nueva Contraseña:");
        labelNueva.setFont(fuenteGeneral);
        labelNueva.setForeground(textoClaro);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(instruccion, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(labelIndice, gbc);

        gbc.gridx = 1;
        panel.add(campoIndice, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(labelNueva, gbc);

        gbc.gridx = 1;
        panel.add(campoNuevaContrasena, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
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
                            JOptionPane.showMessageDialog(
                                    EditarContrasenaVentana.this,
                                    "Contraseña #" + indiceSeleccionado + " actualizada con éxito.",
                                    "Éxito",
                                    JOptionPane.INFORMATION_MESSAGE
                            );

                            if (getOwner() instanceof MenuVentana) {
                                ((MenuVentana) getOwner()).cargarBoveda();
                            }

                            dispose();

                        } else {
                            JOptionPane.showMessageDialog(
                                    EditarContrasenaVentana.this,
                                    "Error: El número (índice) no es válido o la operación fue rechazada.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    } catch (ExecutionException ex) {
                        Throwable causa = ex.getCause();
                        String errorMsg = "Error al comunicarse con el servidor.";

                        if (causa instanceof IOException) {
                            errorMsg = "Error de Conexión: El servidor no responde al intentar editar.";
                            System.err.println("Error I/O al editar: " + causa.getMessage());
                        } else if (causa != null) {
                            errorMsg = "Error Interno al editar: " + causa.getMessage();
                            causa.printStackTrace();
                        }

                        JOptionPane.showMessageDialog(
                                EditarContrasenaVentana.this,
                                errorMsg,
                                "Error de Red",
                                JOptionPane.ERROR_MESSAGE
                        );

                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(
                                EditarContrasenaVentana.this,
                                "Operación de edición interrumpida.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        Thread.currentThread().interrupt();

                    } finally {
                        btnEditar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, ingrese un número válido para el índice.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

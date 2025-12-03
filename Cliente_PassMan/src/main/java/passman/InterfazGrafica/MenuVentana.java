package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MenuVentana extends JFrame implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;

    private JTabbedPane tabbedPane;
    private JTextArea areaBoveda;
    private JTextField campoServicio;
    private JPasswordField campoContrasenaNueva;
    private JButton btnGuardar;
    private JButton btnActualizarBoveda;
    private JButton btnCerrarSesion;

    public MenuVentana(String usuario, ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.usuarioAutenticado = usuario;

        setTitle("PassMan - Menú Principal | Usuario: " + usuario);
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnCerrarSesion = new JButton("Cerrar Sesión");

        btnCerrarSesion.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "¿Deseas cerrar sesión?",
                    "Confirmar Cierre de Sesión",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginVentana(controlador);
            }
        });

        panelSuperior.add(btnCerrarSesion);
        add(panelSuperior, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Ver Contraseñas", crearPanelBoveda());
        tabbedPane.addTab("Guardar Nueva", crearPanelGuardar());
        tabbedPane.addTab("Opciones Avanzadas", crearPanelOpciones());

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);

        areaBoveda.setText("Cargando bóveda...");
        cargarBoveda();
    }

    private JPanel crearPanelBoveda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaBoveda = new JTextArea();
        areaBoveda.setEditable(false);
        areaBoveda.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(areaBoveda);

        btnActualizarBoveda = new JButton("Actualizar Bóveda");

        btnActualizarBoveda.addActionListener(e -> {
            areaBoveda.setText("Cargando bóveda...");
            cargarBoveda();
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnActualizarBoveda, BorderLayout.SOUTH);

        return panel;
    }

    public void cargarBoveda() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnActualizarBoveda.setEnabled(false);

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return controlador.obtenerBovedaFormateada(usuarioAutenticado);
            }
            @Override
            protected void done() {
                try {
                    String bovedaFormateada = get();
                    areaBoveda.setText(bovedaFormateada);
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    if (causa instanceof IOException) {
                        areaBoveda.setText("Error de Conexión: No se pudo conectar con el servidor para obtener la bóveda.");
                        System.err.println("Error de I/O al cargar bóveda: " + causa.getMessage());
                    } else if (causa != null) {
                        areaBoveda.setText("Error Interno al cargar la bóveda: " + causa.getMessage());
                        causa.printStackTrace();
                    } else {
                        areaBoveda.setText("Error desconocido al cargar la bóveda.");
                    }
                } catch (InterruptedException ex) {
                    areaBoveda.setText("La operación de carga fue interrumpida.");
                    Thread.currentThread().interrupt();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    btnActualizarBoveda.setEnabled(true);
                }
            }
        }.execute();
    }

    public String getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    private JPanel crearPanelGuardar() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoServicio = new JTextField(25);
        campoContrasenaNueva = new JPasswordField(25);
        btnGuardar = new JButton("Guardar Contraseña");
        btnGuardar.addActionListener(this);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Guardar una nueva contraseña en la bóveda:"), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Nombre del Servicio/Web:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(campoServicio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(campoContrasenaNueva, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnGuardar, gbc);

        return panel;
    }

    private JPanel crearPanelOpciones() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnEditar = new JButton("Editar Contraseña Guardada");
        JButton btnEliminar = new JButton("Eliminar Contraseña");
        JButton btnEvaluar = new JButton("Evaluar/Verificar Contraseña");

        btnEditar.addActionListener(e -> {
            controlador.abrirEdicion(this);
        });

        btnEliminar.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese el número (No.) de la contraseña a eliminar:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int indiceVisual = Integer.parseInt(input.trim());
                    int indiceReal = indiceVisual - 1;

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "¿Estás seguro de borrar la contraseña #" + indiceVisual + "?",
                            "Confirmar", JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        new SwingWorker<Boolean, Void>() {
                            @Override
                            protected Boolean doInBackground() throws Exception {
                                return controlador.eliminarContrasena(usuarioAutenticado, indiceReal);
                            }
                            @Override
                            protected void done() {
                                setCursor(Cursor.getDefaultCursor());
                                try {
                                    if (get()) {
                                        JOptionPane.showMessageDialog(MenuVentana.this, "Eliminada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                        cargarBoveda();
                                    } else {
                                        JOptionPane.showMessageDialog(MenuVentana.this, "Error al eliminar. Índice inválido o error del servidor.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (ExecutionException ex) {
                                    Throwable causa = ex.getCause();
                                    String errorMsg = "Error al comunicarse con el servidor.";
                                    if (causa instanceof IOException) {
                                        errorMsg = "Error de conexión: El servidor no responde al intentar eliminar.";
                                    } else if (causa != null) {
                                        errorMsg = "Error interno al eliminar: " + causa.getMessage();
                                        causa.printStackTrace();
                                    }
                                    JOptionPane.showMessageDialog(MenuVentana.this, errorMsg, "Error de Red", JOptionPane.ERROR_MESSAGE);
                                } catch (InterruptedException ex) {
                                    JOptionPane.showMessageDialog(MenuVentana.this, "Operación de eliminación interrumpida.", "Error", JOptionPane.ERROR_MESSAGE);
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }.execute();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEvaluar.addActionListener(e -> controlador.abrirEvaluacion(this));

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Funcionalidades Adicionales:"), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(btnEditar, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(btnEliminar, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(btnEvaluar, gbc);

        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGuardar) {
            String servicio = campoServicio.getText().trim();
            String contrasena = new String(campoContrasenaNueva.getPassword()).trim();

            if (servicio.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el servicio y la contraseña.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            btnGuardar.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controlador.guardarContrasena(usuarioAutenticado, servicio, contrasena);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(MenuVentana.this, "¡Contraseña guardada exitosamente!",
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            campoServicio.setText("");
                            campoContrasenaNueva.setText("");
                            cargarBoveda();
                            tabbedPane.setSelectedIndex(0);
                        } else {
                            JOptionPane.showMessageDialog(MenuVentana.this, "Error al guardar la contraseña. (Servidor rechazó la operación).",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ExecutionException ex) {
                        Throwable causa = ex.getCause();
                        String errorMsg = "Error al comunicarse con el servidor.";
                        if (causa instanceof IOException) {
                             errorMsg = "Error de conexión: El servidor no responde al intentar guardar.";
                             System.err.println("Error de I/O al guardar contraseña: " + causa.getMessage());
                        } else if (causa != null) {
                            errorMsg = "Error interno al guardar: " + causa.getMessage();
                            causa.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(MenuVentana.this, errorMsg, "Error de Red", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        JOptionPane.showMessageDialog(MenuVentana.this, "Operación de guardado interrumpida.", "Error", JOptionPane.ERROR_MESSAGE);
                        Thread.currentThread().interrupt();
                    } finally {
                        btnGuardar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        }
    }
}
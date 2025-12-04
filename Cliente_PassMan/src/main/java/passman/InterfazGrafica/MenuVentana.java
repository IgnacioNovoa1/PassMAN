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

        // ======== DARK THEME GLOBAL ========
        Color fondo = new Color(35, 35, 35);
        Color panelOscuro = new Color(45, 45, 45);
        Color panelMasOscuro = new Color(30, 30, 30);
        Color textoClaro = new Color(250, 250, 250);

        UIManager.put("OptionPane.background", panelOscuro);
        UIManager.put("Panel.background", panelOscuro);
        UIManager.put("OptionPane.messageForeground", textoClaro);

        // ===================================
        //  TABS — ESTILO A (tema oscuro uniforme)
        // ===================================
        UIManager.put("TabbedPane.selected", panelOscuro); // evita blanco brillante
        UIManager.put("TabbedPane.contentAreaColor", panelOscuro);
        UIManager.put("TabbedPane.shadow", panelOscuro);
        UIManager.put("TabbedPane.darkShadow", panelOscuro);
        UIManager.put("TabbedPane.highlight", panelOscuro);
        UIManager.put("TabbedPane.light", panelOscuro);
        UIManager.put("TabbedPane.focus", panelOscuro);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(panelMasOscuro);
        tabbedPane.setForeground(textoClaro);
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabbedPane.setOpaque(true);

        tabbedPane.addTab("Ver Contraseñas", crearPanelBoveda());
        tabbedPane.addTab("Guardar Nueva", crearPanelGuardar());
        tabbedPane.addTab("Opciones Avanzadas", crearPanelOpciones());

        add(tabbedPane, BorderLayout.CENTER);
        getContentPane().setBackground(fondo);

        setVisible(true);

        areaBoveda.setText("Cargando bóveda...");
        cargarBoveda();
    }

    private JPanel crearPanelBoveda() {
        Color fondo = new Color(35, 35, 35);
        Color panelOscuro = new Color(45, 45, 45);
        Color botones = new Color(60, 60, 60);
        Color textoClaro = new Color(230, 230, 230);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(fondo);

        areaBoveda = new JTextArea();
        areaBoveda.setEditable(false);
        areaBoveda.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaBoveda.setBackground(panelOscuro);
        areaBoveda.setForeground(textoClaro);

        JScrollPane scrollPane = new JScrollPane(areaBoveda);
        scrollPane.getViewport().setBackground(panelOscuro);

        btnActualizarBoveda = new JButton("Actualizar Bóveda");
        btnActualizarBoveda.setBackground(botones);
        btnActualizarBoveda.setForeground(textoClaro);

        btnActualizarBoveda.addActionListener(e -> {
            areaBoveda.setText("Cargando bóveda...");
            cargarBoveda();
        });

        // Botón cerrar sesión simple (como pediste)
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(botones);
        btnCerrarSesion.setForeground(textoClaro);

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

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelBotones.setBackground(fondo);
        panelBotones.add(btnActualizarBoveda);
        panelBotones.add(btnCerrarSesion);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(panelBotones, BorderLayout.SOUTH);

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
                        areaBoveda.setText("Error de Conexión: No se pudo conectar con el servidor.");
                    } else if (causa != null) {
                        areaBoveda.setText("Error Interno: " + causa.getMessage());
                    } else {
                        areaBoveda.setText("Error desconocido.");
                    }
                } catch (InterruptedException ex) {
                    areaBoveda.setText("La operación fue interrumpida.");
                    Thread.currentThread().interrupt();
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                    btnActualizarBoveda.setEnabled(true);
                }
            }
        }.execute();
    }

    private JPanel crearPanelGuardar() {
        Color fondo = new Color(35, 35, 35);
        Color panelOscuro = new Color(45, 45, 45);
        Color botones = new Color(60, 60, 60);
        Color textoClaro = new Color(230, 230, 230);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(fondo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoServicio = new JTextField(25);
        campoServicio.setBackground(panelOscuro);
        campoServicio.setForeground(textoClaro);

        campoContrasenaNueva = new JPasswordField(25);
        campoContrasenaNueva.setBackground(panelOscuro);
        campoContrasenaNueva.setForeground(textoClaro);

        btnGuardar = new JButton("Guardar Contraseña");
        btnGuardar.setBackground(botones);
        btnGuardar.setForeground(textoClaro);
        btnGuardar.addActionListener(this);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lbl1 = new JLabel("Guardar una nueva contraseña:");
        lbl1.setForeground(textoClaro);
        panel.add(lbl1, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel lbl2 = new JLabel("Servicio:");
        lbl2.setForeground(textoClaro);
        panel.add(lbl2, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(campoServicio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lbl3 = new JLabel("Contraseña:");
        lbl3.setForeground(textoClaro);
        panel.add(lbl3, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(campoContrasenaNueva, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnGuardar, gbc);

        return panel;
    }

    private JPanel crearPanelOpciones() {
        Color fondo = new Color(35, 35, 35);
        Color botones = new Color(60, 60, 60);
        Color textoClaro = new Color(230, 230, 230);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(fondo);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnEditar = new JButton("Editar Contraseña Guardada");
        btnEditar.setBackground(botones);
        btnEditar.setForeground(textoClaro);

        JButton btnEliminar = new JButton("Eliminar Contraseña");
        btnEliminar.setBackground(botones);
        btnEliminar.setForeground(textoClaro);

        JButton btnEvaluar = new JButton("Evaluar/Verificar Contraseña");
        btnEvaluar.setBackground(botones);
        btnEvaluar.setForeground(textoClaro);

        btnEditar.addActionListener(e -> controlador.abrirEdicion(this));

        btnEliminar.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Ingrese el número (No.) de la contraseña a eliminar:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int indiceVisual = Integer.parseInt(input.trim());
                    int indiceReal = indiceVisual - 1;

                    int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "¿Seguro que deseas eliminar la contraseña #" + indiceVisual + "?",
                            "Confirmar",
                            JOptionPane.YES_NO_OPTION
                    );

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
                                        JOptionPane.showMessageDialog(MenuVentana.this, "Error al eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                                    }
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(MenuVentana.this, "Error de comunicación.", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }.execute();
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEvaluar.addActionListener(e -> controlador.abrirEvaluacion(this));

        JLabel lblOpc = new JLabel("Opciones avanzadas:");
        lblOpc.setForeground(textoClaro);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(lblOpc, gbc);

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
                            JOptionPane.showMessageDialog(MenuVentana.this,
                                    "¡Contraseña guardada exitosamente!",
                                    "Éxito", JOptionPane.INFORMATION_MESSAGE);

                            campoServicio.setText("");
                            campoContrasenaNueva.setText("");
                            cargarBoveda();
                            tabbedPane.setSelectedIndex(0);

                        } else {
                            JOptionPane.showMessageDialog(MenuVentana.this,
                                    "Error al guardar la contraseña.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (ExecutionException ex) {
                        JOptionPane.showMessageDialog(MenuVentana.this,
                                "Error de conexión.", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    } finally {
                        btnGuardar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        }
    }

    public String getUsuarioAutenticado() {
        return usuarioAutenticado;
    }
}
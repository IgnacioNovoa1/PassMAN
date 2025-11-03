package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuVentana extends JFrame implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;

    private JTabbedPane tabbedPane;
    private JTextArea areaBoveda;
    private JTextField campoServicio;
    private JPasswordField campoContrasenaNueva;
    private JButton btnGuardar;
    private JButton btnActualizarBoveda;

    public MenuVentana(String usuario, ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.usuarioAutenticado = usuario;

        setTitle("PassMan - Menú Principal | Usuario: " + usuario);
        setSize(700, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("🔑 Ver Contraseñas", crearPanelBoveda());
        tabbedPane.addTab("➕ Guardar Nueva", crearPanelGuardar());
        tabbedPane.addTab("⚙️ Opciones Avanzadas", crearPanelOpciones());
        
        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
        
        // Carga la bóveda automáticamente al abrir
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
        
        // --- ¡CORRECCIÓN BUG 5! ---
        // El listener ahora llama a cargarBoveda()
        btnActualizarBoveda.addActionListener(e -> {
            areaBoveda.setText("Cargando bóveda...");
            cargarBoveda();
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnActualizarBoveda, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarBoveda() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Tarea de fondo (Red)
                return controlador.obtenerBovedaFormateada(usuarioAutenticado);
            }
            @Override
            protected void done() {
                try {
                    // Tarea de UI (Actualizar)
                    String bovedaFormateada = get();
                    areaBoveda.setText(bovedaFormateada);
                } catch (Exception e) {
                    areaBoveda.setText("Error al cargar la bóveda: " + e.getMessage());
                } finally {
                    setCursor(Cursor.getDefaultCursor());
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
        JButton btnEvaluar = new JButton("Evaluar/Verificar Contraseña");
        
        // --- ¡CORRECCIÓN DE NAVEGACIÓN! ---
        // Los botones ahora llaman al controlador para abrir las ventanas
        
        btnEditar.addActionListener(e -> {
            controlador.abrirEdicion(this);
            // Al volver, actualiza la bóveda por si hubo cambios
            cargarBoveda();
        });
        
        btnEvaluar.addActionListener(e -> {
            controlador.abrirEvaluacion(this);
        });

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Funcionalidades Adicionales:"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(btnEditar, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
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
                            tabbedPane.setSelectedIndex(0); // Vuelve a la bóveda
                        } else {
                            JOptionPane.showMessageDialog(MenuVentana.this, "Error al guardar la contraseña.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MenuVentana.this, "Error de red al guardar: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnGuardar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        }
    }
}
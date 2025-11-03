package passman.InterfazGrafica;

import passman.nucleo.servicio.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MenuVentana extends JFrame implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;

    private JTabbedPane tabbedPane;
    private JTextArea areaBoveda; // Para la pestaña "Ver Contraseñas"
    private JTextField campoServicio;
    private JPasswordField campoContrasenaNueva;
    private JButton btnGuardar;
    private JButton btnActualizarBoveda;

    public MenuVentana(String usuario, ControladorPrincipal controlador) {
        this.controlador = controlador;
        this.usuarioAutenticado = usuario;

        // Configuración de la Ventana Principal
        setTitle("PassMan - Menú Principal | Usuario: " + usuario);
        setSize(700, 500); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        // Crear el Panel de Pestañas
        tabbedPane = new JTabbedPane();

        // Crear las Pestañas
        tabbedPane.addTab("🔑 Ver Contraseñas", crearPanelBoveda());
        tabbedPane.addTab("➕ Guardar Nueva", crearPanelGuardar());
        tabbedPane.addTab("⚙️ Opciones Avanzadas", crearPanelOpciones()); // Aquí irían "Editar" y "Evaluar"
        
        // Añadir el TabbedPane a la ventana
        add(tabbedPane, BorderLayout.CENTER);

        // Mostrar la ventana
        setVisible(true);
    }

    // Boveda de contraseñas
    private JPanel crearPanelBoveda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaBoveda = new JTextArea();
        areaBoveda.setEditable(false);
        areaBoveda.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(areaBoveda);
        
        btnActualizarBoveda = new JButton("Actualizar Bóveda");
        btnActualizarBoveda.addActionListener(e -> {
            areaBoveda.setText("Cargando...");
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnActualizarBoveda, BorderLayout.SOUTH);

        return panel;
    }

    private void cargarBoveda() {
        String bovedaFormateada = controlador.obtenerBovedaFormateada(usuarioAutenticado);
        mostrarBoveda(bovedaFormateada);
    }

    public void cargarBovedaYMostrar() {
        cargarBoveda();
        tabbedPane.setSelectedIndex(0);
    }

    public void mostrarBoveda(String datosFormateados){
        areaBoveda.setText(datosFormateados);
    }

    public String getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    // Guardar nueva contraseña
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

        // Título
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(new JLabel("Guardar una nueva contraseña en la bóveda:"), gbc);
        
        // Servicio
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panel.add(new JLabel("Nombre del Servicio/Web:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(campoServicio, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(campoContrasenaNueva, gbc);

        // Botón
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(btnGuardar, gbc);

        return panel;
    }
    
    // Opciones 
    private JPanel crearPanelOpciones() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton btnEditar = new JButton("Editar Contraseña Guardada");
        JButton btnEvaluar = new JButton("Evaluar Contraseña Maestra");
        
        // Listeners
        btnEditar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad de edición no implementada aún.", "En Desarrollo", JOptionPane.INFORMATION_MESSAGE);
        });
        
        btnEvaluar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Funcionalidad de evaluación no implementada aún.", "En Desarrollo", JOptionPane.INFORMATION_MESSAGE);
        });

        // Diseño
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Funcionalidades Adicionales:"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(btnEditar, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(btnEvaluar, gbc);

        return panel;}

    // Manejo de eventos (botones)
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

            if (controlador.guardarContrasena(usuarioAutenticado, servicio, contrasena)) {
                JOptionPane.showMessageDialog(this, "¡Contraseña guardada exitosamente!",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                campoServicio.setText("");
                campoContrasenaNueva.setText("");
                cargarBoveda();
                tabbedPane.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar la contraseña.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
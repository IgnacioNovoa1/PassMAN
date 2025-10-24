package passman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class MenuVentana extends JFrame implements ActionListener {

    private final PassManService service;
    private final String usuarioAutenticado;

    private JTabbedPane tabbedPane;
    private JTextArea areaBoveda; // Para la pestaña "Ver Contraseñas"
    private JTextField campoServicio;
    private JPasswordField campoContrasenaNueva;
    private JButton btnGuardar;

    public MenuVentana(String usuario, PassManService service) {
        this.usuarioAutenticado = usuario;
        this.service = service;

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
        
        // Cargar las contraseñas al iniciar
        cargarBoveda();
    }

    // Boveda de contraseñas
    private JPanel crearPanelBoveda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        areaBoveda = new JTextArea();
        areaBoveda.setEditable(false);
        areaBoveda.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(areaBoveda);
        
        JButton btnActualizar = new JButton("Actualizar Bóveda");
        btnActualizar.addActionListener(e -> cargarBoveda());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnActualizar, BorderLayout.SOUTH);

        return panel;
    }

    // Carga y muestra las contraseñas en la bóveda
    private void cargarBoveda() {
        areaBoveda.setText(""); // Limpiar el área
        
        // El servicio de archivos necesita ser ajustado para devolver la lista, no solo imprimir
        List<Map<String, String>> boveda = service.getContrasenasBoveda(usuarioAutenticado);

        if (boveda == null || boveda.isEmpty()) {
            areaBoveda.setText("No hay contraseñas guardadas en tu bóveda.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------------------------------------------------\n");
        sb.append(String.format("| %-4s | %-25s | %-30s |\n", "No.", "Servicio", "Contraseña Descifrada"));
        sb.append("-----------------------------------------------------------------------\n");

        for (int i = 0; i < boveda.size(); i++) {
            Map<String, String> entrada = boveda.get(i);
            String servicio = entrada.get("servicio");
            String contrasenaCifrada = entrada.get("contraseña");

            String contrasenaDescifrada = CifradoCesar.descifrar(contrasenaCifrada, 4); 

            sb.append(String.format("| %-4d | %-25s | %-30s |\n", i + 1, servicio, contrasenaDescifrada));
        }
        sb.append("-----------------------------------------------------------------------\n");
        
        areaBoveda.setText(sb.toString());
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
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Aquí irían las opciones de Editar Contraseñas y Evaluar Contraseña...", SwingConstants.CENTER), BorderLayout.CENTER);
        return panel;
    }

    // Manejo de eventos (botones)
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnGuardar) {
            String servicio = campoServicio.getText().trim();
            String contrasena = new String(campoContrasenaNueva.getPassword()).trim();

            if (servicio.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar el servicio y la contraseña.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Llama al servicio de guardado
            service.guardarContrasena(usuarioAutenticado, servicio, contrasena);

            JOptionPane.showMessageDialog(this, "¡Contraseña guardada exitosamente!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            // Limpiar campos y actualizar bóveda
            campoServicio.setText("");
            campoContrasenaNueva.setText("");
            cargarBoveda(); // Recargar la lista de contraseñas
            tabbedPane.setSelectedIndex(0); // Mover a la pestaña de la bóveda
        }
    }
}
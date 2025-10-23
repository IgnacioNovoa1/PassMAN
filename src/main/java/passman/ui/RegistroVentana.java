package passman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistroVentana extends JFrame implements ActionListener {

    // Componentes
    private JTextField campoUsuario;
    private JTextField campoRut;
    private JTextField campoCumpleanos;
    private JPasswordField campoPassword;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;

    // Dependencia del Servicio
    private final PassManService service;
    private final LoginVentana loginVentana; 

    // Constructor 
    public RegistroVentana(PassManService service, LoginVentana loginVentana) {
        this.service = service;
        this.loginVentana = loginVentana;
        
        // Configuración de la Ventana
        setTitle("PassMan - Nuevo Registro");
        setSize(450, 350); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra esta ventana, no la app
        setLocationRelativeTo(loginVentana); // Centra la ventana relativa a la Ventana Login

        // Inicializar y configurar el Panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización de Componentes
        campoUsuario = new JTextField(20);
        campoRut = new JTextField(20);
        campoCumpleanos = new JTextField(20);
        campoPassword = new JPasswordField(20);
        btnRegistrar = new JButton("Completar Registro");
        etiquetaMensaje = new JLabel("Ingrese sus datos para crear una cuenta en PassMan."); 
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Arial", Font.ITALIC, 12));

        // Diseño de la Ventana

        int row = 0;
        
        // Rtiqueta Usuario
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(new JLabel("Usuario:"), gbc);
        
        // Campo de Usuario
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoUsuario, gbc);

        // Etiqueta RUT
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(new JLabel("RUT (8 o 9 dígitos, sin guión):"), gbc);
        
        // Campo de RUT
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoRut, gbc);

        // Etiqueta Cumpleaños
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(new JLabel("Cumpleaños (DDMM):"), gbc);
        
        // Campo de Cumpleaños
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoCumpleanos, gbc);

        // Etiqueta Contraseña
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(new JLabel("Contraseña Maestra (4 dígitos):"), gbc);
        
        // Campo de Contraseña
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoPassword, gbc);

        // Botón de Registro 
        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; 
        panel.add(btnRegistrar, gbc);
        
        // Mensaje
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2; 
        panel.add(etiquetaMensaje, gbc);

        // Añadir el panel y el listener
        add(panel, BorderLayout.CENTER);
        btnRegistrar.addActionListener(this); 
    }

    // Eventos del botón
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegistrar) {
            registrarUsuario();
        }
    }
    
    // Recopilación de datos y llamado al servicio para registrar el usuario
    private void registrarUsuario() {
        String usuario = campoUsuario.getText().trim();
        String rut = campoRut.getText().trim();
        String cumpleanos = campoCumpleanos.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();

        // Validaciones 
        if (usuario.isEmpty() || rut.isEmpty() || cumpleanos.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Todos los campos son obligatorios.", Color.RED);
            return;
        }
        
        // Validación de RUT 
        if (!rut.matches("\\d{8,9}")) {
            mostrarMensaje("RUT inválido. Debe tener 8 o 9 dígitos (sin guión).", Color.RED);
            return;
        }

        // Validación de Cumpleaños 
        if (!cumpleanos.matches("\\d{4}")) {
            mostrarMensaje("Cumpleaños inválido. Use el formato DDMM.", Color.RED);
            return;
        }
        
        // Validación de Contraseña Maestra 
        if (!password.matches("\\d{4}")) {
            mostrarMensaje("La Contraseña Maestra debe ser de 4 dígitos.", Color.RED);
            return;
        }
        
        // Llamar a la lógica de registro del servicio
        boolean exito = service.registrarUsuario(usuario, rut, cumpleanos, password);

        if (exito) {
            mostrarMensaje("¡Registro exitoso! Ya puedes iniciar sesión.", Color.BLUE.darker());
            
            // Cerrar la ventana de registro y reabrir la de login
            Timer timer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); 
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            mostrarMensaje("Error: El nombre de usuario ya existe.", Color.RED);
        }
    }
    
    private void mostrarMensaje(String texto, Color color) {
        etiquetaMensaje.setText(texto);
        etiquetaMensaje.setForeground(color);
    }
}

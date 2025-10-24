package passman.ui.InterfazGrafica;

import javax.swing.*;

import passman.ui.PassManService;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Extendemos JFrame para crear la ventana e implementamos ActionListener para manejar el botón
public class LoginVentana extends JFrame implements ActionListener {

    // Componentes de la Interfaz
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;

    // Dependencia del Servicio
    private final PassManService service;

    public LoginVentana(PassManService service) {
        this.service = service;
        
        // Configuración básica de la Ventana
        setTitle("PassMan - Inicio de Sesión");
        setSize(400, 250); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centra la ventana

        // Inicializar y configurar el Panel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Márgenes entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Componentes
        campoUsuario = new JTextField(20);
        campoPassword = new JPasswordField(20);
        btnLogin = new JButton("Ingresar");
        btnRegistrar = new JButton("Registrarse"); // Botón para el flujo de registro
        etiquetaMensaje = new JLabel(" "); 
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Arial", Font.BOLD, 12));


        // Diseño con GridBagLayout

        // Etiqueta Usuario
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);

        // Campo de Usuario
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(campoUsuario, gbc);

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña (4 dígitos):"), gbc);

        // Campo de Contraseña
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(campoPassword, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistrar);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Ocupa las dos columnas
        panel.add(panelBotones, gbc);

        // Mensaje de error/éxito
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; 
        panel.add(etiquetaMensaje, gbc);

        // Añadir el panel al contenedor principal y hacerlo visible
        add(panel, BorderLayout.CENTER);
        
        // Listeners
        btnLogin.addActionListener(this); 
        btnRegistrar.addActionListener(this); 

        setVisible(true);
    }

    // Manejo de eventos de los botones
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            autenticarUsuario();
        } else if (e.getSource() == btnRegistrar) {
            RegistroVentana registro = new RegistroVentana(service, this);
            registro.setVisible(true);
        }
    }
    
    //Autenticación mediante PassManService
    private void autenticarUsuario() {
        String usuario = campoUsuario.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();

        // Valida el formato de la contraseña
        if (password.length() != 4 || !password.matches("\\d+")) {
            mostrarMensaje("La contraseña debe ser de 4 dígitos.", Color.RED);
            return;
        }
        
        // Llama a la lógica de autenticación del servicio
        if (service.iniciarSesion(usuario, password)) {
            mostrarMensaje("¡Inicio de sesión exitoso! Abriendo PassMan...", Color.BLUE);
            
            // Pausa breve para que el usuario vea el mensaje
            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose(); // Cierra la ventana de Login
                    
                    // LLama a la ventana principal
                    new MenuVentana(usuario, service).setVisible(true);
                }
            });
            timer.setRepeats(false); // Solo se ejecuta una vez
            timer.start();
            
        } else {
            mostrarMensaje("Error: Usuario o contraseña incorrectos.", Color.RED);
        }
    }
    
    // Método para mostrar el mensaje de las estiquetas
    private void mostrarMensaje(String texto, Color color) {
        etiquetaMensaje.setText(texto);
        etiquetaMensaje.setForeground(color);
    }
}
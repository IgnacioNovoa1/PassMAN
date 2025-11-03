package passman.InterfazGrafica;

import passman.nucleo.servicio.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Extendemos JFrame para crear la ventana e implementamos ActionListener para manejar el botón
public class LoginVentana extends JFrame implements ActionListener {

    private final ControladorPrincipal controlador;
    // Componentes de la Interfaz
    private JTextField campoUsuario;
    private JPasswordField campoPassword;
    private JButton btnLogin;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;


    public LoginVentana(ControladorPrincipal controlador) {
        this.controlador = controlador;
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
            String usuario = campoUsuario.getText().trim();
            String password = new String(campoPassword.getPassword()).trim();

            if (usuario.isEmpty() || password.isEmpty()) {
                mostrarMensaje("Debe ingresar usuario y contraseña.", Color.RED);
                return;
            }

            if (controlador.autenticarUsuario(usuario, password)) {
                mostrarMensaje("¡Inicio de sesión exitoso!", Color.BLUE);
                controlador.abrirMenuPrincipal(usuario);
            } else {
                mostrarMensaje("Error: Usuario o contraseña incorrectos.", Color.RED);
            }
        } else if (e.getSource() == btnRegistrar) {
            controlador.abrirRegistro(this);
        }
    }
    
    // Método para mostrar el mensaje de las estiquetas
    private void mostrarMensaje(String texto, Color color) {
        etiquetaMensaje.setText(texto);
        etiquetaMensaje.setForeground(color);
    }
}
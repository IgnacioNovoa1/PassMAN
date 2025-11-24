package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistroVentana extends JDialog implements ActionListener {

    private final ControladorPrincipal controlador;
    private JTextField campoUsuario;
    private JTextField campoRut;
    private JTextField campoCumpleanos;
    private JPasswordField campoPassword;
    private JButton btnRegistrar;
    private JLabel etiquetaMensaje;

    public RegistroVentana(LoginVentana loginVentana, ControladorPrincipal controlador) {
        super(loginVentana, "PassMan - Nuevo Registro", true); // Modal
        this.controlador = controlador;
        
        setSize(450, 350); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(loginVentana); 

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Margen
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoUsuario = new JTextField(20);
        campoRut = new JTextField(20);
        campoCumpleanos = new JTextField(20);
        campoPassword = new JPasswordField(20);
        btnRegistrar = new JButton("Completar Registro");
        etiquetaMensaje = new JLabel("Ingrese sus datos para crear una cuenta en PassMan."); 
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Arial", Font.ITALIC, 12));

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("RUT (8 o 9 dígitos, sin guión):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoRut, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Cumpleaños (DDMM):"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoCumpleanos, gbc);

        gbc.gridx = 0; gbc.gridy = row; panel.add(new JLabel("Contraseña Maestra:"), gbc);
        gbc.gridx = 1; gbc.gridy = row++; panel.add(campoPassword, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; 
        panel.add(btnRegistrar, gbc);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; 
        panel.add(etiquetaMensaje, gbc);

        add(panel, BorderLayout.CENTER);
        btnRegistrar.addActionListener(this); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegistrar) {
            String usuario = campoUsuario.getText().trim();
            String rut = campoRut.getText().trim();
            String cumpleanos = campoCumpleanos.getText().trim();
            String password = new String(campoPassword.getPassword()).trim();

            if (usuario.isEmpty() || rut.isEmpty() || cumpleanos.isEmpty() || password.isEmpty()) {
                mostrarMensaje("Todos los campos son obligatorios.", Color.RED);
                return;
            }
            
            // Deshabilita el botón mientras se registra
            btnRegistrar.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return controlador.registrarUsuario(usuario, rut, cumpleanos, password);
                }
                
                @Override
                protected void done() {
                    try {
                        if (get()) {
                            mostrarMensaje("¡Registro exitoso! Ya puedes iniciar sesión.", Color.BLUE);
                            // Espera 2 segundos y cierra
                            Timer timer = new Timer(2000, ae -> dispose());
                            timer.setRepeats(false);
                            timer.start();
                        } else {
                            mostrarMensaje("Error: El nombre de usuario ya existe.", Color.RED);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        mostrarMensaje("Error de conexión al registrar.", Color.RED);
                    } finally {
                        btnRegistrar.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        }
    }
    
    private void mostrarMensaje(String texto, Color color) {
        etiquetaMensaje.setText(texto);
        etiquetaMensaje.setForeground(color);
    }
}
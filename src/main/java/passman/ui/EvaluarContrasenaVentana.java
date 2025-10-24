package passman.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvaluarContrasenaVentana extends JDialog implements ActionListener {

    private final PassManService service;
    private final String usuarioAutenticado;

    private JPasswordField campoContrasena;
    private JButton btnEvaluar;
    private JLabel etiquetaMensaje;
    private JLabel etiquetaSugerencia;

    
    public EvaluarContrasenaVentana(JFrame owner, PassManService service, String usuario) {
        super(owner, "Evaluar Contraseña Maestra", true); 
        this.service = service;
        this.usuarioAutenticado = usuario;

        // Configuración de la Ventana
        setSize(450, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        // Panel Principal con GridBagLayout para buen control
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización de Componentes
        campoContrasena = new JPasswordField(15);
        btnEvaluar = new JButton("Evaluar");
        etiquetaMensaje = new JLabel("Ingrese una contraseña de 4 dígitos a evaluar:");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaSugerencia = new JLabel(" "); 
        
        btnEvaluar.addActionListener(this);

        // Diseño 
        int row = 0;
        
        // Etiqueta de instrucción
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(etiquetaMensaje, gbc);

        // Campo de Contraseña
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1;
        panel.add(new JLabel("Contraseña (4 dígitos):"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoContrasena, gbc);

        // Botón Evaluar
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(btnEvaluar, gbc);
        
        // Mensaje de Resultado
        gbc.gridx = 0; gbc.gridy = row++;
        etiquetaSugerencia.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(etiquetaSugerencia, gbc);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEvaluar) {
            evaluarContrasena();
        }
    }

    private void evaluarContrasena() {
        String contrasena = new String(campoContrasena.getPassword()).trim();
        etiquetaSugerencia.setText(" "); 

        // Validar el formato (debe ser 4 dígitos)
        if (contrasena.length() != 4 || !contrasena.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, 
                "El formato es inválido. Ingrese exactamente 4 dígitos.", 
                "Error de Formato", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Llamar al servicio de evaluación
        if (service.esContrasenaDebil(contrasena, usuarioAutenticado)) {
            etiquetaMensaje.setText("Resultado: Contraseña DÉBIL. ¡Cámbiala!");
            etiquetaMensaje.setForeground(Color.RED.darker());

            // Generar Sugerencia
            String sugerencia = service.generarContrasenaFuerte(usuarioAutenticado);
            etiquetaSugerencia.setText("Sugerencia segura: " + sugerencia);
            etiquetaSugerencia.setForeground(Color.BLUE.darker());
        } else {
            etiquetaMensaje.setText("Resultado: Contraseña SEGURA.");
            etiquetaMensaje.setForeground(Color.GREEN.darker());
            etiquetaSugerencia.setText(" "); 
        }
    }
}
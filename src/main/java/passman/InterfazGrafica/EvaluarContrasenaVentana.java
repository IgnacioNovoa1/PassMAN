package passman.ui.InterfazGrafica;

import passman.controlador.ControladorPrincipal;
import passman.servicio.ServicioPassman;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EvaluarContrasenaVentana extends JDialog implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;

    private JPasswordField campoContrasena;
    private JButton btnEvaluar;
    private JLabel etiquetaMensaje;
    private JLabel etiquetaSugerencia;

    
    public EvaluarContrasenaVentana(MenuVentana owner, ControladorPrincipal controlador) {
        super(owner, "Evaluar Contraseña Maestra", true);
        this.controlador = controlador;
        this.usuarioAutenticado = owner.getUsuarioAutenticado();

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

        if (contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una contraseña para evaluar.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String resultado = controlador.evaluarContrasena(contrasena, usuarioAutenticado);
        String[] partes = resultado.split("\\|");

        if (partes[0].equals("DÉBIL")) {
            etiquetaMensaje.setText("Resultado: Contraseña DÉBIL. ¡Cámbiala!");
            etiquetaMensaje.setForeground(Color.RED);
            etiquetaSugerencia.setText("Sugerencia segura: " + partes[1]);
            etiquetaSugerencia.setForeground(Color.BLUE);
        } else {
            etiquetaMensaje.setText("Resultado: Contraseña FUERTE.");
            etiquetaMensaje.setForeground(Color.GREEN);
            etiquetaSugerencia.setText(" ");
        }
    }
}
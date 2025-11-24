package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;

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
        super(owner, "Evaluar Contraseña", true); // Modal
        this.controlador = controlador;
        this.usuarioAutenticado = owner.getUsuarioAutenticado();

        setSize(450, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoContrasena = new JPasswordField(15);
        btnEvaluar = new JButton("Evaluar");
        etiquetaMensaje = new JLabel("Ingrese una contraseña a evaluar:");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaSugerencia = new JLabel(" "); 
        
        btnEvaluar.addActionListener(this);

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(etiquetaMensaje, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(btnEvaluar, gbc);
        
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

        btnEvaluar.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                // Esta es la llamada de red/cómputo (lenta)
                return controlador.evaluarContrasena(contrasena, usuarioAutenticado);
            }
            
            @Override
            protected void done() {
                try {
                    String resultado = get();
                    String[] partes = resultado.split("\\|");

                    if (partes[0].equals("DÉBIL")) {
                        etiquetaMensaje.setText("<html>Resultado: Contraseña DÉBIL.</html>");
                        etiquetaMensaje.setForeground(Color.RED);
                        // Muestra el mensaje (que ahora puede ser la sugerencia o la razón)
                        etiquetaSugerencia.setText("<html>" + partes[1] + "</html>"); 
                        etiquetaSugerencia.setForeground(Color.BLUE.darker());
                    } else { // FUERTE
                        etiquetaMensaje.setText("<html>Resultado: Contraseña FUERTE.</html>");
                        etiquetaMensaje.setForeground(new Color(0, 100, 0)); // Verde oscuro
                        etiquetaSugerencia.setText("<html>" + partes[1] + "</html>");
                        etiquetaSugerencia.setForeground(new Color(0, 100, 0));
                    }
                } catch (Exception ex) {
                    etiquetaMensaje.setText("Error de conexión con la API HIBP.");
                    etiquetaMensaje.setForeground(Color.RED);
                    etiquetaSugerencia.setText(" ");
                } finally {
                    btnEvaluar.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }
}
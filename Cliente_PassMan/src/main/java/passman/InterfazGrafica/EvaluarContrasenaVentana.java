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

        setSize(450, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        campoContrasena = new JPasswordField(15);
        btnEvaluar = new JButton("Evaluar");
        etiquetaMensaje = new JLabel(" "); 
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel etiquetaContrasena = new JLabel("Contraseña:");
        etiquetaSugerencia = new JLabel(" "); 
        etiquetaSugerencia.setVerticalAlignment(SwingConstants.TOP);
        etiquetaSugerencia.setFont(new Font("Arial", Font.PLAIN, 12));
        
        btnEvaluar.addActionListener(this);

        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.gridwidth = 2;
        panel.add(etiquetaContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.gridwidth = 2;
        panel.add(campoContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.gridwidth = 2; 
        panel.add(btnEvaluar, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; 
        gbc.weighty = 0.0;
        panel.add(etiquetaMensaje, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2; 
        
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.weighty = 1.0;
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
        etiquetaMensaje.setText("Evaluando..."); 
        etiquetaMensaje.setForeground(Color.GRAY);
        etiquetaSugerencia.setText(" ");

        if (contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una contraseña para evaluar.",
                    "Error", JOptionPane.ERROR_MESSAGE);

            etiquetaMensaje.setText("Error de entrada.");        
            return;
        }

        btnEvaluar.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {

                return controlador.evaluarContrasena(contrasena, usuarioAutenticado);
            }
            
            @Override
            protected void done() {
                try {
                    String resultado = get();
                    String[] partes = resultado.split("\\|");

                    String mensajeResultado = "Resultado: Contraseña " + partes[0] + ".";
                    String mensajeDetalle = partes[1];

                    if (partes[0].equals("DÉBIL")) {
                        etiquetaMensaje.setText(mensajeResultado);
                        etiquetaMensaje.setForeground(Color.RED);

                        etiquetaSugerencia.setText("<html><p style='text-align: left;'>" + mensajeDetalle + "</p></html>"); 
                        etiquetaSugerencia.setForeground(Color.BLUE.darker());
                    } else { 
                        etiquetaMensaje.setText(mensajeResultado);
                        etiquetaMensaje.setForeground(new Color(0, 100, 0)); 
                        
                        etiquetaSugerencia.setText("<html>" + mensajeDetalle + "</html>");
                        etiquetaSugerencia.setForeground(new Color(0, 100, 0));
                    }
                } catch (Exception ex) {
                    etiquetaMensaje.setText("Error: Falló la evaluación.");
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
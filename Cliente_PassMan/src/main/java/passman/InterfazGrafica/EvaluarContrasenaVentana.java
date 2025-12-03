package passman.InterfazGrafica;

import passman.lanzador.ControladorPrincipal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class EvaluarContrasenaVentana extends JDialog implements ActionListener {
    private final ControladorPrincipal controlador;
    private final String usuarioAutenticado;

    private JPasswordField campoContrasena;
    private JButton btnEvaluar;
    private JLabel etiquetaMensaje;
    private JLabel etiquetaSugerencia;
    
    public EvaluarContrasenaVentana(MenuVentana owner, ControladorPrincipal controlador) {
        super(owner, "Evaluar Contraseña", true);
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
        JLabel etiquetaContrasena = new JLabel("Contraseña a evaluar:");
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
                    "Error de Entrada", JOptionPane.ERROR_MESSAGE);

            etiquetaMensaje.setText("Error de entrada.");
            etiquetaMensaje.setForeground(Color.RED);
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

                    if (partes.length < 2) {
                        throw new Exception("Formato de respuesta del servidor inválido. Faltan partes.");
                    }
                    
                    String estadoResultado = partes[0].trim();
                    String mensajeDetalle = partes[1].trim();

                    String mensajeResultado = "Resultado: Contraseña " + estadoResultado + ".";

                    if (estadoResultado.equals("DÉBIL")) {
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
                } catch (ExecutionException ex) {
                    Throwable causa = ex.getCause();
                    
                    if (causa instanceof IOException) {
                        etiquetaMensaje.setText("Error de Conexión. El servidor no responde.");
                    } else if (causa != null) {
                        etiquetaMensaje.setText("Error Interno del Servidor: " + causa.getMessage());
                        causa.printStackTrace();
                    } else {
                        etiquetaMensaje.setText("Error de red desconocido.");
                    }
                    etiquetaMensaje.setForeground(Color.RED);
                    etiquetaSugerencia.setText(" ");
                    
                } catch (InterruptedException ex) {
                    etiquetaMensaje.setText("Operación interrumpida.");
                    etiquetaMensaje.setForeground(Color.ORANGE);
                    etiquetaSugerencia.setText(" ");
                    Thread.currentThread().interrupt();
                } catch (Exception ex) {
                    etiquetaMensaje.setText("Error en el procesamiento del resultado: Falló la evaluación.");
                    etiquetaMensaje.setForeground(Color.RED);
                    etiquetaSugerencia.setText("Error de datos: El servidor devolvió un formato inesperado.");
                    System.err.println("Error de formato/procesamiento: " + ex.getMessage());
                } finally {
                    btnEvaluar.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }
}
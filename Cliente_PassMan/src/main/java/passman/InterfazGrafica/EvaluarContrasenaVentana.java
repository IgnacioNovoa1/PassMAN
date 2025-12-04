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

    // Componentes
    private JPasswordField campoContrasena;
    private JButton btnEvaluar;
    private JLabel etiquetaMensaje;
    private JLabel etiquetaSugerencia;

    // ====== PALETA DEL MISMO LOGIN ======
    private final Color COLOR_FONDO = new Color(35, 35, 35);
    private final Color COLOR_TARJETA = new Color(45, 45, 45);
    private final Color COLOR_TEXTO = new Color(220, 220, 220);
    private final Color COLOR_PRIMARIO = new Color(70, 130, 180);
    private final Color COLOR_BOTON_SECUNDARIO = new Color(60, 60, 60);

    public EvaluarContrasenaVentana(MenuVentana owner, ControladorPrincipal controlador) {
        super(owner, "Evaluar Contraseña", true);
        this.controlador = controlador;
        this.usuarioAutenticado = owner.getUsuarioAutenticado();

        setSize(540, 420);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Fondo principal
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        panelPrincipal.setBackground(COLOR_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Etiqueta: contraseña
        JLabel etiquetaContrasena = new JLabel("Contraseña a evaluar:");
        etiquetaContrasena.setFont(new Font("SansSerif", Font.BOLD, 15));
        etiquetaContrasena.setForeground(COLOR_TEXTO);

        // Campo contraseña
        campoContrasena = new JPasswordField(20);
        estilizarCampo(campoContrasena);

        // Botón Evaluar
        btnEvaluar = new JButton("Evaluar");
        estilizarBoton(btnEvaluar);
        btnEvaluar.addActionListener(this);

        // Etiquetas de resultado
        etiquetaMensaje = new JLabel(" ");
        etiquetaMensaje.setHorizontalAlignment(SwingConstants.CENTER);
        etiquetaMensaje.setFont(new Font("SansSerif", Font.BOLD, 16));
        etiquetaMensaje.setForeground(COLOR_TEXTO);

        etiquetaSugerencia = new JLabel(" ");
        etiquetaSugerencia.setVerticalAlignment(SwingConstants.TOP);
        etiquetaSugerencia.setFont(new Font("SansSerif", Font.PLAIN, 13));
        etiquetaSugerencia.setForeground(COLOR_TEXTO);

        JPanel panelResultado = new JPanel(new BorderLayout());
        panelResultado.setBackground(COLOR_TARJETA);
        panelResultado.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panelResultado.add(etiquetaMensaje, BorderLayout.NORTH);
        panelResultado.add(etiquetaSugerencia, BorderLayout.CENTER);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        panelPrincipal.add(etiquetaContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panelPrincipal.add(campoContrasena, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panelPrincipal.add(btnEvaluar, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        panelPrincipal.add(panelResultado, gbc);

        add(panelPrincipal);
    }

    // ========== ESTILOS — MISMO LOGIN ==========
    private void estilizarBoton(JButton boton) {
        boton.setBackground(COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("SansSerif", Font.BOLD, 14));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(COLOR_PRIMARIO.brighter());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(COLOR_PRIMARIO);
            }
        });
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(COLOR_TARJETA);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BOTON_SECUNDARIO, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
    }

    // =====================================================
    //             LOGICA (NO SE MODIFICA NADA)
    // =====================================================
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
                        etiquetaSugerencia.setForeground(new Color(120, 160, 255));
                    } else {
                        etiquetaMensaje.setText(mensajeResultado);
                        etiquetaMensaje.setForeground(new Color(0, 180, 0));

                        etiquetaSugerencia.setText("<html>" + mensajeDetalle + "</html>");
                        etiquetaSugerencia.setForeground(new Color(0, 180, 0));
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
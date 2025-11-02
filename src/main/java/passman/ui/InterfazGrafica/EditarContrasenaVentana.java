package passman.ui.InterfazGrafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EditarContrasenaVentana extends JDialog implements ActionListener {
    private JTextField campoIndice;
    private JPasswordField campoNuevaContrasena;
    private JButton btnEditar;

    public EditarContrasenaVentana(MenuVentana owner) {
        super(owner, "Editar Contraseña Guardada", true);

        // Configuración
        setSize(450, 200);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicialización
        campoIndice = new JTextField(5);
        campoNuevaContrasena = new JPasswordField(20);
        btnEditar = new JButton("Actualizar Contraseña");
        btnEditar.addActionListener(this);

        // Instrucción para el usuario
        JLabel instruccion = new JLabel("<html>Primero vea el índice (No.) en la pestaña 'Ver Contraseñas'.</html>");
        instruccion.setForeground(Color.BLUE.darker());

        // Diseño 
        int row = 0;
        
        // Instrucción
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(instruccion, gbc);

        // Índice
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 1;
        panel.add(new JLabel("Número (índice) a editar:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoIndice, gbc);

        // Nueva Contraseña
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(new JLabel("Nueva Contraseña:"), gbc);
        gbc.gridx = 1; gbc.gridy = row - 1;
        panel.add(campoNuevaContrasena, gbc);

        // Botón
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        panel.add(btnEditar, gbc);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEditar) {
            JOptionPane.showMessageDialog(this, "Editando...", "info", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

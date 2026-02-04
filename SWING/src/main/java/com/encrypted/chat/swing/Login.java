package com.encrypted.chat.swing;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.encrypted.chat.models.Persona;

public class Login extends JFrame {

    public Login() {
        setTitle("Datos del cliente");
        setSize(300, 150);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField txtNombre = new JTextField(15);
        JButton btnAceptar = new JButton("Aceptar");

        btnAceptar.addActionListener(e -> {
            String nombre = txtNombre.getText();

            if (!nombre.isEmpty()) {
                dispose(); // cerrar esta ventana
                new ChatWindow(new Persona(nombre));
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(btnAceptar);

        add(panel);
        setVisible(true);
    }
}
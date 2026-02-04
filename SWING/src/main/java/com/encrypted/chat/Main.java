package com.encrypted.chat;

import javax.swing.SwingUtilities;

import com.encrypted.chat.models.Persona;
import com.encrypted.chat.swing.Login;
import com.encrypted.chat.udp.UtilsUDP;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);

        try {
            Thread.sleep(5000);
            Thread util = new Thread(new UtilsUDP(new Persona("Ivan")));
            util.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
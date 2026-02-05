package com.encrypted.chat;

import javax.swing.SwingUtilities;

import com.encrypted.chat.swing.Login;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }
}
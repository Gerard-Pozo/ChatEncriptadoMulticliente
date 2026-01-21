package com.udp.encrypted.chat;

import com.udp.encrypted.chat.udp.DescobrirEquipsUDP;

public class Main {
    public static void main(String[] args) {
        Thread t1 = new Thread(new DescobrirEquipsUDP());
        t1.start();
    }
}
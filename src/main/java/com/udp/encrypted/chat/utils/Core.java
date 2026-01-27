package com.udp.encrypted.chat.utils;

public class Core {

    private UI ui;

    public Core(UI ui) {
        this.ui = ui;
    }

    public void passarMissatge(String msg) {
        ui.mostrarMissatge(msg);
    }

}

package com.encrypted.chat.utils;

import com.encrypted.chat.models.MissatgeDesencriptat;

public class Core {

    private UI ui;

    public Core(UI ui) {
        this.ui = ui;
    }

    public void passarMissatge(MissatgeDesencriptat missatge) {
        ui.mostrarMissatge(missatge);
    }

}

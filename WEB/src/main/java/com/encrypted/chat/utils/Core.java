package com.encrypted.chat.utils;

import com.encrypted.chat.models.MissatgeDesencriptat;

/**
 * La classe Core és el nucli de l'aplicació que gestiona la comunicació entre
 * la lògica de l'aplicació i la interfície d'usuari (UI). Aquesta classe permet
 * passar missatges desencriptats a la UI per mostrar-los a l'usuari.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class Core {

    /**
     * Referència a la classe UI que permetrà al nucli de l'aplicació comunicar-se
     * amb el front.
     */
    private UI ui;

    /**
     * Constructor de la classe Core que rep una instància de la classe UI per
     * permetre la comunicació entre el nucli de l'aplicació i la interfície
     * d'usuari.
     * 
     * @param ui La classe que implementi de UI
     */
    public Core(UI ui) {
        this.ui = ui;
    }

    /**
     * Permet passar un missatge desencriptat a la UI per mostrar-lo a l'usuari.
     * 
     * @param missatge El missatge desencriptat que es vol mostrar a la UI.
     */
    public void passarMissatge(MissatgeDesencriptat missatge) {
        ui.mostrarMissatge(missatge);
    }

}

package com.encrypted.chat.utils;

import com.encrypted.chat.models.MissatgeDesencriptat;

/**
 * Interfície que defineix els mètodes que ha de implementar la classe que gestiona la interfície d'usuari.
 * Aquesta interfície permet mostrar missatges desencriptats a l'usuari.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public interface UI {
    void mostrarMissatge(MissatgeDesencriptat missatge);
}

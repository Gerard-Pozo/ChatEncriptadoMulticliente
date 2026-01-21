package com.udp.encrypted.chat;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.udp.DescobrirEquipsUDP;
import com.udp.encrypted.chat.udp.ReceptorUDP;

public class Main {
    public static void main(String[] args) {
        Persona persona = new Persona("Ivan");

        LlistatPersones.afegirPersona(persona);
        
        Thread t1 = new Thread(new DescobrirEquipsUDP(persona));
        t1.start();

        Thread t2 = new Thread(new ReceptorUDP(persona));
        t2.start();
    }
}
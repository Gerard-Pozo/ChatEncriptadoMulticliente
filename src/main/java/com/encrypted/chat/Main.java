package com.encrypted.chat;

import com.encrypted.chat.models.MissatgeDesencriptat;
import com.encrypted.chat.models.Persona;
import com.encrypted.chat.models.MissatgeDesencriptat.TipusMissatgeDesencriptat;
import com.encrypted.chat.tcp.ReceptorTCP;
import com.encrypted.chat.tcp.RemitentTCP;
import com.encrypted.chat.utils.Core;
import com.encrypted.chat.web.ChatEndpoint;

public class Main {
    public static void main(String[] args) {
        Persona p = new Persona("Geri");

        ReceptorTCP re = new ReceptorTCP(new Core(new ChatEndpoint()), p);

        Thread t = new Thread(re);
        t.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        MissatgeDesencriptat md = new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE, p, "Hola cara cola");
        md.addDestinatari(p);

        RemitentTCP.enviarMissatge(md);
    }
}
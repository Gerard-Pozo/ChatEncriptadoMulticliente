package com.encrypted.chat.tcp;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.crypto.SecretKey;

import com.encrypted.chat.models.MissatgeDesencriptat;
import com.encrypted.chat.models.MissatgeEncriptat;
import com.encrypted.chat.models.Persona;
import com.encrypted.chat.models.MissatgeDesencriptat.TipusMissatgeDesencriptat;
import com.encrypted.chat.security.DiffieHellman;
import com.encrypted.chat.utils.Core;
import com.encrypted.chat.utils.Utils;

public class ReceptorTCP implements Runnable {

    private Core core;

    private Persona persona;

    public ReceptorTCP(Core core, Persona persona) {
        this.core = core;
        this.persona = persona;
    }

    @Override
    public void run() {
        new Thread(() -> tcpEscoltant()).start();
    }

    public void tcpEscoltant() {
        try (ServerSocket servidor = new ServerSocket(Utils.PORT_TCP)) {
            while (true) {
                Socket socket = servidor.accept();

                ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());

                MissatgeEncriptat missatge = (MissatgeEncriptat) entrada.readObject();

                for (String id : missatge.getDestinataris().keySet()) {
                    // Desencripta la clau AES
                    SecretKey clauAESSessio = DiffieHellman.desencriptarClauAES(missatge.getDestinataris().get(id),
                            persona.getPrivada(),
                            missatge.getEmisor().getPublica());

                    // Després desencriptem el missatge amb la clau AES
                    String missatgeDesencriptat = DiffieHellman.desencriptarMissatge(missatge.getMissatgeEncriptat(),
                            clauAESSessio);
                    core.passarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE,
                            missatge.getEmisor(), missatgeDesencriptat));
                    System.out.println("Missatge arribat: " + missatgeDesencriptat);
                }
            }
        } catch (Exception e) {
        }
    }

}

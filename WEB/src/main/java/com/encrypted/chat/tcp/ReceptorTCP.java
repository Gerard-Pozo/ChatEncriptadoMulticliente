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

/**
 * Classe per rebre els missatges enviats per altres clients per TCP.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class ReceptorTCP implements Runnable {

    /**
     * Core del programa, permet passar missatges entre les diferents parts del programa
     */
    private Core core;

    /**
     * El client que executa l'aplicació
     */
    private Persona persona;

    /**
     * Constructor, demana un core i el client que executa l'aplicació
     * 
     * @param core Core, per passar missatges al front
     * @param persona Client que executa l'aplicació
     */
    public ReceptorTCP(Core core, Persona persona) {
        this.core = core;
        this.persona = persona;
    }

    /**
     * Inicia un fil que es quedará a l'escolta de connexions.
     */
    @Override
    public void run() {
        new Thread(() -> tcpEscoltant()).start();
    }

    /**
     * Es queda escoltant per missatges que arriben per TCP, quan arriba un missatge el desencripta i el passa al core perquè el mostri al front.
    */
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
                    core.passarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE, true,
                            missatge.getEmisor(), missatgeDesencriptat));
                }
            }
        } catch (Exception e) {
        }
    }

}

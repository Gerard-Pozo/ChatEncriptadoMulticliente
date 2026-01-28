package com.encrypted.chat.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.encrypted.chat.models.MissatgeEncriptat;
import com.encrypted.chat.models.Persona;
import com.encrypted.chat.models.MissatgeEncriptat.TipusMissatge;
import com.encrypted.chat.utils.Utils;

/**
 * Classe que s'utilitza per cercar els nous clients que es connectan al
 * programa i envia mostres de vida, per demostrar que segueix connectat, a la
 * resta de clients.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class UtilsUDP implements Runnable {

    /**
     * Socket per enviar les consultes
     */
    private static DatagramSocket socket;
    /**
     * El client que està fent ús del programa
     */
    private static Persona persona;

    /**
     * Constructor, només necessita especificar la persona que executa el programa
     * 
     * @param persona Client
     */
    public UtilsUDP(Persona persona) {
        UtilsUDP.persona = persona;
    }

    /**
     * Quan el fil és iniciat iniciará el socket, el posará com l'opció de broadcast
     * en true, per enviar les consultes per broadcast, i iniciará dos fils més
     */
    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);

            // Fil per cercar equips connectats
            new Thread(() -> cercarEquipsPeriodic()).start();

            // Fil per notificar a tots els clients que seguim connectats
            new Thread(() -> enviarMostraDeVida()).start();

        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cerca els equips en la xarxa sense parar
     */
    public static void cercarEquipsPeriodic() {
        while (true) {
            try {
                // Envia el missatge de descobriment
                enviarDescubriment();

                // Esperar x segons abans de tornar-ho a intentar
                Thread.sleep(Utils.TEMPS_ENTRE_DESCUBRIMENTS);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Envia a la resta d'equips una notificació per informar que segueix en
     * l'aplicació
     */
    public static void enviarMostraDeVida() {
        while (true) {
            MissatgeEncriptat missatge = new MissatgeEncriptat(TipusMissatge.VIU, persona);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(missatge);
                oos.flush();

                byte[] data = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),
                        Utils.PORT_UDP);
                socket.send(packet);
                oos.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(Utils.TEMPS_ENTRE_MOSTRE_VIDA);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Descobreix els equips en la xarxa que están connectats en aquest moment
     */
    public static void enviarDescubriment() {
        MissatgeEncriptat missatge = new MissatgeEncriptat(MissatgeEncriptat.TipusMissatge.DESCUBRIMENT, persona);

        try {
            // Per serialitzar l'objecte que passarem
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(baos);

            // Prepara l'objecte per passar-lo a la xarxa
            oos.writeObject(missatge);
            oos.flush();

            byte[] data = baos.toByteArray();

            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    InetAddress.getByName("255.255.255.255"),
                    Utils.PORT_UDP);

            // Mandem l'objecte a la xarxa per el broadcast en el port especificat.
            socket.send(packet);

            oos.close();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
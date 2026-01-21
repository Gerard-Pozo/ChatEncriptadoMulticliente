package com.udp.encrypted.chat.udp;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.utils.Utils;

public class DescobrirEquipsUDP implements Runnable {

    private static DatagramSocket socket;
    private static Persona persona;

    public DescobrirEquipsUDP(Persona persona) {
        DescobrirEquipsUDP.persona = persona;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(new InetSocketAddress("0.0.0.0",0));
            socket.setBroadcast(true);
            cercarEquipsPeriodic();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void enviarDescubriment() throws Exception {
        Missatge missatge = new Missatge(Missatge.TipusMissatge.DESCUBRIMENT, persona);

        // Serializar con ObjectOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(missatge);
        oos.flush();

        byte[] data = baos.toByteArray();

        DatagramPacket packet = new DatagramPacket(
                data,
                data.length,
                InetAddress.getByName("255.255.255.255"),
                5000);

        socket.send(packet);
        System.out.println("Missatge enviat");

        oos.close();
        baos.close();
    }

    public static void cercarEquipsPeriodic() {
        while (true) {
            try {
                // Envia el missatge de descobriment
                enviarDescubriment();

                // Esperar 5 segons abans de tornar-ho a intentar
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
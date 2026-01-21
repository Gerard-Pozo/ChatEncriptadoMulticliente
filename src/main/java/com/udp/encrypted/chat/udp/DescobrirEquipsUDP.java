package com.udp.encrypted.chat.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.utils.Utils;

public class DescobrirEquipsUDP implements Runnable {

    private static DatagramSocket socket;

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            cercarEquipsPeriodic();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void enviarDescubrimiento() throws Exception {
        Missatge missatge = new Missatge(Missatge.TipusMissatge.DESCUBRIMENT);

        // Serializar con ObjectOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(missatge);
        oos.flush();

        byte[] buffer = baos.toByteArray();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"),
                Utils.PORT);
        socket.send(packet);
        System.out.println("Missatge enviat");

        oos.close();
        baos.close();
    }

    public static void cercarEquipsPeriodic() {
        new Thread(() -> {
            while (true) {
                try {
                    // Envia el missatge de descobriment
                    enviarDescubrimiento();

                    // Esperar 5 segons abans de tornar-ho a intentar
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
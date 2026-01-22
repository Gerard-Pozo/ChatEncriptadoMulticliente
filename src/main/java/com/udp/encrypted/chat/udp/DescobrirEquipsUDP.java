package com.udp.encrypted.chat.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.Missatge.TipusMissatge;

public class DescobrirEquipsUDP implements Runnable {

    private static DatagramSocket socket;
    private static Persona persona;

    public DescobrirEquipsUDP(Persona persona) {
        DescobrirEquipsUDP.persona = persona;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            new Thread(() -> cercarEquipsPeriodic()).start();

            new Thread(() -> enviarMostraDeVida()).start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Descobreix els equips en la xarxa en aquest moment
     * 
     * @throws Exception
     */
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

    /**
     * Cerca els equips en la xarxa sense parar
     */
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

    /**
     * Envia a la resta d'equips una notificació per informar que segueix en
     * l'aplicació
     */
    public static void enviarMostraDeVida() {
        while (true) {
            System.out.println("ESTOY VIVO");
            Missatge missatge = new Missatge(TipusMissatge.VIU, persona);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos;
            try {
                oos = new ObjectOutputStream(baos);
                oos.writeObject(missatge);
                oos.flush();

                byte[] data = baos.toByteArray();

                DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName("255.255.255.255"),
                        5000);
                socket.send(packet);
                oos.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(15000); // 15 segons
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
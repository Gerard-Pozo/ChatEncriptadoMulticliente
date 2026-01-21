package com.udp.encrypted.chat.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.Missatge.TipusMissatge;
import com.udp.encrypted.chat.security.DiffieHellman;
import com.udp.encrypted.chat.utils.Utils;

public class ReceptorUDP {
	private static DatagramSocket socket;

	public ReceptorUDP() {
		try {
			ReceptorUDP.socket = new DatagramSocket(Utils.PORT);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void udpEscoltant() {
		byte[] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		while (true) {
			try {
				socket.receive(packet);

				ByteArrayInputStream bytesObjecte = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
				ObjectInputStream objecte = new ObjectInputStream(bytesObjecte);

				Missatge missatge = (Missatge) objecte.readObject();

				if (missatge.getTipus() == TipusMissatge.ENVIAMENT) {
					for (String id : missatge.getDestinataris().keySet()) {
						if (id.equals(Persona.getPublica().toString())) {
							System.out.println("Missatge desencriptat: " + DiffieHellman
									.desencriptarAES(missatge.getMissatgeEncriptat(),
											missatge.getDestinataris().get(id)));
						}
					}
				} else if (missatge.getTipus() == TipusMissatge.DESCUBRIMENT) {
					System.out.println("Missatge rebut");
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
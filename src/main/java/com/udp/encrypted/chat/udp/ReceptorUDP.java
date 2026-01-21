package com.udp.encrypted.chat.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.Missatge.TipusMissatge;
import com.udp.encrypted.chat.security.DiffieHellman;
import com.udp.encrypted.chat.utils.Utils;

public class ReceptorUDP implements Runnable {
	private static Persona persona;

	private static DatagramSocket socket;

	public ReceptorUDP(Persona persona) {
		ReceptorUDP.persona = persona;
	}

	public void run() {
		try {
			ReceptorUDP.socket = new DatagramSocket(Utils.PORT);
			udpEscoltant();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void udpEscoltant() {
		byte[] buffer = new byte[2048];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		System.out.println("Receptor UDP Escoltant");

		while (true) {
			try {
				socket.receive(packet);

				ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());

				ObjectInputStream ois = new ObjectInputStream(bais);

				Missatge missatge = (Missatge) ois.readObject();

				if (!missatge.getEmisor().getPublica().equals(persona.getPublica())) {
					if (missatge.getTipus() == TipusMissatge.ENVIAMENT) {
						for (String id : missatge.getDestinataris().keySet()) {
							if (id.equals(persona.getPublica().toString())) {
								System.out.println("Missatge desencriptat: " + DiffieHellman
										.desencriptarAES(missatge.getMissatgeEncriptat(),
												missatge.getDestinataris().get(id)));
							}
						}
					} else if (missatge.getTipus() == TipusMissatge.DESCUBRIMENT) {
						if (!Utils.clientExistent(missatge.getEmisor())) {
							LlistatPersones.afegirPersona(missatge.getEmisor());
						}
					}
					System.out.println("Personas registradas: " + LlistatPersones.getPersones());
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
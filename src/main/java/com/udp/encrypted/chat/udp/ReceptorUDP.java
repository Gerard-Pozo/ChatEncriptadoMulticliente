package com.udp.encrypted.chat.udp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.Missatge.TipusMissatge;
import com.udp.encrypted.chat.security.DiffieHellman;
import com.udp.encrypted.chat.utils.Utils;

public class ReceptorUDP implements Runnable {
	private static Persona persona;

	private static DatagramSocket socket;

	/**
	 * Mapa que registra els últims missatges de vida dels clients, és un
	 * ConcurrentHashMap ja que aquesta classe impedeix que diversos fils entrin a
	 * l'hora
	 */
	private static Map<String, Long> ultimesMostresDeVida = new ConcurrentHashMap<>();

	public ReceptorUDP(Persona persona) {
		ReceptorUDP.persona = persona;
	}

	public void run() {
		try {
			ReceptorUDP.socket = new DatagramSocket(Utils.PORT);
			new Thread(() -> udpEscoltant()).start();
			new Thread(() -> treureMorts()).start();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Escolta connexions UDP
	 */
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
							if (id.equals(persona.getId().toString())) {
								// Primero es desencripta la clau AES de sessio
								SecretKey clauAESSessio = DiffieHellman.desencriptarClauAES(
										missatge.getDestinataris().get(id),
										persona.getPrivada(),
										missatge.getEmisor().getPublica());

								// Després desencriptem el missatge amb la clau AES
								String missatgeDesencriptat = DiffieHellman.desencriptarMissatge(
										missatge.getMissatgeEncriptat(),
										clauAESSessio);
								System.out.println("Missatge desencriptat: " + missatgeDesencriptat);
							}
						}
					} else if (missatge.getTipus() == TipusMissatge.DESCUBRIMENT) {
						if (!Utils.clientExistent(missatge.getEmisor())) {
							LlistatPersones.afegirPersona(missatge.getEmisor());
						}
					} else if (missatge.getTipus() == TipusMissatge.VIU) {
						String id = missatge.getEmisor().getId();
						ultimesMostresDeVida.put(id, System.currentTimeMillis());
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Treu de la llista de clients, tots aquells clients que s'hagin desconnectat
	 * de l'aplicació
	 */
	public void treureMorts() {
		while (true) {
			System.out.println("VIDA");
			long tempsActual = System.currentTimeMillis();
			for (Map.Entry<String, Long> entrada : ultimesMostresDeVida.entrySet()) {
				if (tempsActual - entrada.getValue() > 20000) {
					System.out.println("Client desconectat: " + entrada.getKey());
					ultimesMostresDeVida.remove(entrada.getKey());
					LlistatPersones.eliminarPersona(entrada.getKey());
				}
			}
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
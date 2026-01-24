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
import com.udp.encrypted.chat.web.ChatEndpoint;

/**
 * Classe per rebre els missatges enviats per altres clients.
 * 
 * Amb un fil es queda escoltant fins que arriba un missatge.
 * 
 * I amb un altre fil comproba que els clients segueixin connectats.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class ReceptorUDP implements Runnable {

	/**
	 * Client que està fent ús del programa
	 */
	private static Persona persona;
	/**
	 * Socket per rebre les consultes
	 */
	private static DatagramSocket socket;

	/**
	 * Mapa que registra els últims missatges de vida dels clients, és un
	 * ConcurrentHashMap ja que aquesta classe impedeix que diversos fils entrin a
	 * l'hora
	 */
	private static Map<String, Long> ultimesMostresDeVida = new ConcurrentHashMap<>();

	private static ChatEndpoint endpoint;

	/**
	 * Constructor, demana el client que executa l'aplicació
	 * 
	 * @param persona Client
	 */
	public ReceptorUDP(Persona persona, ChatEndpoint endpoint) {
		ReceptorUDP.persona = persona;
		ReceptorUDP.endpoint = endpoint;
	}

	/**
	 * Quan s'inicia un fil prepara el socket amb el port especificat i inicia 2
	 * fils nous.
	 */
	public void run() {
		try {
			socket = new DatagramSocket(Utils.PORT);

			// Fil per escoltar connexions
			new Thread(() -> udpEscoltant()).start();

			// Fil per treure els clients desconnectats
			new Thread(() -> treureMorts()).start();

		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Es queda escoltant fins que arriba un nou missatge.
	 * 
	 * Filtra els missatges segons el tipus.
	 */
	public void udpEscoltant() {
		byte[] buffer = new byte[2048];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);


		while (true) {
			try {
				socket.receive(packet);

				ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());

				ObjectInputStream ois = new ObjectInputStream(bais);

				Missatge missatge = (Missatge) ois.readObject();

				// Impedeix que un missatge que ha enviar l'usuari sigui processat per ell
				// mateix
				if (!missatge.getEmisor().getPublica().equals(persona.getPublica())) {
					// Si el missatge es normal
					if (missatge.getTipus() == TipusMissatge.ENVIAMENT) {
						for (String id : missatge.getDestinataris().keySet()) {
							if (id.equals(persona.getId().toString())) {
								// Primer desencripta la clau de sessio
								SecretKey clauAESSessio = DiffieHellman.desencriptarClauAES(
										missatge.getDestinataris().get(id),
										persona.getPrivada(),
										missatge.getEmisor().getPublica());

								// Després desencriptem el missatge amb la clau AES
								String missatgeDesencriptat = DiffieHellman.desencriptarMissatge(
										missatge.getMissatgeEncriptat(),
										clauAESSessio);
								endpoint.enviarMissatgeWebSocket(missatge.getEmisor().getNom() + "_" + missatgeDesencriptat);
							}
						}
						// Si el missatge és per trobar nou clients
					} else if (missatge.getTipus() == TipusMissatge.DESCUBRIMENT) {
						if (!Utils.clientExistent(missatge.getEmisor())) {
							LlistatPersones.afegirPersona(missatge.getEmisor());
						}
						// Si el missatge és per trobar a clients connectats
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
			long tempsActual = System.currentTimeMillis();
			for (Map.Entry<String, Long> entrada : ultimesMostresDeVida.entrySet()) {
				if (tempsActual - entrada.getValue() > 20000) {
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
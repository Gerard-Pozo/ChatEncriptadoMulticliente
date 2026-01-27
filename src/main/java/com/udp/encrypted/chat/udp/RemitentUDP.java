package com.udp.encrypted.chat.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.MissatgeEncriptat;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.MissatgeEncriptat.TipusMissatge;
import com.udp.encrypted.chat.security.DiffieHellman;
import com.udp.encrypted.chat.utils.Utils;

/**
 * Aquesta classe permet enviar missatges xifrats per la xarxa
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class RemitentUDP {

	/**
	 * Socket per enviar les consultes
	 */
	private static DatagramSocket socket;

	/**
	 * Constructor inicia el socket i l'habilita per utilitzar broadcast
	 */
	public RemitentUDP() {
		try {
			socket = new DatagramSocket();
			socket.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prepara un missatge i l'envia per la xarxa ja encriptat
	 * 
	 * @param remitent Client que envia el missatge
	 * @param text     Missatge que es vol enviar sense encriptar
	 */
	public void enviarMissatge(Persona remitent, String text) {
		// Agafem el llistat de persones connectades
		List<Persona> persones = LlistatPersones.getPersones();
		// Treiem el remitent del llistat, ja que ell ja té el missatge desencriptat
		persones.remove(remitent);

		try {
			// Es genera una clau AES que s'utilitzará per encriptar el text
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey clauSessio = keyGen.generateKey();

			// S'encripta el missatge amb la clau de la sessio
			String textEncriptat = DiffieHellman.encriptarMissatge(text, clauSessio);

			MissatgeEncriptat missatge = new MissatgeEncriptat(TipusMissatge.ENVIAMENT, textEncriptat, remitent);

			for (Persona destinatari : persones) {
				// Encripta la clau per desxifrar el missatge amb la privada del remitent i la
				// publica del destinatari
				SecretKey clauDH = DiffieHellman.generarClauCompartidaAES(remitent.getPrivada(),
						destinatari.getPublica());
				try {
					// Xifra la clau de la sessio
					Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
					cipher.init(Cipher.ENCRYPT_MODE, clauDH);

					// Encripta la clau de sessio amb la clau AES generada anteriorment
					byte[] clauSessioEncriptada = cipher.doFinal(clauSessio.getEncoded());
					String clauSessioXifrada = Base64.getEncoder().encodeToString(clauSessioEncriptada);
					missatge.afegirDestinataris(destinatari.getId(), clauSessioXifrada);
				} catch (NoSuchPaddingException e) {
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					e.printStackTrace();
				} catch (BadPaddingException e) {
					e.printStackTrace();
				}
			}

			// Prepara l'objecte per ser enviat
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(missatge);
			oos.flush();

			// Envia el missatge per broadcast
			byte[] buffer = baos.toByteArray();
			InetAddress broadcast = InetAddress.getByName("255.255.255.255");
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcast, Utils.PORT);
			socket.send(packet);

			oos.close();
			baos.close();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
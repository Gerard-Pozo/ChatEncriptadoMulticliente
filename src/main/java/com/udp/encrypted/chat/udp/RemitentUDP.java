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
import com.udp.encrypted.chat.models.Missatge;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.Missatge.TipusMissatge;
import com.udp.encrypted.chat.security.DiffieHellman;
import com.udp.encrypted.chat.utils.Utils;

public class RemitentUDP {

	private static DatagramSocket socket;

	private Persona persona;

	public RemitentUDP(Persona persona) {
		try {
			RemitentUDP.socket = new DatagramSocket();
			socket.setBroadcast(true);
			this.persona = persona;
			enviarMissatge(persona, "Hola que tal");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void enviarMissatge(Persona remitent, String missatge) {
		System.out.println("Preparando mensaje para enviar");
		// Agafem el llistat de persones connectades
		List<Persona> persones = LlistatPersones.getPersones();
		// Treiem el remitent del llistat, ja que ell ja té el missatge desencriptat
		persones.remove(remitent);

		// Es genera una clau AES que s'utilitzará per encriptar aquesta comunicació
		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(128);
			SecretKey clauSessio = keyGen.generateKey();

			// S'encripta el missatge amb la clau de la sessio
			String textEncriptat = DiffieHellman.encriptarMissatge(missatge, clauSessio);

			Missatge missatgeEncriptat = new Missatge(TipusMissatge.ENVIAMENT, textEncriptat, remitent);

			for (Persona destinatari : persones) {
				// Encripta la clau per desxifrar el missatge amb la privada del remitent i la
				// publica del destinatari
				SecretKey clauDH = DiffieHellman.generarClauCompartidaAES(remitent.getPrivada(),
						destinatari.getPublica());
				// Xifra la clau de la sessio - USAMOS LA CLAVE DH DIRECTAMENTE
				Cipher cipher;
				try {
					cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
					cipher.init(Cipher.ENCRYPT_MODE, clauDH);
					byte[] clauSessioEncriptada = cipher.doFinal(clauSessio.getEncoded());
					String clauSessioXifrada = Base64.getEncoder().encodeToString(clauSessioEncriptada);
					missatgeEncriptat.afegirDestinataris(destinatari.getId(), clauSessioXifrada);
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

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(missatgeEncriptat);
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
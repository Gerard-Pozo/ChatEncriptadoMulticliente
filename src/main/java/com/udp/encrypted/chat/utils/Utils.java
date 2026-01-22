package com.udp.encrypted.chat.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Persona;

public class Utils {

	/**
	 * Port utilitzat per fer les comunicacions
	 */
	public static final int PORT = 5000;
	/**
	 * El temps que pasa entre una cerca de nou clients i un altra
	 */
	public static final int TEMPS_ENTRE_DESCUBRIMENTS = 5000;
	/**
	 * El temps que pasa entre un missatge de mostra de vida i un altra
	 */
	public static final int TEMPS_ENTRE_MOSTRE_VIDA = 5000;

	/**
	 * Diu si un client especificat existeix en la llista
	 * 
	 * @param p Client
	 * @return true/false
	 */
	public static boolean clientExistent(Persona p) {
		for (Persona persona : LlistatPersones.getPersones()) {
			if (persona.getId().equals(p.getId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Converteix un String en hash
	 * 
	 * @param input Text String
	 * @return hash del input
	 */
	public static String toHash(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

}
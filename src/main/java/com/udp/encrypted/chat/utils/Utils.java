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
	 * Missatge que utilitza el sistema per sapiguer que un missatge vol indicar un
	 * nom d'usuari
	 * 
	 * Si es cambia la variable també es té que cambiar del js
	 */
	public static final String WEB_MISSATGE_SISTEMA_NOM = "$%&MSG_SYSTEM_NOM&%$";
	/**
	 * Missatge que utilitza el sistema per mostrar per la UI els nous clients
	 */
	public static final String WEB_NOU_CLIENT_TROBAT = "$%&MSG-SYSTEM-NOU-CLIENT&%$";
	/**
	 * Missatge que utilitza el sistema per treure de la UI els clients desconectats
	 */
	public static final String WEB_TREURE_CLIENT_DESCONECTAT = "$%&MSG-SYSTEM-CLIENT-DESCONECTAT&%$";

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
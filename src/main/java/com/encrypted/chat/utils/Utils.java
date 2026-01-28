package com.encrypted.chat.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.encrypted.chat.models.LlistatPersones;
import com.encrypted.chat.models.Persona;

public class Utils {

	/**
	 * Port utilitzat per fer les comunicacions per TCP
	 */
	public static final int PORT_TCP = 5000;
	/**
	 * Port utilitzat per fer les comunicacions per UDP
	 */
	public static final int PORT_UDP = 5001;
	/**
	 * El temps que pasa entre una cerca de nou clients i un altra
	 */
	public static final int TEMPS_ENTRE_DESCUBRIMENTS = 5000;
	/**
	 * El temps que pasa entre un missatge de mostra de vida i un altra
	 */
	public static final int TEMPS_ENTRE_MOSTRE_VIDA = 5000;
	/**
	 * Els simbols per separar la cadena de caracters
	 */
	public static final String SEPARADOR = "#$=/&%";
	/**
	 * Missatge que utilitza el sistema per sapiguer que un missatge vol indicar un
	 * nom d'usuari
	 */
	public static final String MISSATGE_SISTEMA_NOM = "$%&MSG-SYSTEM-NOM&%$";
	/**
	 * Missatge que utilitza el sistema per mostrar per la UI els nous clients
	 */
	public static final String NOU_CLIENT_TROBAT = "$%&MSG-SYSTEM-NOU-CLIENT&%$";
	/**
	 * Missatge que utilitza el sistema per treure de la UI els clients desconectats
	 */
	public static final String TREURE_CLIENT_DESCONECTAT = "$%&MSG-SYSTEM-CLIENT-DESCONECTAT&%$";
	/**
	 * Indica que el client que executa la aplicació s'ha connectat
	 */
	public static final String CLIENT_PROPI_CONNECTAT = "$%&MSG-SYSTEM-CLIENT-PROPI-CONNECTAT&%$";
	/**
	 * Indica que es un missatge dels propis clients
	 */
	public static final String MISSATGE_NORMAL = "$%&MSG_CLIENT&%$";

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

	/**
	 * Recorreix les interficies de xarxa buscant la ip que té l'equip
	 * 
	 * @return IP del equip
	 */
	public static String obtenirIpLocal() {
        try {
            for (NetworkInterface ni : java.util.Collections.list(
                    NetworkInterface.getNetworkInterfaces())) {

                for (InetAddress addr : java.util.Collections.list(
                        ni.getInetAddresses())) {

                    if (!addr.isLoopbackAddress()
                            && addr instanceof Inet4Address) {

                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
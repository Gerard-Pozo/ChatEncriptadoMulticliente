package com.encrypted.chat.security;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Classe que permet:
 * 
 * Generar un parell de claus publiques i privades amb el algoritme DH.
 * Encriptar missatges.
 * Desencriptar missatges.
 * Generar claus de sessions per a una comunicació.
 * 
 * L'algoritme DH utilitza les mátematiques per a que totes les claus estiguin
 * vinculades.
 * 
 * Per tal de fer una conexió segura, es té que encriptar el missatge amb la
 * privada del emisor i la pública del receptor, i en el missatge es té que
 * especificar la clau de sessió, que será una clau AES encriptada amb la
 * privada del emisor.
 * 
 * En el moment en el que una sola clau privada surt a la llum, la comunicació
 * ja es veu compromesa mentre s'executi el programa.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class DiffieHellman {

	/**
	 * Genera un parell de claus privades i públiques
	 * 
	 * @return El parell de claus
	 */
	public static KeyPair generarClausDH() {
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("DH");
			kpg.initialize(2048);
			return kpg.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Genera una clau compartida per efectuar la connexió entre el emisor i el
	 * receptor
	 * 
	 * @param clauPrivada Emisor
	 * @param clauPublica Receptor
	 * @return La clau compartida
	 */
	public static SecretKey generarClauCompartidaAES(PrivateKey clauPrivada, PublicKey clauPublica) {
		KeyAgreement ka;
		try {
			ka = KeyAgreement.getInstance("DH");
			ka.init(clauPrivada);
			ka.doPhase(clauPublica, true);
			byte[] sharedSecret = ka.generateSecret();
			return new SecretKeySpec(sharedSecret, 0, 16, "AES");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retorna el missatge encriptat
	 * 
	 * @param missatge Missatge sense encriptar
	 * @param clauAES  Clau compartida
	 * @return Missatge encriptat
	 */
	public static String encriptarMissatge(String missatge, SecretKey clauAES) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, clauAES);
			byte[] encrypted = cipher.doFinal(missatge.getBytes("UTF-8"));
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Desencripta un missatge encriptat
	 * 
	 * @param missatgeEncriptat Missatge encriptat
	 * @param clauAES           Clau AES ja desencriptada
	 * @return Missatge sense encriptar
	 */
	public static String desencriptarMissatge(String missatgeEncriptat, SecretKey clauAES) {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, clauAES);
			byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(missatgeEncriptat));
			return new String(decrypted, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Encripta la clau de sessio
	 * 
	 * @param clauEncriptada    Clau de sessio encriptada
	 * @param clauPrivada       Clau privada del receptor
	 * @param clauPublicaEmisor Clau publica del emisor
	 * @return Clau de sessio desencriptada
	 */
	public static SecretKey desencriptarClauAES(String clauEncriptada, PrivateKey clauPrivada,
			PublicKey clauPublicaEmisor) {
		try {
			// 1. Generar la clau DH compartida
			SecretKey clauDH = generarClauCompartidaAES(clauPrivada, clauPublicaEmisor);

			// 2. Desencriptar la clau AES de sessió
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, clauDH);
			byte[] clauAESBytes = cipher.doFinal(Base64.getDecoder().decode(clauEncriptada));

			return new SecretKeySpec(clauAESBytes, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
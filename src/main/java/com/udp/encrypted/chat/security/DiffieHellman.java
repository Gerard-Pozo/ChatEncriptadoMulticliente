package com.udp.encrypted.chat.security;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DiffieHellman {

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
	 * Genera la clau compartida entre el emisor i el receptor
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
	 * Retorna el missatge encriptat en base 64
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
	 * Desencripta el missatge utilitzant la clau aes generada
	 * 
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

	/**
	 * Converteix un String a SecretKey
	 * 
	 * @param encodedKey Clau xifrada String
	 * @return Clau xifrada SecretKey
	 */
	public static SecretKey stringToSecretKey(String encodedKey) {
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
	}

}
package com.udp.encrypted.chat.models;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.udp.encrypted.chat.security.DiffieHellman;

public class Persona implements Serializable {
	private String id;
	private String nom;
	private PublicKey publica;
	private PrivateKey privada;

	public Persona (String nom) {
		this.nom = nom;
		// Es generen les claus publica i privada
		KeyPair claus = DiffieHellman.generarClausDH();
		this.publica = claus.getPublic();
		this.privada = claus.getPrivate();
		this.id = hash(publica.toString());
	}
	
	public Persona(String id, String nom, PublicKey publica) {
		this.id = id;
		this.nom = nom;
		this.publica = publica;
	}
	
	public PrivateKey getPrivada() {
		return privada;
	}
	
	public PublicKey getPublica() {
		return publica;
	}
	
	public String getId() {
		return id;
	}
	
	public static String hash(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		return "id: " + id
				+ "\nnom: " + nom;
	}
}
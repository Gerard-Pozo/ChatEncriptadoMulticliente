package com.udp.encrypted.chat.models;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import com.udp.encrypted.chat.security.DiffieHellman;
import com.udp.encrypted.chat.utils.Utils;

/**
 * Permet crear una persona amb la seva clau publica i clau privada.
 * 
 * Per crear la persona tindra que tenir un nom, els altres atributs són
 * generats automaticament.
 * 
 * La clau pública i privada només s'emmagatzemen en mémoria.
 * 
 * Per identificar a les persones tenim el atribut id, que és el hash de la clau
 * pública, ho fem així perque és més sencill en termés de recursos comparar dos
 * hashes que dos claus.
 * 
 * La classe implemente de Serializable per poder passar-la per la xarxa, però
 * el atribut de la clau privada no es passará perque té el modificador
 * transient.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class Persona implements Serializable {

	/**
	 * Hash de la clau publica
	 */
	private String id;
	/**
	 * Nom de la persona
	 */
	private String nom;
	/**
	 * Clau publica
	 */
	private PublicKey publica;
	/**
	 * Clau privada
	 */
	private transient PrivateKey privada;

	/**
	 * Constructor, permet crear una persona amb un nom
	 * 
	 * Crea automaticament les claus pública i privada de la persona i li assigna un
	 * id
	 * 
	 * @param nom Nom del client
	 */
	public Persona(String nom) {
		this.nom = nom;
		// Es generen les claus publica i privada
		KeyPair claus = DiffieHellman.generarClausDH();
		this.publica = claus.getPublic();
		this.privada = claus.getPrivate();
		// Converteix la clau publica en un hash per ser utilitzat com id
		this.id = Utils.toHash(publica.toString());
	}

	/**
	 * Retorna el nom del client
	 * 
	 * @return Nom del client
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * Retorna la clau privada del client
	 * 
	 * @return Clau privada del client
	 */
	public PrivateKey getPrivada() {
		return privada;
	}

	/**
	 * Retorna la clau pública del client
	 * 
	 * @return Clau pública del client
	 */
	public PublicKey getPublica() {
		return publica;
	}

	/**
	 * Retorna el id del client
	 * 
	 * @return Id del client
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retorna en un String el id, nom i clau pública del client
	 * 
	 * @return Objecte Persona sense la clau privada
	 */
	@Override
	public String toString() {
		return "id: " + id
				+ "\nnom: " + nom
				+ "\nclau publica: " + publica.toString();
	}
}
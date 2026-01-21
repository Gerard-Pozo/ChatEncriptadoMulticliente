package com.udp.encrypted.chat.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Missatge implements Serializable {
	/**
	 * <p>Per diferenciar els tipus de missatges que pot escoltar el client.</p>
	 * 
	 * <p>Descubriment implica els missatges per descobrir-lo en la xarxa</p>
	 * 
	 * <p>Enviament per enviar els missatges encriptats.</p>
	 * 
	 * <p>VIU és un missatge que cada x temps els clients enviarán per corroborar que
	 * segueixen connectats</p>
	 */
	public enum TipusMissatge {
		DESCUBRIMENT, ENVIAMENT, VIU
	}
	/**
	 * El tipus de missatge que serà
	 */
	private TipusMissatge tipus;
	/**
	 * El missatge que es passara per la xarxa
	 */
	private String missatgeEncriptat;
	/**
	 * La persona que envia el missatge
	 */
	private Persona emisor;
	/**
	 * Els destinataris als que anira el missatge
	 * 
	 * Key -> ID del destinatari
	 * Value -> Clau AES encriptada
	 */
	private Map<String, String> destinataris = new HashMap<>();

	public Missatge(TipusMissatge tipus, Persona emisor) {
		this.tipus = tipus;
		this.emisor = emisor;
	}

	public Missatge(TipusMissatge tipus, String missatgeEncriptat, Persona emisor) {
		this.tipus = tipus;
		this.missatgeEncriptat = missatgeEncriptat;
		this.emisor = emisor;
	}

	public Missatge(TipusMissatge tipus, String missatgeEncriptat, Persona emisor,
			HashMap<String, String> destinataris) {
		this.tipus = tipus;
		this.missatgeEncriptat = missatgeEncriptat;
		this.emisor = emisor;
		this.destinataris = destinataris;
	}

	public void afegirDestinataris(String id, String clauAES) {
		destinataris.put(id, clauAES);
	}

	public Map<String, String> getDestinataris() {
		return destinataris;
	}

	public String getMissatgeEncriptat() {
		return missatgeEncriptat;
	}

	public TipusMissatge getTipus() {
		return tipus;
	}

	public Persona getEmisor() {
		return emisor;
	}
}
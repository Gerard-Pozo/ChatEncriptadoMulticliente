package com.udp.encrypted.chat.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Crea un missatge per enviar per la xarxa
 * 
 * Hi han diferents tipus de missatges:
 * Descubriment -> Indica que és un missatge per descobrir equips que estiguin
 * escoltant
 * Enviament -> Indica que és un missatge normal en el que hi va un missatge
 * encriptat per a que sigui desencriptat
 * Viu -> Indica que es un missatge per confirmar que el client segueix
 * connectat al programa
 * 
 * Si el missatge és normal tindrá:
 * El tipus de missatge.
 * El propi missatge encriptat.
 * Un emisor, que representa el client que ha creat el missatge.
 * Un mapa amb les id dels usuaris receptors i les claus de sessió
 * 
 * Si el missatge no es normal tindrá:
 * El tipus de missatge.
 * Un emisor, que representa el client que ha creat el missatge.
 * 
 * La classe es Serializable per poder ser mandada per la xarxa i recomposta
 * quan sigui rebuda.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class MissatgeEncriptat implements Serializable {
	/**
	 * Per diferenciar els tipus de missatges que pot escoltar el client.
	 * 
	 * Descubriment implica els missatges per descobrir-lo en la xarxa.
	 * 
	 * Enviament per enviar els missatges encriptats.
	 * 
	 * VIU és un missatge que cada x temps els clients enviarán per corroborar que
	 * segueixen connectats.
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

	/**
	 * Constructor que permet crear un missatge amb només un tipus i un emisor per
	 * sapiguer qui envia el missatge
	 * 
	 * @param tipus  Tipus de missatge
	 * @param emisor Persona que crea el missatge
	 */
	public MissatgeEncriptat(TipusMissatge tipus, Persona emisor) {
		this.tipus = tipus;
		this.emisor = emisor;
	}

	/**
	 * Constructor que permet crear un missatge amb el tipus, el missatge ja
	 * encriptat i la persona que envia el missatge
	 * 
	 * @param tipus             Tipus de missatge
	 * @param missatgeEncriptat Missatge ja encriptat
	 * @param emisor            Persona que crea el missatge
	 */
	public MissatgeEncriptat(TipusMissatge tipus, String missatgeEncriptat, Persona emisor) {
		this.tipus = tipus;
		this.missatgeEncriptat = missatgeEncriptat;
		this.emisor = emisor;
	}

	/**
	 * Afegeix a la llista un destinatari amb el seu id i la clau de la sessió
	 * 
	 * @param id      Id del client
	 * @param clauAES Clau de la sessió
	 */
	public void afegirDestinataris(String id, String clauAES) {
		destinataris.put(id, clauAES);
	}

	/**
	 * Retorna un Map amb la id i la clau de sessió dels destinataris
	 * 
	 * @return Map amb id i clau dels destinataris
	 */
	public Map<String, String> getDestinataris() {
		return destinataris;
	}

	/**
	 * Retorna el missatge encriptat
	 * 
	 * @return Missatge encriptat
	 */
	public String getMissatgeEncriptat() {
		return missatgeEncriptat;
	}

	/**
	 * Retorna el tipus del missatge
	 * 
	 * @return Tipus del missatge
	 */
	public TipusMissatge getTipus() {
		return tipus;
	}

	/**
	 * Retorna l'emisor del missatge
	 * 
	 * @return Emisor del missatge
	 */
	public Persona getEmisor() {
		return emisor;
	}
}
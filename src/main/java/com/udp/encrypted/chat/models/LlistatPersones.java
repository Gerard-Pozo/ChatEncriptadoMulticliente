package com.udp.encrypted.chat.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que té els clients connectats que estàn utilitzant el chat.
 * 
 * Permet afegir, treure i agafar els clients.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class LlistatPersones {
	/**
	 * Llistat de les persones conenctades
	 */
	private static List<Persona> persones = new ArrayList<>();
	
	/**
	 * Permet afegir a la llista una persona
	 * 
	 * @param persona Client connectat
	 */
	public static void afegirPersona(Persona persona) {
		persones.add(persona);
	}

	/**
	 * Elimina a un client per el seu id
	 * 
	 * @param id Id del client
	 */
	public static void eliminarPersona(String id) {
		for (Persona p : persones) {
			if (p.getId().equals(id)) {
				persones.remove(p);
			}
		}
	}
	
	/**
	 * Permet treure de la llista una persona
	 * 
	 * @param persona Client desconnectat
	 */
	public static void eliminarPersona(Persona persona) {
			persones.remove(persona);
	}
	
	/**
	 * Permet agafar la llista de persones
	 * 
	 * @return Llista de les persones connectades
	 */
	public static ArrayList<Persona> getPersones() {
		return (ArrayList<Persona>) persones;
	}

	/**
	 * Retorna totes les persones
	 * 
	 * @return Clients conenctats
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Persona p : persones) {
			sb.append(p);
		}
		return sb.toString();
	}
	
}
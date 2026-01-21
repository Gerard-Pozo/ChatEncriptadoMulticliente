package com.udp.encrypted.chat.models;

import java.util.ArrayList;
import java.util.List;

public class LlistatPersones {
	private static List<Persona> persones = new ArrayList<>();
	
	public static void afegirPersona(Persona persona) {
		persones.add(persona);
	}
	
	public static void eliminarPersona(Persona persona) {
			persones.remove(persona);
	}
	
	public static ArrayList<Persona> getPersones() {
		return (ArrayList) persones;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (Persona p : persones) {
			sb.append(p);
		}
		return sb.toString();
	}
	
}
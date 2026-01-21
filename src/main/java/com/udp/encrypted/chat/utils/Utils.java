package com.udp.encrypted.chat.utils;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Persona;

public class Utils {

	// TODO Hay que añadir un control de buffer, para que los mensajes muy grandes
	// se vayan mandando, hay que verlo como hacerlo con la encriptación pero
	// debería de ser lo mismo, simplemente se irán mandando y el cliente los irá
	// desencriptando hasta encontrar con un limite

	public static final int PORT = 5000;

	public static boolean clientExistent(Persona p) {
		for (Persona persona : LlistatPersones.getPersones()) {
			if (persona.getId().equals(p.getId())) {
				return true;
			}
		}

		return false;
	}

}
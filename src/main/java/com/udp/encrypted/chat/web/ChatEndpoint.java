package com.udp.encrypted.chat.web;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.udp.ReceptorUDP;
import com.udp.encrypted.chat.udp.RemitentUDP;
import com.udp.encrypted.chat.udp.UtilsUDP;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/chat")
public class ChatEndpoint {

    private Session session;

    private Persona persona;

    private RemitentUDP remitent = new RemitentUDP();
    private ReceptorUDP receptor;

    // Set estático para rastrear todas las sesiones activas
    private static final Set<Session> sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Nueva conexión WebSocket: " + session.getId());
        this.session = session;
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Conexión cerrada: " + session.getId());
        sessions.remove(session);
    }

    @OnMessage
    public void handlerMessage(String message) {
        if (esMissatge(message)) {
            if (persona != null) {
                remitent.enviarMissatge(persona, message);
            }
        } else {
            persona = new Persona(message.substring(3, message.length()));

            receptor = new ReceptorUDP(persona, this);

            Thread receptorFil = new Thread(receptor);
            Thread utils = new Thread(new UtilsUDP(persona));
            
            receptorFil.start();
            utils.start();
        }
    }

    /**
     * Envía un mensaje a todos los clientes WebSocket conectados
     * @param message Mensaje a enviar
     */
    public void broadcast(String message) {
        for (Session s : sessions) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    System.err.println("Error enviando mensaje a " + s.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    public String imprimirMissatge(String remitent, String missatge) {
        String formattedMessage = remitent + "_" + missatge;
        // Enviar el mensaje a todos los clientes WebSocket
        broadcast(formattedMessage);
        return formattedMessage;
    }

    public String actualitzarLlistatClients() {
        StringBuilder sb = new StringBuilder();
        sb.append("!#ActualitzarLlistat");
        for (Persona p : LlistatPersones.getPersones()) {
            sb.append("_" + p.getNom());
        }
        String message = sb.toString();
        // Enviar la lista actualizada a todos los clientes
        broadcast(message);
        return message;
    }

    private boolean esMissatge(String missatge) {
        return missatge.contains("NOM");
    }

}


package com.udp.encrypted.chat.web;

import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.udp.ReceptorUDP;
import com.udp.encrypted.chat.udp.RemitentUDP;
import com.udp.encrypted.chat.udp.UtilsUDP;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/chat")
public class ChatEndpoint {

    private Session session;
    private Persona persona;
    private RemitentUDP remitent = new RemitentUDP();
    private ReceptorUDP receptor;
    
    // Mantener todas las sesiones activas
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnMessage
    public void handlerMessage(String message) {
        System.out.println("Mensaje recibido via WebSocket: " + message);
        
        if (esMissatge(message)) {
            if (persona != null) {
                // Es un mensaje normal de chat
                remitent.enviarMissatge(persona, message);
                // Mostrar mensaje propio inmediatamente
                enviarMensajeWebSocket(persona.getNom() + "_" + message);
            }
        } else {
            // Es el nombre de usuario (NOMnombre)
            persona = new Persona(message.substring(3, message.length()));
            System.out.println("Nuevo usuario: " + persona.getNom());
            
            receptor = new ReceptorUDP(persona, this);

            Thread receptorFil = new Thread(receptor);
            Thread utils = new Thread(new UtilsUDP(persona));

            utils.start();
            receptorFil.start();
            
            // Confirmar conexión al cliente
            enviarMensajeWebSocket("SERVIDOR_Conectado como " + persona.getNom());
        }
    }
    
    public void enviarMensajeWebSocket(String mensaje) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(mensaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean esMissatge(String missatge) {
        return !missatge.startsWith("NOM");
    }
    
}
package com.udp.encrypted.chat.web;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.udp.encrypted.chat.models.LlistatPersones;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.udp.ReceptorUDP;
import com.udp.encrypted.chat.udp.RemitentUDP;
import com.udp.encrypted.chat.udp.UtilsUDP;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
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
    
    // Mantener referencia estática de sesiones activas
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        sessions.add(session);
        System.out.println("Nueva conexión WebSocket: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Conexión cerrada: " + session.getId());
        
        // Limpiar recursos si es necesario
        if (persona != null) {
            LlistatPersones.eliminarPersona(persona);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.err.println("Error en WebSocket: " + error.getMessage());
        error.printStackTrace();
    }

    @OnMessage
    public void handlerMessage(String message) {
        System.out.println("Mensaje recibido: " + message);
        
        if (esMissatge(message)) {
            if (persona != null) {
                remitent.enviarMissatge(persona, message);
                // Mostrar el mensaje propio inmediatamente
                imprimirMissatge(persona.getNom(), message);
            }
        } else {
            // El mensaje empieza con "NOM" - es el nombre de usuario
            String nombre = message.substring(3);
            persona = new Persona(nombre);
            
            System.out.println("Nuevo usuario registrado: " + nombre);
            
            // Iniciar componentes UDP
            receptor = new ReceptorUDP(persona, this);
            Thread receptorFil = new Thread(receptor);
            Thread utils = new Thread(new UtilsUDP(persona));
            
            utils.start();
            receptorFil.start();
            
            // Añadir a la lista de personas
            LlistatPersones.afegirPersona(persona);
            
            // Enviar lista actualizada a este usuario
            actualitzarLlistatClients();
            
            // Notificar a otros usuarios sobre el nuevo usuario
            // (esto debería pasar automáticamente con los mensajes de descubrimiento)
        }
    }

    // Métodos getter para que ReceptorUDP pueda llamarlos
    public void imprimirMissatge(String remitent, String missatge) {
        try {
            String mensajeCompleto = remitent + "_" + missatge;
            this.session.getBasicRemote().sendText(mensajeCompleto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actualitzarLlistatClients() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("!#ActualitzarLlistat");
            for (Persona p : LlistatPersones.getPersones()) {
                // No incluir al usuario actual en la lista
                if (!p.getId().equals(persona.getId())) {
                    sb.append("_").append(p.getNom());
                }
            }
            
            String lista = sb.toString();
            if (!lista.equals("!#ActualitzarLlistat")) {
                this.session.getBasicRemote().sendText(lista);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean esMissatge(String missatge) {
        return !missatge.startsWith("NOM");
    }
}
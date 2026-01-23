package com.udp.encrypted.chat.web;

import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.udp.ReceptorUDP;
import com.udp.encrypted.chat.udp.RemitentUDP;
import com.udp.encrypted.chat.udp.UtilsUDP;

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

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
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

            utils.start();
            receptorFil.start();
        }
    }

    public String imprimirMissatge(String remitent, String missatge) {
        return remitent + "_" + missatge;
    }

    private boolean esMissatge(String missatge) {
        return missatge.contains("NOM");
    }

}

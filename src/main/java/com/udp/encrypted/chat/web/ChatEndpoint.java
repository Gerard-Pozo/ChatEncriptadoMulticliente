package com.udp.encrypted.chat.web;

import com.udp.encrypted.chat.models.MissatgeDesencriptat;
import com.udp.encrypted.chat.models.Persona;
import com.udp.encrypted.chat.models.MissatgeDesencriptat.TipusMissatgeDesencriptat;
import com.udp.encrypted.chat.udp.ReceptorUDP;
import com.udp.encrypted.chat.udp.RemitentUDP;
import com.udp.encrypted.chat.udp.UtilsUDP;
import com.udp.encrypted.chat.utils.Core;
import com.udp.encrypted.chat.utils.UI;
import com.udp.encrypted.chat.utils.Utils;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Aquesta classe permet tenir una connexió amb una página web bidireccional i
 * en temps real, per tal de aconseguir mostrar missatges i enviarlos sense
 * tenir que recargar la página.
 * 
 * Actua com un intermediari entre la lógica del programa i el front.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
@ServerEndpoint("/chat")
public class ChatEndpoint implements UI {

    private Core core;
    /**
     * El client
     */
    private Persona persona;
    /**
     * Classe per enviar els missatges
     */
    private RemitentUDP remitent = new RemitentUDP();
    /**
     * Classe per rebre els missatges
     */
    private ReceptorUDP receptor;

    /**
     * Si el client obra més d'un cop la página s'afegirán a la llista de sesions
     * per tenir tots els chats actualitzats
     */
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    /**
     * Quan s'obre el websocket emmagatzema la sessio en un llista
     * 
     * @param sessio Conexió entre la UI i el programa
     */
    @OnOpen
    public void onOpen(Session sessio) {
        sessions.add(sessio);
    }

    /**
     * Quan la sessió es tanca es treu de la llesta
     * 
     * @param session Conexió entre la UI i el programa
     */
    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    /**
     * Quan hi ha un missatge comprova que sigui un missatge del sistema o del
     * usuari.
     * 
     * Si es un missatge del usuari el envia per la xarxa.
     * 
     * Si no ho es fa el que tingui que fer
     * 
     * @param missatge Missatge del client o del sistema
     */
    @OnMessage
    public void handlerMessage(String missatge) {
        System.out.println("Missatge rebut via WebSocket: " + missatge);

        // Descarta entre missatges del client i del sistema
        if (!missatge.startsWith(Utils.MISSATGE_SISTEMA_NOM)) {
            if (persona != null) {
                // El client envia el missatge
                remitent.enviarMissatge(persona, missatge);
                // Mostrar el propi missatge que ha enviat el client
                mostrarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE, persona, missatge));
            }
        } else {
            // Crea al client amb el seu nom real
            persona = new Persona(missatge.substring(Utils.MISSATGE_SISTEMA_NOM.length(), missatge.length()));
            System.out.println("Usuari nou: " + persona.getNom());

            core = new Core(this);
            receptor = new ReceptorUDP(persona, core);

            // Inicia els fils
            Thread receptorFil = new Thread(receptor);
            Thread utils = new Thread(new UtilsUDP(persona));
            utils.start();
            receptorFil.start();

            // Confirma la connexió al client
            mostrarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.CONNECTAT, persona, null));
        }
    }

    /**
     * Envia un missatge a totes les sessions i les mostren per la UI
     * 
     * @param mensaje Missatge per mostrar
     */
    @Override
    public void mostrarMissatge(MissatgeDesencriptat missatge) {
        try {
            for (Session s : sessions) {
                if (s != null && s.isOpen()) {
                    String missatgePerPassar = "";

                    switch (missatge.getTipus()) {
                        case CONNECTAT:
                            missatgePerPassar = Utils.CLIENT_PROPI_CONNECTAT
                                    + Utils.SEPARADOR + missatge.getEmisor().getId()
                                    + Utils.SEPARADOR + missatge.getEmisor().getNom();
                            break;
                        case NOU_CLIENT:
                            missatgePerPassar = Utils.NOU_CLIENT_TROBAT
                                    + Utils.SEPARADOR + missatge.getEmisor().getId() 
                                    + Utils.SEPARADOR + missatge.getEmisor().getNom();
                            break;
                        case TREURE_CLIENT:
                            missatgePerPassar = Utils.TREURE_CLIENT_DESCONECTAT
                                    + Utils.SEPARADOR + missatge.getEmisor().getId()
                                    + Utils.SEPARADOR + " ";
                            break;
                        case MISSATGE:
                            missatgePerPassar = missatge.getEmisor().getNom()
                                    + Utils.SEPARADOR + missatge.getEmisor().getId()
                                    + Utils.SEPARADOR + missatge.getMissatge();
                            break;
                        default:
                            break;
                    }

                    s.getBasicRemote().sendText(missatgePerPassar);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
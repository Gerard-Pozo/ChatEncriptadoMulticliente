package com.encrypted.chat.web;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.encrypted.chat.models.LlistatPersones;
import com.encrypted.chat.models.MissatgeDesencriptat;
import com.encrypted.chat.models.Persona;
import com.encrypted.chat.models.MissatgeDesencriptat.TipusMissatgeDesencriptat;
import com.encrypted.chat.tcp.ReceptorTCP;
import com.encrypted.chat.tcp.RemitentTCP;
import com.encrypted.chat.udp.ReceptorUDP;
import com.encrypted.chat.udp.RemitentUDP;
import com.encrypted.chat.udp.UtilsUDP;
import com.encrypted.chat.utils.Core;
import com.encrypted.chat.utils.UI;
import com.encrypted.chat.utils.Utils;

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

    /**
     * Core del programa, permet passar missatges entre les diferents parts del programa
     */
    private Core core;
    /**
     * El client
     */
    private Persona persona;
    /**
     * Classe per rebre els missatges per TCP
     */
    private ReceptorTCP receptorTCP;
    /**
     * Classe per enviar els missatges
     */
    private RemitentUDP remitentUDP = new RemitentUDP();
    /**
     * Classe per rebre els missatges
     */
    private ReceptorUDP receptorUDP;

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

                String[] parts = missatge.split(Pattern.quote(Utils.SEPARADOR));

                boolean esMissatgeTCP = false;

                // Missatge TCP
                if (parts.length > 1) {
                    esMissatgeTCP = true;
                    MissatgeDesencriptat md = new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE, esMissatgeTCP, persona,
                            parts[parts.length - 1]);
                    for (int i = 0; i < parts.length - 1; i++) {
                        md.setDestinatari(LlistatPersones.getPersona(parts[i]));
                        RemitentTCP.enviarMissatge(md);
                        mostrarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE, esMissatgeTCP, persona,
                                parts[parts.length - 1]));
                    }
                    return;
                } else {
                    remitentUDP.enviarMissatge(persona, missatge);
                }
                // Mostrar el propi missatge que ha enviat el client
                mostrarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.MISSATGE, esMissatgeTCP, persona, missatge));
            }
        } else {
            // Crea al client amb el seu nom real
            persona = new Persona(missatge.substring(Utils.MISSATGE_SISTEMA_NOM.length(), missatge.length()));
            System.out.println("Usuari nou: " + persona.getNom());

            core = new Core(this);
            receptorUDP = new ReceptorUDP(core, persona);
            receptorTCP = new ReceptorTCP(core, persona);

            // Inicia els fils
            Thread receptorTCPFil = new Thread(receptorTCP);
            Thread receptorUDPFil = new Thread(receptorUDP);
            Thread utils = new Thread(new UtilsUDP(persona));
            receptorTCPFil.start();
            receptorUDPFil.start();
            utils.start();

            // Confirma la connexió al client
            mostrarMissatge(new MissatgeDesencriptat(TipusMissatgeDesencriptat.CONNECTAT, persona, null));
        }
    }

    /**
     * Envia un missatge a totes les sessions i les mostren per la UI
     * 
     * També s'utilitza per mostrar missatges del sistema com quan un client es connecta o es desconecta
     * 
     * @param mensaje Missatge per mostrar
     */
    @Override
    public void mostrarMissatge(MissatgeDesencriptat missatge) {
        try {
            for (Session s : sessions) {
                if (s != null && s.isOpen()) {
                    String missatgePerPassar = "";

                    LocalTime temps = LocalTime.now();

                    DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm");

                    // Agafa l'hora actual amb format HH:mm
                    String horaFormatada = temps.format(format);

                    switch (missatge.getTipus()) {
                        case CONNECTAT:
                            missatgePerPassar = Utils.CLIENT_PROPI_CONNECTAT
                                    + Utils.SEPARADOR + missatge.getEmisor().getId()
                                    + Utils.SEPARADOR + missatge.getEmisor().getNom()
                                    + Utils.SEPARADOR + horaFormatada;
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
                                    + Utils.SEPARADOR + missatge.getMissatge()
                                    + Utils.SEPARADOR + horaFormatada
                                    + Utils.SEPARADOR + missatge.getEsMissatgeTCP();
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
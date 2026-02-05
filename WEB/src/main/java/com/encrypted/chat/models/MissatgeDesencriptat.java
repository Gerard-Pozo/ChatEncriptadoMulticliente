package com.encrypted.chat.models;

/**
 * Classe que representa un missatge desencriptat, és a dir, el missatge que es mostrarà al client.
 * 
 * @author Gerard Pozo i Ivan Rodriguez
 */
public class MissatgeDesencriptat {
    /**
     * Per diferenciar els tipus de missatges que pot escoltar el client.
     */
    public enum TipusMissatgeDesencriptat {
        MISSATGE, NOU_CLIENT, TREURE_CLIENT, CONNECTAT
    }

    /**
     * El tipus de missatge que serà
     */
    private TipusMissatgeDesencriptat tipus;
    /**
     * El missatge que es passara per la xarxa
     */
    private String missatge;
    /**
     * La persona que envia el missatge
     */
    private Persona emisor;
    /**
     * La persona que rep el missatge, en cas de ser un missatge TCP, sinó serà null
     */
    private Persona destinatari;
    /**
     * Indica si el missatge és un missatge TCP
     */
    private boolean esMissatgeTCP;

    public MissatgeDesencriptat(TipusMissatgeDesencriptat tipus, Persona emisor, String missatge) {
        this.tipus = tipus;
        this.missatge = missatge;
        this.emisor = emisor;
    }

    /**
     * Constructor que permet crear un missatge amb el tipus, el missatge ja
     * encriptat i la persona que envia el missatge
     * 
     * @param tipus    Tipus de missatge
     * @param missatge Missatge ja encriptat
     * @param emisor   Persona que crea el missatge
     */
    public MissatgeDesencriptat(TipusMissatgeDesencriptat tipus, boolean esMissatgeTCP, Persona emisor,
            String missatge) {
        this.tipus = tipus;
        this.missatge = missatge;
        this.emisor = emisor;
        this.esMissatgeTCP = esMissatgeTCP;
    }

    /**
     * Retorna el missatge
     * 
     * @return Missatge
     */
    public String getMissatge() {
        return missatge;
    }

    /**
     * Retorna el tipus del missatge
     * 
     * @return Tipus del missatge
     */
    public TipusMissatgeDesencriptat getTipus() {
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

    /**
     * Retorna el destinatari del missatge
     * 
     * @return Destinatari del missatge
     */
    public Persona getDestinatari() {
        return destinatari;
    }

    /**
     * Estableix el destinatari del missatge
     * 
     * @param p Destinatari del missatge
     */
    public void setDestinatari(Persona p) {
        destinatari = p;
    }

    /**
     * Retorna si el missatge és un missatge TCP.
     * 
     * @return True si el missatge és un missatge TCP, False en cas contrari
     */
    public boolean getEsMissatgeTCP() {
        return esMissatgeTCP;
    }
}

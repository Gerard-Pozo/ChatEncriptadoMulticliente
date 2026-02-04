package com.encrypted.chat.models;

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
    private Persona destinatari;

    /**
     * Constructor que permet crear un missatge amb el tipus, el missatge ja
     * encriptat i la persona que envia el missatge
     * 
     * @param tipus    Tipus de missatge
     * @param missatge Missatge ja encriptat
     * @param emisor   Persona que crea el missatge
     */
    public MissatgeDesencriptat(TipusMissatgeDesencriptat tipus, Persona emisor, String missatge) {
        this.tipus = tipus;
        this.missatge = missatge;
        this.emisor = emisor;
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

    public Persona getDestinatari() {
        return destinatari;
    }

    public void setDestinatari(Persona p) {
        destinatari = p;
    }
}

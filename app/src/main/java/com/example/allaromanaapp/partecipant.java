package com.example.allaromanaapp;

public class partecipant {

    private String idUtente;
    private String nomeP;

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getNomeP() {
        return nomeP;
    }

    public void setNomeP(String nomeP) {
        this.nomeP = nomeP;
    }

    public String getCognomeP() {
        return cognomeP;
    }

    public void setCognomeP(String cognomeP) {
        this.cognomeP = cognomeP;
    }

    private String cognomeP;

    public partecipant(){

    }

    public partecipant(String nome, String cognome, String idPartecipante){
        this.nomeP = nome;
        this.cognomeP = cognome;
        this.idUtente = idPartecipante;
    }

    public String getIdPartecipante() {
        return idUtente;
    }

    public void setIdPartecipante(String idPartecipante) {
        this.idUtente = idPartecipante;
    }










}

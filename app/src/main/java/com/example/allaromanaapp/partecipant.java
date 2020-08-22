package com.example.allaromanaapp;

public class partecipant {
    public String getRuolo() {
        return Ruolo;
    }

    public void setRuolo(String ruolo) {
        Ruolo = ruolo;
    }

    public String getIdGruppo() {
        return idGruppo;
    }

    public void setIdGruppo(String idGruppo) {
        this.idGruppo = idGruppo;
    }

    public String getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(String idUtente) {
        this.idUtente = idUtente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    private String idPartecipante;
    private String Ruolo;

    public String getIdPartecipante() {
        return idPartecipante;
    }

    public void setIdPartecipante(String idPartecipante) {
        this.idPartecipante = idPartecipante;
    }

    private String idGruppo;
    private String idUtente;
    private String nome, cognome;

    public partecipant(){

    }

    public partecipant(String Ruolo, String idGruppo, String idUtente, String nome, String cognome, String idPartecipante){
        this.Ruolo = Ruolo;
        this.idGruppo = idGruppo;
        this.idUtente = idUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.idPartecipante = idPartecipante;
    }



}

package com.example.allaromanaapp;

public class notify {
   String nome,cognome;
   String id;
   Double debito;
   String letto;
   String data;

    public notify(String nome, String cognome, String id, Double debito, String letto, String data) {
        this.nome = nome;
        this.cognome = cognome;
        this.id = id;
        this.debito = debito;
        this.letto = letto;
        this.data = data;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getDebito() {
        return debito;
    }

    public void setDebito(Double debito) {
        this.debito = debito;
    }

    public String getLetto() {
        return letto;
    }

    public void setLetto(String letto) {
        this.letto = letto;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}


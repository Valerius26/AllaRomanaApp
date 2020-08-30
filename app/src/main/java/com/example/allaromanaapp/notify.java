package com.example.allaromanaapp;

public class notify {
   String nome,cognome;
   String id;
   Long debito;

    public notify(String nome, String cognome, String id, Long debito) {
        this.nome = nome;
        this.cognome = cognome;
        this.id = id;
        this.debito = debito;
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

    public Long getDebito() {
        return debito;
    }

    public void setDebito(Long debito) {
        this.debito = debito;
    }
}


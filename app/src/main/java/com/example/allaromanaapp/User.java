package com.example.allaromanaapp;

public class User {
    private String nome,cognome,email,password,idUser,idRef;
    Long bilancio = Long.valueOf(0);

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public User(){

    }


    public User(String nome, String cognome, String email, String password, String idUser, Long bilancio, String idRef){
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.bilancio = bilancio;
        this.idUser = idUser;
        this.idRef = idRef;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getBilancio() {
        return bilancio;
    }


    public String getIdRef() {
        return idRef;
    }

    public void setIdRef(String idRef) {
        this.idRef = idRef;
    }

    public void setBilancio(Long bilancio) {
        this.bilancio = bilancio;
    }
}

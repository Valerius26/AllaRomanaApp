package com.example.allaromanaapp;

public class Reported {


    private String id;
    private String name;
    private String surname;
    private String e_mail;
    private String id_ref;

    public Reported(String id, String name, String surname, String e_mail, String id_ref) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.e_mail = e_mail;
        this.id_ref = id_ref;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getId_ref() {
        return id_ref;
    }

    public void setId_ref(String id_ref) {
        this.id_ref = id_ref;
    }
}

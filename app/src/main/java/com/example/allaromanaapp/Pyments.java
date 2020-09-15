package com.example.allaromanaapp;

public class Pyments {


    private String id;
    private String name;
    private String surname;
    private Long debt;
    private String date;
    private String id_ref;

    public Pyments(String id, String name, String surname, Long debt, String date, String id_ref) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.debt = debt;
        this.date = date;
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

    public Long getDebt() {
        return debt;
    }

    public void setDebt(Long debt) {
        this.debt = debt;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId_ref() {
        return id_ref;
    }

    public void setId_ref(String id_ref) {
        this.id_ref = id_ref;
    }
}

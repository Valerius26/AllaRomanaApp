package com.example.allaromanaapp;

public class Creditors {


    private String id;
    private String name;
    private String surname;
    private Long debt;
    private String data;

    public Creditors(String id, String name, String surname, Long debt, String data) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.debt = debt;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

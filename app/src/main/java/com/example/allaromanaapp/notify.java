package com.example.allaromanaapp;

public class notify {
    String id;
    String id_pagante;
    String id_debito;
    String letto;

    public notify(String id, String id_pagante, String id_debito, String letto) {
        this.id = id;
        this.id_pagante = id_pagante;
        this.id_debito = id_debito;
        this.letto = letto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_pagante() {
        return id_pagante;
    }

    public void setId_pagante(String id_pagante) {
        this.id_pagante = id_pagante;
    }

    public String getId_debito() {
        return id_debito;
    }

    public void setId_debito(String id_debito) {
        this.id_debito = id_debito;
    }

    public String getLetto() {
        return letto;
    }

    public void setLetto(String letto) {
        this.letto = letto;
    }
}

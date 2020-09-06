package com.example.allaromanaapp;

import java.util.ArrayList;

class DataHolder {
    final ArrayList<User> people = new ArrayList<>();

    private DataHolder() {}

    static DataHolder getInstance() {
        if( instance == null ) {
            instance = new DataHolder();
        }
        return instance;
    }

    private static DataHolder instance;
}

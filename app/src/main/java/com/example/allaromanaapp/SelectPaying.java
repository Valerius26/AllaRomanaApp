package com.example.allaromanaapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class SelectPaying extends AppCompatActivity {

    TextView title,list;
    FloatingActionButton retAdd;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_selectpayng);

        title = findViewById(R.id.TitleInPay);
        list = findViewById(R.id.ListaPart);
        retAdd = findViewById(R.id.addPartecipant);
        recyclerView = findViewById(R.id.recycler2);



    }
}
package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SelectPaying extends AppCompatActivity {

    TextView title,list,importTitle;
    FloatingActionButton retAdd;
    RecyclerView recyclerView;
    EditText editImport;
    Button pay;
    String creatorID, accountID;
    FirebaseFirestore db;
    ViewAdapterSelectingPay adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activiy_selectpayng);

        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.TitleInPay);
        list = findViewById(R.id.ListaPart);
        retAdd = findViewById(R.id.addPartecipant);
        recyclerView = findViewById(R.id.recycler2);
        importTitle = findViewById(R.id.importTitle);
        editImport = findViewById(R.id.editImport);
        pay = findViewById(R.id.payBtn);


        Intent intent = getIntent();
        creatorID = intent.getStringExtra("idCreatore");
        accountID = intent.getStringExtra("idAccount");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        showPartecipant();


    }

    private void showPartecipant() {

        db.collection("users").document(creatorID).collection("accounts")
                .document(accountID).collection("partecipants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       ArrayList<partecipant> partecipants = new ArrayList<>();
                       for(DocumentSnapshot documentSnapshot: task.getResult()){
                           partecipant p  = new partecipant(documentSnapshot.getString("nomePartecipante"),
                                   documentSnapshot.getString("cognomePartecipante"),documentSnapshot.getString("idUtente"));
                           partecipants.add(p);
                       }
                        adapter = new ViewAdapterSelectingPay(SelectPaying.this, getApplicationContext(), partecipants);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}
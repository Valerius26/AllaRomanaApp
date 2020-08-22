package com.example.allaromanaapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class AddPartecipantActivity extends AppCompatActivity {

    private String groupID, userID;
    private TextView nomeGruppo, descrizioneGruppo;
    RecyclerView recyclerView;
    FloatingActionButton CreatePartecipantBtn;
    ArrayList<user> users;
    RecyclerViewAdapter3 adapter;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpartecipant);

        Intent intent = getIntent();
        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();

        groupID = intent.getStringExtra("groupId");
        userID = fAuth.getCurrentUser().getUid();

        users = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        setUpRecyclerView();
        setUpFirestore();
        loadDataFromFirebase();

    }



    private void loadDataFromFirebase() {

        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot querySnapshot : task.getResult()){
                    user utente = new user(querySnapshot.getString("nome"),
                            querySnapshot.getString("cognome"), querySnapshot.getString("e-mail"),
                            querySnapshot.getString("password"), querySnapshot.getId() );
                    users.add(utente);
                }
                adapter = new RecyclerViewAdapter3(AddPartecipantActivity.this, users, getApplicationContext() );
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddPartecipantActivity.this, R.string.errore, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpFirestore() {
        fStore = FirebaseFirestore.getInstance();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
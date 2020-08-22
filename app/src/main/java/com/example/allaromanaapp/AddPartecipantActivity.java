package com.example.allaromanaapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AddPartecipantActivity extends AppCompatActivity {

    private String groupID, userID;
    private TextView nomeGruppo, descrizioneGruppo;
    RecyclerView recyclerView;
    FloatingActionButton CreatePartecipantBtn;
    ArrayList<user> users;
    RecyclerViewAdapter3 adapter;
    EditText editText;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpartecipant);

        editText = findViewById(R.id.searchUser);
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

        String string = editText.getText().toString().trim();


        /*searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchUsers(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });*/


    }





    /*private void searchUsers(String s) {
        String query = s.toLowerCase();


    }*/


    private void loadDataFromFirebase() {
        fStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot querySnapshot : task.getResult()){
                    String userid =  querySnapshot.getId();
                    if(!userid.equals(userID)) {
                        user utente = new user(querySnapshot.getString("nome"),
                                querySnapshot.getString("cognome"), querySnapshot.getString("e-mail"),
                                querySnapshot.getString("password"), userid);
                        users.add(utente);
                    }
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
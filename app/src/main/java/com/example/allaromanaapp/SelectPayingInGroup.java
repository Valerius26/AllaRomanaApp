package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SelectPayingInGroup extends AppCompatActivity {

    TextView title,list,importTitle;
    RecyclerView recyclerView;
    EditText editImport;
    Button pay;
    String currentUserID, groupID;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ViewAdapterSelectingPay adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_paying_in_group);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.TitleInPay);
        list = findViewById(R.id.ListaPart);
        recyclerView = findViewById(R.id.recycler2);
        importTitle = findViewById(R.id.importTitle);
        editImport = findViewById(R.id.editImport);
        pay = findViewById(R.id.payBtn);

        Intent intent = getIntent();


        groupID = intent.getStringExtra("idGruppo");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        showPartecipant();


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pagante = adapter.getPayingUser();
                if(!TextUtils.isEmpty(pagante)) {

                    String importo = editImport.getText().toString().trim();
                    Long importNumber = Long.valueOf(0);
                    int partecipantsSize = 0;

                    try {
                        int num = Integer.parseInt(importo);
                        if (TextUtils.isEmpty(importo)) {
                            editImport.setError("L'importo è richiesto");
                            return;
                        }else {
                            importNumber = (Long) Long.valueOf(importo);
                            partecipantsSize = adapter.getItemCount();
                            if (importNumber < partecipantsSize) {
                                editImport.setError("L'importo dev'essere maggiore o ugale numero di partecipanti");
                            }else {


                                }

                            }
                            return;

                    } catch (NumberFormatException e) {
                        editImport.setError("Inserisci un intero");
                    }


                    return;
                }
                else{
                    Toast.makeText(getApplicationContext(),"Seleziona un pagante", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void showPartecipant() {

        db.collection("users").document(currentUserID).collection("groups")
                .document(groupID).collection("partecipants").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<partecipant> partecipants = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            partecipant p  = new partecipant(documentSnapshot.getString("nomePartecipante"),
                                    documentSnapshot.getString("cognomePartecipante"),documentSnapshot.getString("idUtente"));
                            partecipants.add(p);
                        }
                        adapter = new ViewAdapterSelectingPay(SelectPayingInGroup.this, partecipants);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}
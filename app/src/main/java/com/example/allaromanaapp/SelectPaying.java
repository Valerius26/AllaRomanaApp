package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;


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

                                    ArrayList<String> debtors = new ArrayList<>();
                                    debtors = adapter.getDebtors();
                                    for (int position = 0; position < debtors.size(); position++) {
                                        String id_debtor = debtors.get(position);
                                        updateDB(pagante, id_debtor, importNumber, partecipantsSize);
                                    }
                                }
                                return;
                            }
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

    private void updateDB(final String pagante, final String debtor, Long importNumber, int partecipantsSize) {
        HashMap<String,String> hashMap = new HashMap<>();
        final int credit = (int) (importNumber/partecipantsSize);
        hashMap.put("credito",""+credit);
        hashMap.put("idDebitore",debtor);


        db.collection("users").document(pagante).collection("credits")
                .add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                 Toast.makeText(getApplicationContext(),"importo Pagato", Toast.LENGTH_SHORT).show();
                 updateDebtor(pagante,debtor,credit);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void updateDebtor(String pagante, String debtor, int credit) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("debito",""+credit);
        hashMap.put("idCreditore",pagante);

        db.collection("users").document(debtor).collection("debts")
                .add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
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
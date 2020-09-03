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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


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
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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

        retAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddUsers.class);
                intent.putExtra("idCreatore", creatorID);
                intent.putExtra("idAccount",accountID);
                startActivity(intent);
            }
        });


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
                                        recupera_nome_debitore(id_debtor,pagante,importNumber,partecipantsSize);
                                    }
                                    deletePartecipants(creatorID,accountID);
                                    deleteAccount(creatorID,accountID);
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

    private void recupera_nome_debitore(final String id_debtor, final String pagante, final Long importNumber, final int partecipantSize) {
        db.collection("users").document(id_debtor).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                } else {
                    String nome_debitore = value.getString("nome");
                    String cognome_debitore = value.getString("cognome");
                    updateDB(id_debtor, pagante, importNumber, partecipantSize, nome_debitore, cognome_debitore);
                }
            }
        });

    }

    private void deletePartecipants(final String creatorID, final String accountID) {
        db.collection("users").document(creatorID).collection("accounts")
                .document(accountID).collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot :task.getResult()){
                    String partecipant_id = documentSnapshot.getId();
                    delete(creatorID,accountID,partecipant_id);
                }
            }
        });
    }

    private void delete(String creatorID, String accountID, String partecipant_id) {
        db.collection("users").document(creatorID).collection("accounts")
                .document(accountID).collection("partecipants").document(partecipant_id)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void deleteAccount(String creatorID, String accountID) {
        db.collection("users").document(creatorID).collection("accounts")
                .document(accountID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void updateDB(final String debtor, final String pagante, Long importNumber, int partecipantsSize, String nome, String cognome) {

        HashMap<String,String> hashMap = new HashMap<>();
        final int credit = (int) (importNumber/partecipantsSize);
        hashMap.put("credito",""+credit);
        hashMap.put("idDebitore",debtor);
        hashMap.put("idCreatoreConto",creatorID);
        hashMap.put("nome debitore",nome);
        hashMap.put("cognome debitore",cognome);


        db.collection("users").document(pagante).collection("credits")
                .add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                 Toast.makeText(getApplicationContext(),"importo Pagato", Toast.LENGTH_SHORT).show();
                 String id_credito = documentReference.getId();
                 //updateBalanceCredit(pagante,credit); //devo pensare a qualcos altro...
                 recupera_nome_creditore(pagante,debtor,credit,id_credito);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void recupera_nome_creditore(final String pagante, final String debtor, final int credit, final String id_credito) {
        db.collection("users").document(pagante).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){

                }else {

                    String nome_creditore = value.getString("nome");
                    String cognome_creditore = value.getString("cognome");
                    updateDebtor(pagante, debtor, credit, nome_creditore, cognome_creditore,id_credito);
                }
            }
        });

    }


    private void updateDebtor(final String pagante, final String debtor, final int credit, final String nome, final String cognome, final String id_credito) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("debito",""+credit);
        hashMap.put("idCreditore",pagante);
        hashMap.put("nome creditore",nome);
        hashMap.put("cognome creditore",cognome);

        db.collection("users").document(debtor).collection("debts")
                .add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                //updateBalanceDebit(debtor,credit);
                inviaNotifica(debtor, pagante, nome, cognome, credit);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBalanceDebit(String debtor, int credit) {
        DocumentReference documentReference = db.collection("users").document(debtor);
        documentReference.update("bilancio", FieldValue.increment(-credit)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void inviaNotifica(String debtor,String pagante, String nomeMittente, String cognomeMittente, int credit) {

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomeMittente",nomeMittente);
        hashMap.put("cognomeMittente",cognomeMittente);
        hashMap.put("idMittente",pagante);
        hashMap.put("daPagare",""+credit);
        hashMap.put("letto","no");
        hashMap.put("testo","Ti ricordo che hai un debito con me pari a euro " +
                credit + ".\nRimborsami al più presto, grazie.");

        db.collection("users").document(debtor).collection("notify")
                .add(hashMap);
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
                        adapter = new ViewAdapterSelectingPay(SelectPaying.this, partecipants);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
    private void updateBalanceCredit(String pagante, int credit) {

        DocumentReference documentReference = db.collection("users").document(pagante);
        documentReference.update("bilancio", FieldValue.increment(credit));

    }
}
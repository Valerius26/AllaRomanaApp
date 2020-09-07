package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button profileBtn, balanceBtn, historyBtn, payBtn, messageBtn, settingsBtn, mapBtn, groupBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID;
    String name,surname;
    CardView nuovaNotifica;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getUid();
        account_delete(currentUserID);
        group_delete();

        profileBtn = (Button) findViewById(R.id.buttonProfile);
        balanceBtn = (Button) findViewById(R.id.buttonDebitoCredito);
        historyBtn = (Button) findViewById(R.id.storyBtn);
        payBtn = (Button)findViewById(R.id.buttonGroup);
        messageBtn = (Button) findViewById(R.id.buttonMessage);
        settingsBtn = (Button) findViewById(R.id.buttonImpostation);
        nuovaNotifica = (CardView) findViewById(R.id.notifyCounter);
        mapBtn = (Button) findViewById(R.id.mapBtn);
        groupBtn = (Button) findViewById(R.id.GroupBtn);


        nuovaNotifica.setVisibility(View.INVISIBLE);

        notifyNew(currentUserID);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("asfagfas",currentUserID);
                createAccount(currentUserID);
            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), notificationActivity.class));
            }
        });

        balanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), balanceActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SettingActivity.class));
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),groupActivity.class));
            }
        });

    }

    private void group_delete() {
        db.collection("users").document(currentUserID).collection("groups")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    if(documentSnapshot.getString("Stato").equals("Non creato")){
                        String id = documentSnapshot.getId();
                        deleteNonCreated(id);
                    }
                }
            }
        });
    }

    private void deleteNonCreated(String id) {
        db.collection("users").document(currentUserID).collection("groups")
                .document(id).delete();
    }

    private void notifyNew(String currentUserID) {
        db.collection("users").document(currentUserID).collection("notify")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for(DocumentSnapshot documentSnapshot: task.getResult()){
                        if(documentSnapshot.get("letto").equals("no")){
                            nuovaNotifica.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void account_delete(String currentUserID) {
        final CollectionReference collectionReference = db.collection("users").document(currentUserID)
                .collection("accounts");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = documentSnapshot.getId();
                        deletePartecipants(collectionReference,id);
                        deleteAccount(collectionReference,id);
                    }
                }
            }
        });
    }

    private void deleteAccount(CollectionReference collectionReference, String id) {
        collectionReference.document(id).delete();
    }

    private void deletePartecipants(CollectionReference collectionReference, String id) {
        final CollectionReference collectionReference1 = collectionReference.document(id).collection("partecipants");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()) {
                    String id_partecipant = documentSnapshot.getId();
                    delete(collectionReference1, id_partecipant);
                }
            }
        });
    }

    private void delete(CollectionReference collectionReference1, String id_partecipant) {
        collectionReference1.document(id_partecipant).delete();
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


    private void createAccount(final String creatorID) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Creatore",""+creatorID);
        db.collection("users").document(creatorID).collection("accounts").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String accountID = documentReference.getId();
                startFirstPartecipant(accountID,creatorID);
            }
        });
    }

    private void startFirstPartecipant(final String accountID, final String creatorID) {
        DocumentReference documentReference = db.collection("users").document(creatorID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.d("ErroreBanaleEStupido", ""+error.getMessage());
                }
                else {
                    name = value.getString("nome");
                    surname = value.getString("cognome");
                    createFirstPartecipant(accountID, creatorID, name, surname);
                }
            }
        });
    }


    private void createFirstPartecipant(final String accountID, final String creatorID, String nome, String cognome) {
        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomePartecipante",nome);
        hashMap.put("cognomePartecipante",cognome);
        hashMap.put("idUtente",creatorID);

        db.collection("users").document(creatorID).collection("accounts").document(accountID)
                .collection("partecipants").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent intent = new Intent(getApplicationContext(),AddUsers.class);
                intent.putExtra("idCreatore", creatorID);
                intent.putExtra("idAccount",accountID);
             //   getApplicationContext().startActivity(intent);
                MainActivity.this.startActivity(intent);
            }
        });

    }



}
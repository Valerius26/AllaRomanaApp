package com.example.allaromanaapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button profileBtn, balanceBtn, historyBtn, payBtn, messageBtn, settingsBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID;
    String name,surname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUserID = firebaseAuth.getUid();


        profileBtn = (Button) findViewById(R.id.buttonProfile);
        balanceBtn = (Button) findViewById(R.id.buttonDebitoCredito);
        historyBtn = (Button) findViewById(R.id.storyBtn);
        payBtn = (Button)findViewById(R.id.buttonGroup);
        messageBtn = (Button) findViewById(R.id.buttonMessage);
        settingsBtn = (Button) findViewById(R.id.buttonImpostation);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(currentUserID);
            }
        });
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
                getApplicationContext().startActivity(intent);
            }
        });

    }

}
package com.example.allaromanaapp;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class GroupDetail extends AppCompatActivity {

    private String groupID, userID;
    private TextView nomeGruppo, descrizioneGruppo;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupdetail_activity);

        Intent intent = getIntent();
        fAuth = FirebaseAuth.getInstance();

        fStore = FirebaseFirestore.getInstance();

        groupID = intent.getStringExtra("groupId");
        userID = fAuth.getCurrentUser().getUid();

        nomeGruppo = findViewById(R.id.groupTitleDet);
        descrizioneGruppo = findViewById(R.id.descriptionDet);

        loadGroupInfo();

    }

    private void loadGroupInfo() {
        DocumentReference docRef = fStore.collection("users").document(userID).collection("groups").
                document(groupID);

        docRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                else {
                    nomeGruppo.setText(value.getString("Nome gruppo"));
                    descrizioneGruppo.setText(value.getString("Descrizione"));
                }
            }
        });


    }
}
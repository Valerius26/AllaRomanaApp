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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class GroupDetail extends AppCompatActivity {

    private String groupID, userID;
    private TextView nomeGruppo, descrizioneGruppo;
    RecyclerView recyclerView;
    FloatingActionButton CreatePartecipantBtn;
    ArrayList<partecipant> partecipants;
    RecyclerViewAdapter2 adapter;

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

        partecipants = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        setUpRecyclerView();
        setUpFirestore();
        loadDataFromFirebase();

        CreatePartecipantBtn = (FloatingActionButton) findViewById(R.id.addPartecipant);



        CreatePartecipantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewGroupActivity.class));
            }
        });


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

    private void loadDataFromFirebase() {

        fStore.collection("users").document(userID).collection("groups")
                .document(groupID).collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot querySnapshot : task.getResult()){
                    partecipant partecipante = new partecipant(querySnapshot.getString("Ruolo"),
                            querySnapshot.getString("idGruppo"), querySnapshot.getString("idUtente"),
                            querySnapshot.getString("nomePartecipante"), querySnapshot.getString("cognomePartecipante"),
                            querySnapshot.getId());
                    partecipants.add(partecipante);
                }
                adapter = new RecyclerViewAdapter2(GroupDetail.this, partecipants, getApplicationContext() );
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupDetail.this, R.string.errore, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpFirestore() {
        fStore = FirebaseFirestore.getInstance();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
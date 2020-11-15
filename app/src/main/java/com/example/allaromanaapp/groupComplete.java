package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class groupComplete extends AppCompatActivity {

    TextView title,list;
    FloatingActionButton retAdd;
    RecyclerView recyclerView;
    Button finish;
    String currentUserID, groupID;
    FirebaseFirestore db;
    recVieGroupCompleteAdapter adapter;
    FirebaseAuth firebaseAuth;
    String groupTitle,groupDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_complete);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.Title);
        list = findViewById(R.id.ListaPart);
        retAdd = findViewById(R.id.addPartecipant);
        finish = findViewById(R.id.groupComplete);
        recyclerView = findViewById(R.id.recycler2);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("idGruppo");
        groupTitle = intent.getStringExtra("NomeGruppo");
        groupDescription = intent.getStringExtra("DescrizioneGruppo");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        showPartecipant();

        retAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(groupComplete.this,GroupDetail.class);
                intent.putExtra("idCreatore", currentUserID);
                intent.putExtra("idGruppo",groupID);
                intent.putExtra("NomeGruppo",groupTitle);
                intent.putExtra("DescrizioneGruppo",groupDescription);
                startActivity(intent);
            }
        });


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<User> partecipantsList = adapter.getPartecipantsList();
                for(User partecipant : partecipantsList){
                    if(!partecipant.getIdRef().equals(currentUserID)) {
                        createGroupIn(partecipant, partecipantsList);
                    }else{
                        setGroupState();
                    }
                }
                Toast.makeText(groupComplete.this, getString(R.string.groupCompleted),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(groupComplete.this,MainActivity.class));
            }
        });


    }

    private void setGroupState() {
        db.collection("users").document(currentUserID).collection("groups")
                .document(groupID).update("Stato","Creato");
    }

    private void createGroupIn(final User partecipante, final ArrayList<User> partecipantsList) {

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Stato","Creato");
        hashMap.put("Creato da", currentUserID);
        hashMap.put("Nome gruppo", groupTitle);
        hashMap.put("Descrizione", groupDescription);

        db.collection("users").document(partecipante.getIdRef()).collection("groups")
                .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String groupInUserID = task.getResult().getId();
                for(User p : partecipantsList){
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("idUtente",p.getIdRef());
                    hashMap.put("nomePartecipante",p.getNome());
                    hashMap.put("cognomePartecipante",p.getCognome());

                    db.collection("users").document(partecipante.getIdRef()).collection("groups")
                            .document(groupInUserID).collection("partecipants").add(hashMap);
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
                        ArrayList<User> partecipants = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            User user  =  new User(documentSnapshot.getString("nomePartecipante"),
                                    documentSnapshot.getString("cognomePartecipante"), documentSnapshot.getString("e-mail"),
                                    documentSnapshot.getString("password"), documentSnapshot.getId(),(Long) documentSnapshot.get("bilancio"), documentSnapshot.getString("idUtente"));

                            partecipants.add(user);
                        }
                        Collections.sort(partecipants, new Comparator<User>() {
                            @Override
                            public int compare(User u, User u1) {
                                return u.getNome().compareTo(u1.getCognome());
                            }
                        });
                        adapter = new recVieGroupCompleteAdapter(partecipants, groupComplete.this , groupID);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(groupComplete.this, GroupDetail.class));
    }
}
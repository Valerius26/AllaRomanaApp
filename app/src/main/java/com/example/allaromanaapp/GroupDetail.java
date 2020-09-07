package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GroupDetail extends AppCompatActivity {

    TextView title;
    EditText searchUsers;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String currentUserID,groupID;
    ArrayList<User> users;
    Button create;
    RecyclerView recyclerView;
    RecVieAdapterGroupDet adapter;
    private ArrayList<User> usersSearched;
    String groupTitle,groupDescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("idGruppo");
        groupTitle = intent.getStringExtra("NomeGruppo");
        groupDescription = intent.getStringExtra("DescrizioneGruppo");
        title = findViewById(R.id.title);
        searchUsers = findViewById(R.id.searchUser);
        users = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();

        create = findViewById(R.id.createGroupBtn);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minTwoPartecipant();

            }
        });
        setUpRecyclerView();
        setUpFirestore();
        loadDataFromFirebase();


        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty())
                    setAdapter(editable.toString());

                else {
                    usersSearched = new ArrayList<User>();
                    usersSearched.clear();
                    recyclerView.removeAllViews();
                }
            }
        });

    }

    private void setAdapter(final String toString) {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String fullName = "";
                usersSearched = new ArrayList<User>();
                usersSearched.clear();
                recyclerView.removeAllViews();
                for(DocumentSnapshot querySnapshot: task.getResult()){
                    String userid =  querySnapshot.getId();

                    if(!userid.equals(currentUserID)) {
                        String name = querySnapshot.getString("nome");
                        String surname = querySnapshot.getString("cognome");
                        fullName = name + " " + surname;
                        User utente = new User(name,
                                surname, querySnapshot.getString("e-mail"),
                                querySnapshot.getString("password"), userid,(Long) querySnapshot.get("bilancio"), querySnapshot.getString("idUtente"));


                        if (fullName.toLowerCase().contains(toString.toLowerCase())) {
                            usersSearched.add(utente);

                        }
                    }
                }
                adapter = new RecVieAdapterGroupDet(usersSearched, GroupDetail.this, groupID);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void loadDataFromFirebase() {

        db.collection("users")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot querySnapshot : task.getResult()){
                    if(!querySnapshot.getId().equals(currentUserID)) {
                        User user = new User(querySnapshot.getString("nome"),
                                querySnapshot.getString("cognome"), querySnapshot.getString("e-mail"),
                                querySnapshot.getString("password"), querySnapshot.getId(), (Long) querySnapshot.get("bilancio"), querySnapshot.getString("idUtente"));
                        users.add(user);
                    }
                }
                adapter = new RecVieAdapterGroupDet(users, GroupDetail.this, groupID);
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
        db = FirebaseFirestore.getInstance();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
    }

    private void minTwoPartecipant() {
        db.collection("users").document(currentUserID).collection("groups").document(groupID)
                .collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> partecipants = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    String id = documentSnapshot.getId();
                    partecipants.add(id);
                }
                if(partecipants.size() < 2){
                    Toast.makeText(GroupDetail.this,R.string.aggiungiPartecipante, Toast.LENGTH_SHORT).show();

                }
                else{
                    Intent intent1 = new Intent(GroupDetail.this, groupComplete.class);
                    intent1.putExtra("idCreatore",currentUserID);
                    intent1.putExtra("idGruppo",groupID);
                    intent1.putExtra("NomeGruppo",groupTitle);
                    intent1.putExtra("DescrizioneGruppo",groupDescription);
                    startActivity(intent1);
                }

            }
        });
    }
}
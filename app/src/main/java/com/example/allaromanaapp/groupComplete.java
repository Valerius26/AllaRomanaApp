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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class groupComplete extends AppCompatActivity {

    TextView title,list;
    FloatingActionButton retAdd;
    RecyclerView recyclerView;
    Button finish;
    String currentUserID, groupID;
    FirebaseFirestore db;
    recVieGroupCompleteAdapter adapter;
    FirebaseAuth firebaseAuth;

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
        recyclerView = findViewById(R.id.recycler2);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("idGruppo");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        showPartecipant();

        retAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GroupDetail.class);
                intent.putExtra("idCreatore", currentUserID);
                intent.putExtra("idGruppo",groupID);
                startActivity(intent);
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
                        adapter = new recVieGroupCompleteAdapter(partecipants, groupComplete.this , groupID);
                        recyclerView.setAdapter(adapter);
                    }
                });
    }
}
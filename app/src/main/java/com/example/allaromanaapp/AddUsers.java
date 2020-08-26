package com.example.allaromanaapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddUsers extends AppCompatActivity {
    EditText searchUser;
    List<User> usersList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    ViewAdapterAddUsers adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpartecipant);

        usersList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        searchUser = findViewById(R.id.searchUser);
        recyclerView = (RecyclerView) findViewById(R.id.recycler3);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        showData();
    }

    private void showData() {

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    User user = new User(documentSnapshot.getString("nome"),documentSnapshot.getString("cognome"),
                            documentSnapshot.getString("e-mail"),documentSnapshot.getString("password"),
                            documentSnapshot.getId(), (Long) documentSnapshot.get("bilancio"));
                    usersList.add(user);
                }
                adapter = new ViewAdapterAddUsers(AddUsers.this, usersList, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(AddUsers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
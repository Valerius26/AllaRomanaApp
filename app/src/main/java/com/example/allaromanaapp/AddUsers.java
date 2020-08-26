package com.example.allaromanaapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUsers extends AppCompatActivity {
    EditText searchUser;
    List<User> usersList;
    List<User> usersSearched;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    ViewAdapterAddUsers adapter;
    SearchInAddUsersAdapter searchAdapter;
    String currentUserID;
    String creatorID,accountID,inAllAccountID;
    String name = "";
    String surname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpartecipant);

        Intent intent = getIntent();
        creatorID = intent.getStringExtra("idCreatore");
        accountID = intent.getStringExtra("idAccount");
        inAllAccountID = intent.getStringExtra("idAccountInAll");


        usersList = new ArrayList<>();
        usersSearched = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();



        searchUser = findViewById(R.id.searchUser);
        recyclerView = (RecyclerView) findViewById(R.id.recycler3);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        showData();

        searchUser.addTextChangedListener(new TextWatcher() {
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




    private void setAdapter(final String searchedString) {
        recyclerView = findViewById(R.id.recycler3);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String fullName = "";
                usersSearched = new ArrayList<User>();
                usersSearched.clear();
                recyclerView.removeAllViews();
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String currentID = documentSnapshot.getId();
                    User user = new User();
                    if(!currentUserID.equals(currentID)) {
                        user = new User(documentSnapshot.getString("nome"), documentSnapshot.getString("cognome"),
                                documentSnapshot.getString("e-mail"), documentSnapshot.getString("password"),
                                currentID, (Long) documentSnapshot.get("bilancio"));
                        usersList.add(user);
                    }


                        if (fullName.toLowerCase().contains(searchedString.toLowerCase())) {
                            usersSearched.add(user);

                        }
                    }

                searchAdapter = new SearchInAddUsersAdapter(AddUsers.this, usersSearched, getApplicationContext(),creatorID,accountID,inAllAccountID);
                recyclerView.setAdapter(searchAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void showData() {

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String currentID = documentSnapshot.getId();
                    if(!currentUserID.equals(currentID)) {
                        User user = new User(documentSnapshot.getString("nome"), documentSnapshot.getString("cognome"),
                                documentSnapshot.getString("e-mail"), documentSnapshot.getString("password"),
                                currentID, (Long) documentSnapshot.get("bilancio"));
                        usersList.add(user);
                    }
                }
                adapter = new ViewAdapterAddUsers(AddUsers.this, usersList, getApplicationContext(),creatorID,accountID,inAllAccountID);
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
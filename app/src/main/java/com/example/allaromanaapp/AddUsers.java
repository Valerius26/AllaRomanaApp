package com.example.allaromanaapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    List<User> usersSearched;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore db;
    ViewAdapterAddUsers adapter;
    SearchInAddUsersAdapter searchAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpartecipant);

        usersList = new ArrayList<>();
        usersSearched = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

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
                for(DocumentSnapshot querySnapshot: task.getResult()){
                    String userid =  querySnapshot.getId();

                        String name = querySnapshot.getString("nome");
                        String surname = querySnapshot.getString("cognome");
                        fullName = name + " " + surname;
                        User utente = new User(name,
                                surname, querySnapshot.getString("e-mail"),
                                querySnapshot.getString("password"), userid,(Long) querySnapshot.get("bilancio"));


                        if (fullName.toLowerCase().contains(searchedString.toLowerCase())) {
                            usersSearched.add(utente);

                        }
                    }

                searchAdapter = new SearchInAddUsersAdapter(AddUsers.this, usersSearched, getApplicationContext());
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
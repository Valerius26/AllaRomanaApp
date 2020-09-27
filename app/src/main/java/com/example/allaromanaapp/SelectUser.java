package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectUser extends AppCompatActivity {

    EditText searchUser;
    List<User> usersList;
    List<User> usersSearched;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    SelectUserAdapter adapter;
    String currentUserID;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        address = intent.getStringExtra("FullAddress");
        usersList = new ArrayList<>();
        usersSearched = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        searchUser = findViewById(R.id.searchUser);
        recyclerView = findViewById(R.id.recycler3);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        showData();


        searchUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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

    private void showData() {

        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String currentID = documentSnapshot.getId();
                    if(!currentUserID.equals(currentID)) {
                        User user = new User(documentSnapshot.getString("nome"), documentSnapshot.getString("cognome"),
                                documentSnapshot.getString("e-mail"), documentSnapshot.getString("password"),
                                currentID, (Long) documentSnapshot.get("bilancio"), documentSnapshot.getString("idUtente"));
                        usersList.add(user);
                    }
                }
                Collections.sort(usersList, new Comparator<User>() {
                    @Override
                    public int compare(User u, User u1) {
                        return u.getNome().compareTo(u1.getCognome());
                    }
                });
                adapter = new SelectUserAdapter(usersList, SelectUser.this, address );
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(SelectUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    if(!userid.equals(currentUserID)) {
                        String name = querySnapshot.getString("nome");
                        String surname = querySnapshot.getString("cognome");
                        fullName = name + " " + surname;
                        User utente = new User(name,
                                surname, querySnapshot.getString("e-mail"),
                                querySnapshot.getString("password"), userid,(Long) querySnapshot.get("bilancio"), querySnapshot.getString("idUtente"));


                        if (fullName.toLowerCase().contains(searchedString.toLowerCase())) {
                            usersSearched.add(utente);

                        }
                    }
                }
                Collections.sort(usersSearched, new Comparator<User>() {
                    @Override
                    public int compare(User u, User u1) {
                        return u.getNome().compareTo(u1.getCognome());
                    }
                });
                adapter = new SelectUserAdapter(usersSearched, SelectUser.this, address);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

}
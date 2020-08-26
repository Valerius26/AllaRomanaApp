package com.example.allaromanaapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
    String name, surname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpartecipant);

        usersList = new ArrayList<>();
        usersSearched = new ArrayList<>();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getCurrentUser().getUid();

        createAccount(currentUserID);

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
                    String currentID = documentSnapshot.getId();
                    if(!currentUserID.equals(currentID)) {
                        User user = new User(documentSnapshot.getString("nome"), documentSnapshot.getString("cognome"),
                                documentSnapshot.getString("e-mail"), documentSnapshot.getString("password"),
                                currentID, (Long) documentSnapshot.get("bilancio"));
                        usersList.add(user);
                    }
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

    private void createAccount(final String creatorID) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Creatore",""+creatorID);
        hashMap.put("id in All","null");
        db.collection("users").document(creatorID).collection("accounts").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String accountID = documentReference.getId();
                startFirstPartecipant(accountID,creatorID);
                createAccountinAllAccounts(accountID,creatorID);
            }
        });
    }

    private void startFirstPartecipant(final String accountID, final String creatorID) {
        DocumentReference documentReference = db.collection("users").document(creatorID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name = value.getString("nome");
                surname = value.getString("cognome");
                createFirstPartecipant(accountID,creatorID,name,surname);
            }
        });
    }

    private void createAccountinAllAccounts(final String accountID, final String creatorID) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id",accountID);
        hashMap.put("creatore",creatorID);

        db.collection("Allaccounts").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String InAllAccountID = documentReference.getId();
                AddIdInAccount(InAllAccountID,accountID,creatorID);
            }
        });
    }

    private void AddIdInAccount(String inAllAccountID, String accountID, String creatorID) {
        Map<String,String> map = new HashMap<>();
        DocumentReference documentReference = db.collection("users").document(creatorID)
                .collection("accounts").document(accountID);

        documentReference.update("id in All",inAllAccountID);
    }

    private void createFirstPartecipant(String accountID, String creatorID, String nome, String cognome) {
        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomePartecipante",nome);
        hashMap.put("cognomePartecipante",cognome);
        hashMap.put("idUtente",creatorID);

        db.collection("users").document(creatorID).collection("accounts").document(accountID)
                .collection("partecipants").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        });

    }
}
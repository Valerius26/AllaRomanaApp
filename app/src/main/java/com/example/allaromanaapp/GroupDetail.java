package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
    String currentUserID;
    ArrayList<User> users;
    Button create;
    RecyclerView recyclerView;
    RecVieAdapterGroupDet adapter;
    private ArrayList<User> partecipants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        partecipants = new ArrayList<>();
        Toast.makeText(GroupDetail.this, "Una volta creato il gruppo non sarà più possibile modificarne i partecipanti", Toast.LENGTH_LONG).show();
        title = findViewById(R.id.title);
        searchUsers = findViewById(R.id.searchUser);
        users = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();

        create = findViewById(R.id.createGroupBtn);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setUpRecyclerView();
        setUpFirestore();
        loadDataFromFirebase();



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
                                querySnapshot.getString("password"), querySnapshot.getId(), (Long) querySnapshot.get("bilancio"));
                        users.add(user);
                    }
                }
                adapter = new RecVieAdapterGroupDet(users, GroupDetail.this, GroupDetail.this);
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

    public ArrayList<User> getPartecipants(){
        return partecipants;
    }

    public void setPartecipants(ArrayList<User> partecipants){
        this.partecipants = partecipants;
    }
}
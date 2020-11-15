package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static androidx.constraintlayout.solver.widgets.ConstraintWidget.INVISIBLE;

public class groupActivity extends AppCompatActivity {

    TextView EmptyList;
    RecyclerView recyclerView;
    FloatingActionButton CreateGroupBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    ArrayList<group> groups;
    RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        EmptyList = findViewById(R.id.emptyList);
        EmptyList.setVisibility(android.view.View.INVISIBLE);
        groups = new ArrayList<>();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        setUpRecyclerView();
        setUpFirestore();
        loadDataFromFirebase();



        CreateGroupBtn = (FloatingActionButton) findViewById(R.id.createGroupBtn);

        CreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewGroupActivity.class));
            }
        });


    }

    private void loadDataFromFirebase() {

        fStore.collection("users").document(userID).collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot querySnapshot : task.getResult()){
                    group gruppo = new group(querySnapshot.getString("Nome gruppo"), querySnapshot.getString("Descrizione"),
                            querySnapshot.getId(), querySnapshot.getString("Creato da"));
                    groups.add(gruppo);
                }
                    Collections.sort(groups, new Comparator<group>() {
                        @Override
                        public int compare(group g, group g1) {
                            return g.getTitle().compareTo(g1.getTitle());
                        }
                    });
                adapter = new RecyclerViewAdapter(groups, groupActivity.this);
                recyclerView.setAdapter(adapter);

                if (groups.size() == 0)
                {
                    EmptyList.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(groupActivity.this, R.string.errore, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpFirestore() {
        fStore = FirebaseFirestore.getInstance();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


}
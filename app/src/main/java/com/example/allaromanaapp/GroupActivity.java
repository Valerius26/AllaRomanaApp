package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class GroupActivity extends AppCompatActivity {

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

        EmptyList = findViewById(R.id.emptyList);
        EmptyList.setVisibility(View.INVISIBLE);
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


     /*
        CollectionReference collectionReference = fStore.collection("users").document(userID).collection("groups");


        collectionReference.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                for(DocumentSnapshot ds: value){
                    Title.setText(ds.getString("Nome gruppo"));
                    Description.setText(ds.getString("Description"));
                }
            }
        });*/


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

                 adapter = new RecyclerViewAdapter(GroupActivity.this, groups, getApplicationContext());
                 recyclerView.setAdapter(adapter);

                if (groups.size() == 0)
                {
                    EmptyList.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupActivity.this, R.string.errore, Toast.LENGTH_LONG).show();
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


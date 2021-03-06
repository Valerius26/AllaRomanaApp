package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class notificationActivity extends AppCompatActivity {

    ProgressBar progressBar;
    ImageButton back;
    Button deleteBtn;
    TextView notTitle;
    RecyclerView recyclerView;
    notificationAdapter adapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID,letto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        progressBar = findViewById(R.id.progressBar);
        back = findViewById(R.id.back);
        deleteBtn = findViewById(R.id.deleteBtn);
        notTitle = findViewById(R.id.notTitle);
        recyclerView = findViewById(R.id.recycler5);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        setUpRecyclerView();
        loadDataFromFirebase();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              final CollectionReference collectionReference = db.collection("users").document(currentUserID)
                      .collection("notify");

              collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      if(!task.getResult().isEmpty()) {
                          for (DocumentSnapshot documentSnapshot : task.getResult()) {
                              if(documentSnapshot.getString("letto").equals("si")){
                                  collectionReference.document(documentSnapshot.getId()).delete();
                              }
                          }
                          startActivity(new Intent(getApplicationContext(),notificationActivity.class));
                      }
                  }
              });
            }
        });


    }

    private void loadDataFromFirebase() {
        final NumberFormat nf = NumberFormat.getInstance();
        db.collection("users").document(currentUserID).collection("notify")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (!task.getResult().isEmpty()) {
                    ArrayList<notify> notifies = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        letto = documentSnapshot.getString("letto");
                        notify not = null;
                        try {
                            not = new notify(documentSnapshot.getString("nomeMittente"),
                                    documentSnapshot.getString("cognomeMittente"),documentSnapshot.getId(),
                                    nf.parse(documentSnapshot.getString("daPagare")).doubleValue(),letto,
                                    documentSnapshot.getString("data"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        notifies.add(not);
                    }
                    Collections.sort(notifies, new Comparator<notify>() {
                        @Override
                        public int compare(notify t2, notify t1) {
                            return t1.getData().compareTo(t2.getData());
                        }
                    });
                    progressBar.setVisibility(View.INVISIBLE);
                    adapter = new notificationAdapter(notifies,notificationActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }
        });

    }





    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
    }

}
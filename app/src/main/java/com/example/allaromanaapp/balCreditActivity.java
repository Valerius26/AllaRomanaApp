package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class balCreditActivity extends AppCompatActivity {
    ImageButton back;
    TextView title;
    Button otherPage;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseAuth fAuth;
    String currentUserID;
    creditsAdapter adapter;
    ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balcredit_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getUid();
        db = FirebaseFirestore.getInstance();
        back = findViewById(R.id.back);
        title = findViewById(R.id.creditTitle);
        otherPage = findViewById(R.id.goDebt);
        recyclerView = findViewById(R.id.recycler4);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        otherPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),balanceActivity.class));
            }
        });

        setUpRecyclerView();
        loadDataFromFirebase();
    }

    private void loadDataFromFirebase() {

        db.collection("users").document(currentUserID).collection("credits")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<Creditors> creditsList = new ArrayList();
                NumberFormat nf = NumberFormat.getInstance();
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    Creditors creditor = null;
                    try {
                        creditor = new Creditors(documentSnapshot.getString("idDebitore"),documentSnapshot.getString("nome debitore"),
                                documentSnapshot.getString("cognome debitore"),
                                nf.parse(documentSnapshot.getString("credito")).doubleValue(),documentSnapshot.getString("data"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    creditsList.add(creditor);
                }  Collections.sort(creditsList, new Comparator<Creditors>() {
                    @Override
                    public int compare(Creditors d, Creditors d1) {
                        return d.getName().compareTo(d1.getName());
                    }
                });
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new creditsAdapter(creditsList, getApplicationContext(), balCreditActivity.this);
                recyclerView.setAdapter(adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(balCreditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));
    }



}

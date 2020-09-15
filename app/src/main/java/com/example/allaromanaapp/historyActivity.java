package com.example.allaromanaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class historyActivity extends AppCompatActivity {


    TextView title;
    ImageButton back;
    FirebaseFirestore fStore;
    Button deleteBtn;
    String userID;
    FirebaseAuth fAuth;
    ArrayList<Pyments> payments = new ArrayList<>();
    historyAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getUid();
        recyclerView = findViewById(R.id.recycler5);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder notificationDialog = new AlertDialog.Builder(v.getContext());
                notificationDialog.setTitle("Elimina pagamenti");
                notificationDialog.setMessage("Sei sicuro di voler eliminare tutti i pagamenti?");

                notificationDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePayment();
                    }
                });
                notificationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                notificationDialog.create().show();
            }
        });

        setUpRecyclerView();
        setUpFirestore();
        loadDataFromFirebase();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(historyActivity.this,MainActivity.class));
            }
        });
    }

    private void deletePayment() {
        fStore.collection("users").document(userID).collection("payment")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    deleteP(documentSnapshot.getId());
                }
                restartActivity();
            }
        });
    }

    private void deleteP(String id) {
        fStore.collection("users").document(userID).collection("payment")
                .document(id).delete();
    }


    private void loadDataFromFirebase() {

        fStore.collection("users").document(userID).collection("payment").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot querySnapshot : task.getResult()){
                    Pyments payment = new Pyments(querySnapshot.getString("idPagato"), querySnapshot.getString("nomePagato"),
                            querySnapshot.getString("cognomePagato"), Long.valueOf(querySnapshot.getString("totalePagato")),
                            querySnapshot.getString("data"),querySnapshot.getId());
                    payments.add(payment);
                }
                Collections.sort(payments, new Comparator<Pyments>() {
                    @Override
                    public int compare(Pyments p1, Pyments p2) {
                        return p1.getDate().compareTo(p2.getDate());
                    }
                });

                adapter = new historyAdapter(payments, historyActivity.this);
                recyclerView.setAdapter(adapter);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(historyActivity.this, R.string.errore, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setUpFirestore() {
        fStore = FirebaseFirestore.getInstance();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler5);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

    }


    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class PosActivity extends AppCompatActivity {

    EditText card,pass;
    Button confirm;
    String creditorID,total,toPay;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        creditorID = intent.getStringExtra("idCreditore");
        Toast.makeText(PosActivity.this,""+creditorID,Toast.LENGTH_SHORT).show();
        total = intent.getStringExtra("Totale");
        Toast.makeText(PosActivity.this,""+total,Toast.LENGTH_SHORT).show();
        toPay = intent.getStringExtra("daPagare");
        Toast.makeText(PosActivity.this,""+toPay,Toast.LENGTH_SHORT).show();

        card = findViewById(R.id.cardNum);
        pass = findViewById(R.id.passwordCard);
        confirm = findViewById(R.id.confirmBtn);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = card.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if (TextUtils.isEmpty(number)) {
                    card.setError("Inserisci il numero di carta");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    pass.setError("Inserisci la password");
                    return;
                }


                if (number.equals("123456") && password.equals("123456")) {
                    deleteDebt();
                    deleteCredit();
                    //sendNotification();
                    //updatecurrentCard();
                    //updatecreditorCard();
                    //updatePayment();
                }else{

                    Toast.makeText(PosActivity.this,"Errore!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void updatecreditorCard() {
    }


    private void updatecurrentCard() {
    }

    private void sendNotification() {
    }

    private void deleteCredit() {
        if (total.equals("si")) {
            db.collection("users").document(creditorID).collection("credits")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if(documentSnapshot.getString("idDebitore").equals(currentUserID)){
                            deleteC(documentSnapshot.getId());
                        }
                    }
                }
            });
        }else{
            db.collection("users").document(creditorID).collection("credits")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if(documentSnapshot.getString("idDebitore").equals(currentUserID) &&
                                documentSnapshot.getString("credito").equals(toPay)){
                            deleteC(documentSnapshot.getId());
                            break;
                        }
                    }
                }
            });
        }
    }

    private void deleteC(String id) {
        db.collection("users").document(creditorID).collection("credits")
                .document(id).delete();
    }

    private void deleteDebt() {
        if (total.equals("si")) {
            db.collection("users").document(currentUserID).collection("debts")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if(documentSnapshot.getString("idCreditore").equals(creditorID)){
                            deleteD(documentSnapshot.getId());

                        }
                    }
                }
            });
        }else{
            db.collection("users").document(currentUserID).collection("debts")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        if(documentSnapshot.getString("idCreditore").equals(creditorID) &&
                         documentSnapshot.getString("debito").equals(toPay)){
                            deleteD(documentSnapshot.getId());
                            break;
                        }
                    }
                }
            });
        }
    }

    private void deleteD(String id){
        db.collection("users").document(currentUserID).collection("debts")
                .document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(PosActivity.this,"Il debito è stato saldato2",Toast.LENGTH_SHORT).show();
            }
        });

    }
}

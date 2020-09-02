package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class payActivity extends AppCompatActivity{

    TextView title,debt,totalDebt;
    Switch payTotal;
    Button payBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID,CreditorID;
    String CreditorName,CreditorSurname;
    Long CreditorCredit;
    Long toPay;
    ArrayList<Long> debts = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Intent intent = getIntent();
        CreditorName = intent.getStringExtra("name");
        CreditorSurname = intent.getStringExtra("surname");
        CreditorCredit = Long.valueOf(intent.getStringExtra("debt"));
        CreditorID = intent.getStringExtra("creditorID");


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();

        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.title);
        debt = findViewById(R.id.debt);
        totalDebt = findViewById(R.id.totalDebt);
        payTotal = findViewById(R.id.switch1);
        payBtn = findViewById(R.id.payBtn);

        loadTotalDebt();


    }

    private void loadTotalDebt() {
        db.collection("users").document(currentUserID).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Long CreditorTotalC = Long.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                   if(documentSnapshot.getString("idCreditore").equals(CreditorID)){
                       CreditorTotalC = CreditorTotalC + Long.valueOf(documentSnapshot.getString("debito"));
                       Log.d("totaleeee",""+CreditorTotalC);
                   }
                }

                goNow(CreditorTotalC);
            }
        });
    }

    private void goNow(final Long creditorTotalC) {
        debt.setText("Debito con " + CreditorName + " " + CreditorSurname + " : " + CreditorCredit + "$");

        totalDebt.setText("Debito totale con " + CreditorName + " " + CreditorSurname + " : " + creditorTotalC + "$");

        //l'importo da pagare dovrebbe corrispondere al debito che si seleziona a questo punto

        payTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(payTotal.isChecked()){
                    payBtn.setText("Salda il debito totale");
                    toPay = creditorTotalC;
                    Log.d("asdasdasd",""+toPay);
                }else{
                    payBtn.setText("Salda il debito");
                    toPay = CreditorCredit;
                }
            }
        });
    }




}
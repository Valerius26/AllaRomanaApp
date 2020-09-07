package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class payActivity extends AppCompatActivity{

    TextView title,debt,totalDebt,orBtn;
    Switch payTotal;
    Button payBtn,difference;
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
        orBtn = findViewById(R.id.oppure);
        difference = findViewById(R.id.compensa);

        loadTotalDebt();

        difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(CreditorID).collection("debts")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int vero = 0;
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            if(documentSnapshot.getString("idCreditore").equals(currentUserID)){
                                vero = 1;
                                break;
                            }
                        }
                        if(vero == 0){
                            Toast.makeText(payActivity.this, CreditorName + " " + CreditorSurname + " non ha nessun debito con te",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            loadTotalDebtTwo();
                        }
                    }
                });
            }
        });

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
                   }
                }

                goNow(CreditorTotalC);
            }
        });
    }

    private void loadTotalDebtTwo() {
        db.collection("users").document(currentUserID).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Long CreditorTotalC = Long.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idCreditore").equals(CreditorID)){
                        CreditorTotalC = CreditorTotalC + Long.valueOf(documentSnapshot.getString("debito"));
                    }
                }

                loadCreditorTotalDebt(CreditorTotalC);
            }
        });
    }

    private void loadCreditorTotalDebt(final Long CreditorTotalC) {
        db.collection("users").document(CreditorID).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Long CreditorTotalD = Long.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idCreditore").equals(currentUserID)){
                        CreditorTotalD = CreditorTotalD + Long.valueOf(documentSnapshot.getString("debito"));
                    }
                }

                Long total = CreditorTotalC - CreditorTotalD;
                if(total < 0){
                    //aggiorna con un unica tabella il suo di debito e il mio di credito
                }else
                    if(total > 0){
                        //aggiorna con un unica tabella il mio di debito e il suo di credito
                    }else{
                        //elimina i crediti e i debiti di entrambi
                    }
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
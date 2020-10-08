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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class payActivity extends AppCompatActivity{

    TextView title,debt,totalDebt,orText;
    Switch payTotal;
    Button payBtn,difference;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentDate;
    String currentUserID,CreditorID;
    String CreditorName,CreditorSurname;
    Long CreditorCredit;
    Long toPay;
    boolean total = false;

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

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();

        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.title);
        debt = findViewById(R.id.debt);
        totalDebt = findViewById(R.id.totalDebt);
        payTotal = findViewById(R.id.switch1);
        payBtn = findViewById(R.id.payBtn);
        orText = findViewById(R.id.oppure);
        difference = findViewById(R.id.compensa);
        orText.setText(getString(R.string.anche) + " " +  CreditorName + " " + CreditorSurname + " " + getString(R.string.conTe));

        loadTotalDebt();

        difference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(currentUserID).collection("credits")
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        int vero = 0;
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            if(documentSnapshot.getString("idDebitore").equals(CreditorID)){
                                vero = 1;
                                break;
                            }
                        }
                        if(vero == 0){
                            Toast.makeText(payActivity.this, CreditorName + " " + CreditorSurname + " " + getString(R.string.noDebtTo),Toast.LENGTH_SHORT).show();
                        }
                        else{
                            loadTotalDebtTwo();
                        }
                    }
                });
            }
        });


        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(payActivity.this, PosActivity.class);
                intent1.putExtra("idCreditore",CreditorID);
                if(total){
                    intent1.putExtra("Totale","si");
                    intent1.putExtra("daPagare",""+toPay);
                }else{
                    intent1.putExtra("Totale","no");
                    intent1.putExtra("daPagare",""+CreditorCredit);
                }
                startActivity(intent1);
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
        db.collection("users").document(currentUserID).collection("credits")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Long CreditorTotalD = Long.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idDebitore").equals(CreditorID)){
                        CreditorTotalD = CreditorTotalD + Long.valueOf(documentSnapshot.getString("credito"));
                    }
                }

                final Long total = CreditorTotalC - CreditorTotalD;
                recoverCurrentUserName(total);


            }
        });


    }

    private void recoverCurrentUserName(final Long total) {
        db.collection("users").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){

                }else{
                    String currentName = value.getString("nome");
                    String currentSurname = value.getString("cognome");
                    updateDb(total,currentName,currentSurname);

                }
            }
        });
    }

    private void updateDb(Long total, String currentName, String currentSurname) {
        if(total < 0){
            //aggiorna con un unica tabella il mio credito e il suo debito
            updateDebt(CreditorID,(-total),currentName,currentSurname,currentUserID);
            updateCredit(currentUserID,(-total),CreditorName,CreditorSurname,CreditorID);
            String text = R.string.compensationFirst + "\n" +
                    R.string.yourDebtNow + " " + (-total) + R.string.simbol;
            sendNotification(currentName,currentSurname,(-total),currentUserID,text);
            Toast.makeText(payActivity.this,getString(R.string.compensationSecond) + " " + CreditorName + " " + CreditorSurname + " " + getString(R.string.pariA) + " "  + (-total) + getString(R.string.simbol), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(payActivity.this,MainActivity.class));
        }else
        if(total > 0){
            //aggiorna con un unica tabella il mio di debito e il suo di credito
            updateDebt(currentUserID,total,CreditorName,CreditorSurname,CreditorID);
            updateCredit(CreditorID,total,currentName,currentSurname,currentUserID);
            String text =  R.string.compensationThird + "\n" +
                    R.string.myDebtNow + " " + total + " " + R.string.simbol;
            sendNotification(currentName,currentSurname,total,currentUserID,text);
            Toast.makeText(payActivity.this, getString(R.string.yourDebtNow2) + " " + CreditorName + " " + CreditorSurname + " " + getString(R.string.pariA) + " "  + total + getString(R.string.simbol), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(payActivity.this,MainActivity.class));
        }else{
            String text =  R.string.perfectCompensation + "\n" +
                    R.string.totalDebtSolved;
            sendNotification(currentName,currentSurname,total,currentUserID,text);
            deleteDebts(CreditorID,currentUserID,"0");
            deleteDebts(currentUserID,CreditorID,"0");
            deleteCredits(currentUserID,CreditorID,"0");
            deleteCredits(CreditorID,currentUserID,"0");

            Toast.makeText(payActivity.this,getString(R.string.perfectCompensation2), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(payActivity.this,MainActivity.class));
        }
    }

    private void sendNotification(String currentName, String currentSurname, long daPagare, String currentUserID, String text) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomeMittente",currentName);
        hashMap.put("cognomeMittente",currentSurname);
        hashMap.put("idMittente",currentUserID);
        hashMap.put("daPagare",""+daPagare);
        hashMap.put("letto","no");
        hashMap.put("testo", text);
        hashMap.put("date",currentDate);

        db.collection("users").document(CreditorID).collection("notify")
                .add(hashMap);
        startActivity(new Intent(payActivity.this,MainActivity.class));
    }

    private void updateCredit(String id_user, Long total, String name, String surname, String id_debtor) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nome debitore",name);
        hashMap.put("cognome debitore",surname);
        hashMap.put("idDebitore",id_debtor);
        hashMap.put("credito",""+total);
        hashMap.put("data",currentDate);

        db.collection("users").document(id_user).collection("credits").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String id = task.getResult().getId();
                deleteCredits(currentUserID,CreditorID,id);
                deleteCredits(CreditorID,currentUserID,id);
            }
        });

    }

    private void updateDebt(String id_user, Long total, String name, String surname, String id_creditor) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("cognome creditore",surname);
        hashMap.put("nome creditore",name);
        hashMap.put("debito",""+total);
        hashMap.put("idCreditore",id_creditor);
        hashMap.put("data",currentDate);

        db.collection("users").document(id_user).collection("debts")
                .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                String id = task.getResult().getId();
                deleteDebts(CreditorID,currentUserID,id);
                deleteDebts(currentUserID,CreditorID,id);
            }
        });

    }


    private void deleteDebts(final String id_user, final String id_compare, final String id_ref){
        db.collection("users").document(id_user).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idCreditore").equals(id_compare) && !documentSnapshot.getId().equals(id_ref)){
                        deleteD(documentSnapshot.getId(),id_user);
                    }
                }
            }
        });
    }

    private void deleteD(String id, String id_user) {
        db.collection("users").document(id_user).collection("debts")
                .document(id).delete();
    }

    private void deleteCredits(final String id_user, final String id_compare, final String id_ref){
        db.collection("users").document(id_user).collection("credits")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idDebitore").equals(id_compare) && !documentSnapshot.getId().equals(id_ref)){
                        deleteC(documentSnapshot.getId(), id_user);
                    }
                }
            }
        });
    }

    private void deleteC(String id, String id_user) {
        db.collection("users").document(id_user).collection("credits")
                .document(id).delete();
    }

    private void goNow(final Long creditorTotalC) {
        debt.setText(getString(R.string.debtTo) + " " + CreditorName + " " + CreditorSurname + " : " + CreditorCredit + getString(R.string.simbol));

        totalDebt.setText(getString(R.string.totalDebtTo) + " " + CreditorName + " " + CreditorSurname + " : " + creditorTotalC + getString(R.string.simbol));

        //l'importo da pagare dovrebbe corrispondere al debito che si seleziona a questo punto

        payTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(payTotal.isChecked()){
                    payBtn.setText(R.string.payTotalDebtBtn);
                    toPay = creditorTotalC;
                    total = true;
                }else{
                    payBtn.setText(R.string.payDebtBtn);
                    toPay = CreditorCredit;
                    total = false;
                }
            }
        });




    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }



}
package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
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
import java.text.NumberFormat;
import java.text.ParseException;
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
    Double CreditorCredit;
    Double toPay;
    boolean total = false;
    ProgressBar progressBar;

    ArrayList<Long> debts = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        final Intent intent = getIntent();
        CreditorName = intent.getStringExtra("name");
        CreditorSurname = intent.getStringExtra("surname");
        CreditorCredit = Double.valueOf(intent.getStringExtra("debt"));
        CreditorID = intent.getStringExtra("creditorID");
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        progressBar = findViewById(R.id.progressBar);
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
                progressBar.setVisibility(View.VISIBLE);
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
                    String toPayTot = String.format("%.2f", toPay);
                    intent1.putExtra("daPagareTot", toPayTot);
                    intent1.putExtra("Totale","si");
                    intent1.putExtra("daPagare",toPay);
                }else{
                    String toPayfinal = String.format("%.2f", CreditorCredit);
                    intent1.putExtra("daPagareTot", toPayfinal);
                    intent1.putExtra("Totale","no");
                    intent1.putExtra("daPagare",""+CreditorCredit);
                }
                startActivity(intent1);
            }
        });
    }

    private void loadTotalDebt() {
        final NumberFormat nf = NumberFormat.getInstance();
        db.collection("users").document(currentUserID).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Double CreditorTotalC = Double.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                   if(documentSnapshot.getString("idCreditore").equals(CreditorID)){
                       try {
                           CreditorTotalC = CreditorTotalC + nf.parse(documentSnapshot.getString("debito")).doubleValue();
                       } catch (ParseException e) {
                           e.printStackTrace();
                       }
                   }
                }

                goNow(CreditorTotalC);
            }
        });
    }

    private void loadTotalDebtTwo() {
        final NumberFormat nf = NumberFormat.getInstance();
        db.collection("users").document(currentUserID).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Double CreditorTotalC = Double.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idCreditore").equals(CreditorID)){
                        try {
                            CreditorTotalC = CreditorTotalC + nf.parse(documentSnapshot.getString("debito")).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                loadCreditorTotalDebt(CreditorTotalC);
            }
        });
    }

    private void loadCreditorTotalDebt(final Double CreditorTotalC) {
        final NumberFormat nf = NumberFormat.getInstance();
        db.collection("users").document(currentUserID).collection("credits")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Double CreditorTotalD = Double.valueOf(0);
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idDebitore").equals(CreditorID)){
                        try {
                            CreditorTotalD = CreditorTotalD + nf.parse(documentSnapshot.getString("credito")).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                final Double total = CreditorTotalC - CreditorTotalD;
                recoverCurrentUserName(total);


            }
        });


    }

    private void recoverCurrentUserName(final Double total) {
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

    private void updateDb(Double total, String currentName, String currentSurname) {

        if(total < 0){
            String finaltotal = String.format("%.2f",-total);
            //aggiorna con un unica tabella il mio credito e il suo debito
            updateDebt(CreditorID,(-total),currentName,currentSurname,currentUserID);
            updateCredit(currentUserID,(-total),CreditorName,CreditorSurname,CreditorID);
            String text = R.string.compensationFirst + "\n" +
                    R.string.yourDebtNow + " " + finaltotal + " " + R.string.simbol;
            sendNotification(currentName,currentSurname,(-total),currentUserID,text);
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(payActivity.this,getString(R.string.compensationSecond) + " " + CreditorName + " " + CreditorSurname + " " + getString(R.string.pariA) + " "  + finaltotal + " " + getString(R.string.simbol), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(payActivity.this,MainActivity.class));
        }else
        if(total > 0){
            String finaltotal = String.format("%.2f",total);
            //aggiorna con un unica tabella il mio di debito e il suo di credito
            updateDebt(currentUserID,total,CreditorName,CreditorSurname,CreditorID);
            updateCredit(CreditorID,total,currentName,currentSurname,currentUserID);
            String text =  R.string.compensationThird + "\n" +
                    R.string.myDebtNow + " " + finaltotal + " " + R.string.simbol;
            sendNotification(currentName,currentSurname,total,currentUserID,text);
            Toast.makeText(payActivity.this, getString(R.string.yourDebtNow2) + " " + CreditorName + " " + CreditorSurname + " " + getString(R.string.pariA) + " "  + finaltotal + getString(R.string.simbol), Toast.LENGTH_SHORT).show();
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

    private void sendNotification(String currentName, String currentSurname, Double daPagare, String currentUserID, String text) {
        String finalDaPagare = String.format("%.2f", daPagare);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomeMittente",currentName);
        hashMap.put("cognomeMittente",currentSurname);
        hashMap.put("idMittente",currentUserID);
        hashMap.put("daPagare",""+finalDaPagare);
        hashMap.put("letto","no");
        hashMap.put("testo", text);
        hashMap.put("date",currentDate);

        db.collection("users").document(CreditorID).collection("notify")
                .add(hashMap);
        startActivity(new Intent(payActivity.this,MainActivity.class));
    }

    private void updateCredit(String id_user, Double total, String name, String surname, String id_debtor) {
        String finalTotal = String.format("%.2f", total);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nome debitore",name);
        hashMap.put("cognome debitore",surname);
        hashMap.put("idDebitore",id_debtor);
        hashMap.put("credito",""+finalTotal);
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

    private void updateDebt(String id_user, Double total, String name, String surname, String id_creditor) {
        String finalTotal = String.format("%.2f", total);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("cognome creditore",surname);
        hashMap.put("nome creditore",name);
        hashMap.put("debito",""+finalTotal);
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

    private void goNow(final Double creditorTotalC) {
        String finalCreditorCredit = String.format("%.2f", CreditorCredit);
        debt.setText(getString(R.string.debtTo) + " " + CreditorName + " " + CreditorSurname + " : " + finalCreditorCredit + getString(R.string.simbol));
        String finalCreditorTotalC = String.format("%.2f", creditorTotalC);
        totalDebt.setText(getString(R.string.totalDebtTo) + " " + CreditorName + " " + CreditorSurname + " : " + finalCreditorTotalC + getString(R.string.simbol));

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
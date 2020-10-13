package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PosActivity extends AppCompatActivity {

    EditText card,pass;
    Button confirm;
    String creditorID,total,toPay;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID,currentDate;
    DatabaseHelper databaseHelper;
    RadioGroup radioGroup;
    RadioButton radioButton;
    TextView title;
    String cardType = "";
    String toPaytot = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        databaseHelper = new DatabaseHelper(this);
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        creditorID = intent.getStringExtra("idCreditore");
        total = intent.getStringExtra("Totale");
        toPay = intent.getStringExtra("daPagare");
        toPaytot = intent.getStringExtra("daPagareTot");

        title = findViewById(R.id.cardType);
        radioGroup = findViewById(R.id.radiogroup);
        card = findViewById(R.id.cardNum);
        pass = findViewById(R.id.passwordCard);
        confirm = findViewById(R.id.confirmBtn);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String number = card.getText().toString().trim();
                final String password = pass.getText().toString().trim();

                if (TextUtils.isEmpty(number)) {
                    card.setError(getString(R.string.insertCardNum));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    pass.setError(getString(R.string.insertPassword));
                    return;
                }

                if(TextUtils.isEmpty(cardType)){
                    Toast.makeText(PosActivity.this,getString(R.string.insertCardT), Toast.LENGTH_SHORT).show();
                }

                db.collection("users").document(creditorID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null)
                        {

                        }else{
                            String email = value.getString("e-mail");
                            if(databaseHelper.isCorrectCard(number, password,cardType)){
                                if(!databaseHelper.updateCreditinDB(toPaytot,number)){
                                    Toast.makeText(PosActivity.this, getString(R.string.youAreDown), Toast.LENGTH_SHORT).show();
                                }else {
                                    databaseHelper.updateCreditCreditorDB(toPaytot,email);
                                    deleteDebt();
                                    deleteCredit();
                                    sendNotification();
                                    updatePayment();
                                    Toast.makeText(PosActivity.this, getString(R.string.debtSolved), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(PosActivity.this,getString(R.string.errore), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });



               /* if (number.equals("123456") && password.equals("123456")) {
                    deleteDebt();
                    deleteCredit();
                    sendNotification();
                    //updatecurrentCard();
                    //updatecreditorCard();
                    updatePayment();
                    Toast.makeText(PosActivity.this, getString(R.string.debtSolved) ,Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PosActivity.this,getString(R.string.errore), Toast.LENGTH_SHORT).show();
                }*/
            }
        });


    }

    private void updatePayment() {
        recoverDataforPayment(creditorID);
    }

    private void recoverDataforPayment(String id) {
        db.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){

                }else{
                    writePayment(value.getString("nome"),value.getString("cognome"));
                }
            }
        });
    }

    private void writePayment(String nome, String cognome) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("idPagato",creditorID);
        hashMap.put("totalePagato",toPaytot);
        hashMap.put("nomePagato",nome);
        hashMap.put("cognomePagato",cognome);
        hashMap.put("data",currentDate);

        db.collection("users").document(currentUserID).collection("payment")
                .add(hashMap);
    }


    private void updatecreditorCard() {
    }


    private void updatecurrentCard() {
    }

    private void sendNotification() {
        recoverData(currentUserID);
    }

    private void recoverData(String id) {
        db.collection("users").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){

                }else{
                    send(value.getString("nome"),value.getString("cognome"));
                }
            }
        });
    }

    private void send(String nome, String cognome) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomeMittente",nome);
        hashMap.put("cognomeMittente",cognome);
        hashMap.put("idMittente",currentUserID);
        hashMap.put("daPagare",toPaytot);
        hashMap.put("letto","no");
        hashMap.put("data",currentDate);
        if(!toPaytot.equals(1)) {
            hashMap.put("testo", getString(R.string.yourDebtSolved) + " " + toPaytot + " " + getString(R.string.valute));
        }else{
            hashMap.put("testo", getString(R.string.yourDebtSolved) + " " + toPaytot + " " + getString(R.string.valuteS));
        }

        db.collection("users").document(creditorID).collection("notify").add(hashMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        startActivity(new Intent(PosActivity.this,MainActivity.class));
                    }
                });
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
                                documentSnapshot.getString("credito").equals(toPaytot)){
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
                                documentSnapshot.getString("debito").equals(toPaytot)){
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

            }
        });

    }

    public void checkButton(View v){
        int radioID = radioGroup.getCheckedRadioButtonId();

        radioButton = findViewById(radioID);

        CharSequence text = radioButton.getText();
        cardType = text.toString();
    }
}
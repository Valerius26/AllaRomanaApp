package com.example.allaromanaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

public class NotDetailActivity extends AppCompatActivity {
    
    String idNotification,currentUserID;
    Double debt;
    TextView userName,message,info,indicate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String debito,name,surname,nomeMittente,cognomeMittente;
    String sendID,testo,currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.not_detail_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        idNotification = intent.getStringExtra("idNotifica");
        NumberFormat nf = NumberFormat.getInstance();
        try {
            debt = nf.parse(intent.getStringExtra("debito")).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        markAsRead(currentUserID,idNotification);
        recoversData();

        userName = findViewById(R.id.userName);
        message = findViewById(R.id.message);
        info = findViewById(R.id.info);
        indicate = findViewById(R.id.indicate);

        final DocumentReference documentReference = db.collection("users").document(currentUserID)
                .collection("notify").document(idNotification);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                else {
                    sendID = value.getString("idMittente");
                    nomeMittente = value.getString("nomeMittente");
                    cognomeMittente = value.getString("cognomeMittente");
                    userName.setText(nomeMittente + " " + cognomeMittente);
                    debito = (value.getString("daPagare"));
                    testo = value.getString("testo");
                    message.setText(testo);
                    if(testo.contains(getString(R.string.IhaveRepYou))){
                        info.setText(R.string.deleteDebt);
                        indicate.setText(R.string.deleteDebtYes);
                    }
                    if(testo.contains(getString(R.string.thisIsMyPosi)) || testo.contains(getString(R.string.IpaidYou)) ||
                     testo.contains(getString(R.string.youAreStuck)) || testo.contains(getString(R.string.youNoLocked))
                    || testo.contains(getString(R.string.youWillSbD)) || testo.contains(getString(R.string.iHaveDelDeb))){
                        info.setVisibility(View.INVISIBLE);
                        indicate.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });


        indicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (testo.contains(getString(R.string.IhaveRepYou))) {
                    final AlertDialog.Builder indicateDialog = new AlertDialog.Builder(view.getContext());
                    indicateDialog.setTitle(getString(R.string.deleteDebtYes));
                    indicateDialog.setMessage(getString(R.string.areYouSure) + " " + nomeMittente + " " + cognomeMittente + "?");

                    indicateDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteDebt();
                            deleteCredit();
                            startActivity(new Intent(NotDetailActivity.this,notificationActivity.class));
                        }
                    });

                    indicateDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });


                    indicateDialog.create().show();

                } else {
                    final AlertDialog.Builder indicateDialog = new AlertDialog.Builder(view.getContext());
                    indicateDialog.setTitle(getString(R.string.report));
                    indicateDialog.setMessage(getString(R.string.areYouSure2) + " " + nomeMittente + " " + cognomeMittente + "?");

                    indicateDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("idUtente", sendID);


                            db.collection("reports").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    sendToCreditor();
                                }
                            });

                        }
                    });

                    indicateDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });


                    indicateDialog.create().show();

                }
            }
        });


        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotDetailActivity.this,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", sendID);
                startActivity(intent);
            }
        });

    }

    private void deleteCredit() {
        db.collection("users").document(currentUserID).collection("credits")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    if(documentSnapshot.getString("credito").equals(debito) && documentSnapshot.getString("idDebitore").equals(sendID)){
                        String id = documentSnapshot.getId();
                        delete(id,2);
                        break;
                    }
                }

                }else{
                    Toast.makeText(NotDetailActivity.this, R.string.debtDoesntExi, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void recoversData() {
        DocumentReference documentReference = db.collection("users").document(currentUserID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){

                }else{
                    name = value.getString("nome");
                    surname = value.getString("cognome");
                }
            }
        });
    }

    private void sendToCreditor() {
        NumberFormat nf = NumberFormat.getInstance();
        Double value = Double.valueOf(0);
        try {
            value = nf.parse(debito).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finaldebito = String.format("%.2f", value);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("idMittente",currentUserID);
        if(!debito.equals(1)) {
            hashMap.put("testo", getString(R.string.iHaveRepYou) + " " + finaldebito + " " + getString(R.string.valute) + ".\n" +
                    getString(R.string.IdontKnow));
        }else{
            hashMap.put("testo", getString(R.string.iHaveRepYou) + " " + finaldebito + " " + getString(R.string.valuteS) + ".\n" + getString(R.string.IdontKnow));
        }
        hashMap.put("nomeMittente",name);
        hashMap.put("cognomeMittente",surname);
        hashMap.put("letto","no");
        hashMap.put("daPagare", finaldebito);
        hashMap.put("data",currentDate);

        db.collection("users").document(sendID).collection("notify").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getApplicationContext(),getString(R.string.reportSent), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),notificationActivity.class));
            }
        });
    }

    private void markAsRead(String currentUserID, String idNotification) {
        DocumentReference documentReference = db.collection("users").document(currentUserID)
                .collection("notify").document(idNotification);
        documentReference.update("letto", "si");
    }

    private void sendToDebtor() {
        HashMap<String,String> hashMap = new HashMap<>();
        NumberFormat nf = NumberFormat.getInstance();
        Double value = Double.valueOf(0);
        try {
            value = nf.parse(debito).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String finalDebito = String.format("%.2f", value);
        hashMap.put("idMittente",currentUserID);
        if(!debito.equals(1)) {
            hashMap.put("testo", getString(R.string.iHaveDelDeb) + " " + finalDebito + " " + getString(R.string.valute));
        }else{
            hashMap.put("testo", getString(R.string.iHaveDelDeb) + " " + finalDebito + " " + getString(R.string.valuteS));
        }
        hashMap.put("nomeMittente",name);
        hashMap.put("cognomeMittente",surname);
        hashMap.put("letto","no");
        hashMap.put("daPagare",""+0);
        hashMap.put("data",currentDate);

        db.collection("users").document(sendID).collection("notify").add(hashMap);
    }

    private void deleteDebt(){
        db.collection("users").document(sendID).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int trovato = 0;
                if(!task.getResult().isEmpty()){
                 for(DocumentSnapshot documentSnapshot : task.getResult()){
                     if(documentSnapshot.getString("debito").equals(debito) && documentSnapshot.getString("idCreditore").equals(currentUserID)){
                         trovato = 1;
                         String id = documentSnapshot.getId();
                         delete(id,trovato);
                         break;
                     }
                 }
                }else{

                }
            }
        });
    }

    private void delete(String id, int trovato) {
        if(trovato == 1){
            db.collection("users").document(sendID).collection("debts")
                    .document(id).delete();
            sendToDebtor();
            Toast.makeText(NotDetailActivity.this, getString(R.string.debtDeleted), Toast.LENGTH_SHORT).show();
        }else
            if(trovato == 2){
                db.collection("users").document(currentUserID).collection("credits")
                        .document(id).delete();
            }
        else{
            Toast.makeText(NotDetailActivity.this, getString(R.string.debtDoesntExi), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NotDetailActivity.this, notificationActivity.class));
    }
}

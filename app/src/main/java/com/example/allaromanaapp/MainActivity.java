package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    Button profileBtn, balanceBtn, historyBtn, payBtn, messageBtn, settingsBtn, mapBtn, groupBtn,logout;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String currentUserID;
    String name,surname;
    CardView nuovaNotifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        currentUserID = firebaseAuth.getUid();
        account_delete(currentUserID);
        group_delete();

        profileBtn = (Button) findViewById(R.id.buttonProfile);
        balanceBtn = (Button) findViewById(R.id.buttonDebitoCredito);
        historyBtn = (Button) findViewById(R.id.storyBtn);
        payBtn = (Button)findViewById(R.id.buttonGroup);
        messageBtn = (Button) findViewById(R.id.buttonMessage);
        settingsBtn = (Button) findViewById(R.id.buttonImpostation);
        nuovaNotifica = (CardView) findViewById(R.id.notifyCounter);
        mapBtn = (Button) findViewById(R.id.mapBtn);
        groupBtn = (Button) findViewById(R.id.GroupBtn);
        logout = (Button) findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Disconnessione...", Toast.LENGTH_LONG).show();
                logout();
            }
        });

        nuovaNotifica.setVisibility(View.INVISIBLE);

        notifyNew(currentUserID);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("blocked").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int bloccato = 0;
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            if(currentUserID.equals(documentSnapshot.getString("idUtente"))){
                                Toast.makeText(MainActivity.this,"Sei stato bloccato!",Toast.LENGTH_SHORT).show();
                                bloccato = 1;
                                break;
                            }


                        }
                        if(bloccato==0){
                            createAccount(currentUserID);
                        }
                    }
                });

            }
        });

        messageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, notificationActivity.class));
            }
        });

        balanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, balanceActivity.class));
            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SettingActivity.class));
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("blocked").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    int bloccato = 0;
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            if(currentUserID.equals(documentSnapshot.getString("idUtente"))){
                                bloccato = 1;
                                Toast.makeText(MainActivity.this,"Sei stato bloccato!",Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if(bloccato==0){
                            startActivity(new Intent(MainActivity.this,groupActivity.class));
                        }
                    }
                });

            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,historyActivity.class));
            }
        });
    }

    private void group_delete() {
        db.collection("users").document(currentUserID).collection("groups")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    if(documentSnapshot.getString("Stato").equals("Non creato")){
                        String id = documentSnapshot.getId();
                        deleteNonCreated(id);
                    }
                }
            }
        });
    }

    private void deleteNonCreated(String id) {
        db.collection("users").document(currentUserID).collection("groups")
                .document(id).delete();
    }

    private void notifyNew(String currentUserID) {
        db.collection("users").document(currentUserID).collection("notify")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    for(DocumentSnapshot documentSnapshot: task.getResult()){
                        if(documentSnapshot.get("letto").equals("no")){
                            nuovaNotifica.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void account_delete(String currentUserID) {
        final CollectionReference collectionReference = db.collection("users").document(currentUserID)
                .collection("accounts");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        String id = documentSnapshot.getId();
                        deletePartecipants(collectionReference,id);
                        deleteAccount(collectionReference,id);
                    }
                }
            }
        });
    }

    private void deleteAccount(CollectionReference collectionReference, String id) {
        collectionReference.document(id).delete();
    }

    private void deletePartecipants(CollectionReference collectionReference, String id) {
        final CollectionReference collectionReference1 = collectionReference.document(id).collection("partecipants");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()) {
                    String id_partecipant = documentSnapshot.getId();
                    delete(collectionReference1, id_partecipant);
                }
            }
        });
    }

    private void delete(CollectionReference collectionReference1, String id_partecipant) {
        collectionReference1.document(id_partecipant).delete();
    }

    public void logout(){

        startActivity(new Intent(MainActivity.this,byeActivity.class));

        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,1000,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 1000 , pendingIntent);
        firebaseAuth.signOut(); //logout

        System.exit(0);




    }


    private void createAccount(final String creatorID) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Creatore",""+creatorID);
        db.collection("users").document(creatorID).collection("accounts").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String accountID = documentReference.getId();
                startFirstPartecipant(accountID,creatorID);
            }
        });
    }

    private void startFirstPartecipant(final String accountID, final String creatorID) {
        DocumentReference documentReference = db.collection("users").document(creatorID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.d("ErroreBanaleEStupido", ""+error.getMessage());
                }
                else {
                    name = value.getString("nome");
                    surname = value.getString("cognome");
                    createFirstPartecipant(accountID, creatorID, name, surname);
                }
            }
        });
    }


    private void createFirstPartecipant(final String accountID, final String creatorID, String nome, String cognome) {
        final HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomePartecipante",nome);
        hashMap.put("cognomePartecipante",cognome);
        hashMap.put("idUtente",creatorID);

        db.collection("users").document(creatorID).collection("accounts").document(accountID)
                .collection("partecipants").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Intent intent = new Intent(getApplicationContext(),AddUsers.class);
                intent.putExtra("idCreatore", creatorID);
                intent.putExtra("idAccount",accountID);
             //   getApplicationContext().startActivity(intent);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
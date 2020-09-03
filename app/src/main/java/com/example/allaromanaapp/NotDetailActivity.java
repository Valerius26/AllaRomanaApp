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

import java.util.HashMap;

public class NotDetailActivity extends AppCompatActivity {

    ImageButton back;
    String idNotification,currentUserID;
    Long debt;
    TextView userName,message,info,indicate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String debito,name,surname,nomeMittente,cognomeMittente;
    String sendID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.not_detail_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        idNotification = intent.getStringExtra("idNotifica");
        debt = Long.valueOf(intent.getStringExtra("debito"));


        markAsRead(currentUserID,idNotification);
        recoversData();

        userName = findViewById(R.id.userName);
        message = findViewById(R.id.message);
        info = findViewById(R.id.info);
        indicate = findViewById(R.id.indicate);
        back = findViewById(R.id.back);

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
                    String testo = value.getString("testo");
                    message.setText(testo);
                    if(testo.contains("Ti ho segnalato per un debito")){
                        info.setVisibility(View.INVISIBLE);
                        indicate.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });


        indicate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder indicateDialog = new AlertDialog.Builder(view.getContext());
                indicateDialog.setTitle("Segnalazione");
                indicateDialog.setMessage("Sei sicuro di voler segnalare " + nomeMittente +" "+cognomeMittente+"?");

                indicateDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HashMap<String,String> hashMap = new HashMap<>();
                        hashMap.put("idUtente",sendID);

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
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),notificationActivity.class));
            }
        });


        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", sendID);
                getApplicationContext().startActivity(intent);
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
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("idMittente",currentUserID);
        hashMap.put("testo","Ti ho segnalato per un debito di euro " + debito + ".\nNon so nulla di " +
                    "questo debito.");
        hashMap.put("nomeMittente",name);
        hashMap.put("cognomeMittente",surname);
        hashMap.put("letto","no");
        hashMap.put("daPagare",""+debito);

        db.collection("users").document(sendID).collection("notify").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(getApplicationContext(),"Segnalazione inviata", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),notificationActivity.class));
            }
        });
    }

    private void markAsRead(String currentUserID, String idNotification) {
        DocumentReference documentReference = db.collection("users").document(currentUserID)
                .collection("notify").document(idNotification);
        documentReference.update("letto", "si");
    }
}

package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class NewGroupActivity extends AppCompatActivity {

    EditText NomeGruppo, Descrizione;
    Button SubmitGruppoBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, nome, cognome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgroup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        NomeGruppo = findViewById(R.id.nomeGruppo);
        Descrizione = findViewById(R.id.descrizione);
        SubmitGruppoBtn = findViewById(R.id.submitGruppo);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        SubmitGruppoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreatingGroup();
            }
        });

        }

    private void startCreatingGroup() {

        String title = NomeGruppo.getText().toString().trim();
        String description = Descrizione.getText().toString().trim();

        if(TextUtils.isEmpty(title)){
            Toast.makeText(this, getString(R.string.NomeRichiesto), Toast.LENGTH_SHORT).show();
            return;
        }

        else{
            createGroup(title,description);
        }


    }

    private void createGroup(final String groupTitle, final String groupDescription) {

        userID = fAuth.getCurrentUser().getUid();


        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Nome gruppo", ""+groupTitle);
        hashMap.put("Descrizione", ""+groupDescription);
        hashMap.put("Creato da", ""+userID);
        hashMap.put("Stato","Non creato");

        //recupera nome e cognome del creatore
        DocumentReference documentReference1 = fStore.collection("users").document(userID);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nome = value.getString("nome");
                cognome = value.getString("cognome");
            }
        });


        //crea il gruppo nell'utente
        CollectionReference collectionReference = fStore.collection("users").document(userID).collection("groups");
        collectionReference.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String group_id = documentReference.getId();
                createGroupAll(userID,groupTitle,groupDescription,nome,cognome);
                createFirstPartecipant(documentReference, nome, cognome);
                Toast.makeText(NewGroupActivity.this, getString(R.string.GruppoCreato) , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),GroupDetail.class);
                intent.putExtra("idGruppo",group_id);
                intent.putExtra("NomeGruppo",groupTitle);
                intent.putExtra("DescrizioneGruppo",groupDescription);
                startActivity(intent);
                IncrementGroup();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });




    }

    private void createGroupAll(String userID, String groupTitle, String groupDescription, final String nome, final String cognome) {
        userID = fAuth.getCurrentUser().getUid();


        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Nome gruppo", ""+groupTitle);
        hashMap.put("Descrizione", ""+groupDescription);
        hashMap.put("Creato da", ""+userID);
        hashMap.put("Stato","Non creato");

        //crea il gruppo nell'utente
        CollectionReference collectionReference = fStore.collection("AllGroups");
        collectionReference.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                createFirstPartecipantInAllGroups(documentReference, nome, cognome);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void createFirstPartecipantInAllGroups(DocumentReference documentReference, String nome, String cognome) {
        String groupID = documentReference.getId();



        HashMap<String,String> hashMap1 = new HashMap<>();
        hashMap1.put("Ruolo", "creatore");
        hashMap1.put("idUtente", ""+userID);
        hashMap1.put("idGruppo", ""+groupID);
        hashMap1.put("nomePartecipante",""+nome);
        hashMap1.put("cognomePartecipante",""+cognome);


        CollectionReference collectionReference1 = fStore.collection("AllGroups").document(groupID).collection("partecipants");
        collectionReference1.add(hashMap1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void IncrementGroup() {

        DocumentReference dr = fStore.collection("users").document(userID);

        dr.update("gruppi", FieldValue.increment(1));

    }

    private void createFirstPartecipant(DocumentReference documentReference, String nome, String cognome) {




        String groupID = documentReference.getId();



        HashMap<String,String> hashMap1 = new HashMap<>();
        hashMap1.put("Ruolo", "creatore");
        hashMap1.put("idUtente", ""+userID);
        hashMap1.put("idGruppo", ""+groupID);
        hashMap1.put("nomePartecipante",""+nome);
        hashMap1.put("cognomePartecipante",""+cognome);


        CollectionReference collectionReference1 = fStore.collection("users").document(userID).collection("groups").document(groupID).collection("partecipants");
        collectionReference1.add(hashMap1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }



}

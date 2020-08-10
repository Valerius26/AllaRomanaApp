package com.example.allaromanaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class NewGroupActivity extends AppCompatActivity {

    EditText NomeGruppo, Descrizione;
    Button SubmitGruppoBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newgroup);

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

    private void createGroup(String groupTitle, String groupDescription) {

        userID = fAuth.getCurrentUser().getUid();

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Nome gruppo", ""+groupTitle);
        hashMap.put("Descrizione", ""+groupDescription);
        hashMap.put("Creato da", ""+userID);

        //crea il gruppo
        CollectionReference collectionReference = fStore.collection("groups");
        collectionReference.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(NewGroupActivity.this, getString(R.string.GruppoCreato) , Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), GroupActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }


}

package com.example.allaromanaapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class NotCurrentProfileActivity extends AppCompatActivity {

    TextView nome, cognome, email, bilancio, gruppi;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    Button deleteProfile;
    int bilancio2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nome = findViewById(R.id.nomeText);
        cognome = findViewById(R.id.cognomeText);
        email = findViewById(R.id.emailText);
        bilancio = findViewById(R.id.balanceText);
        gruppi = findViewById(R.id.groupText);
        deleteProfile = findViewById(R.id.deleteProfile);

        deleteProfile.setVisibility(View.INVISIBLE);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
        userID = intent.getStringExtra("idUtente");

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }
                else {
                    nome.setText(value.getString("nome"));
                    cognome.setText(value.getString("cognome"));
                    email.setText(value.getString("e-mail"));
                    bilancio2 = Math.toIntExact((Long) value.get("bilancio"));
                    bilancio.setText(String.valueOf(bilancio2));
                }
            }
        });


    }


}

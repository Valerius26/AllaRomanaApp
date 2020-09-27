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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

    TextView nome, cognome, email, bilancio,balanceTextTitle;
    ImageView profileImage,addFoto;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    Button deleteProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        nome = findViewById(R.id.nomeText);
        cognome = findViewById(R.id.cognomeText);
        email = findViewById(R.id.emailText);
        deleteProfile = findViewById(R.id.deleteProfile);
        profileImage = findViewById(R.id.profileImage);
        addFoto = findViewById(R.id.addFoto);

        addFoto.setVisibility(View.INVISIBLE);

        balanceTextTitle.setVisibility(View.INVISIBLE);
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
                    bilancio.setVisibility(View.INVISIBLE);
                    Glide.with(NotCurrentProfileActivity.this).load(value.getString("immagine")).circleCrop().into(profileImage);
                }
            }
        });


    }


}

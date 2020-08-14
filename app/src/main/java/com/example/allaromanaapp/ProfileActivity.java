package com.example.allaromanaapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileActivity extends AppCompatActivity {

    TextView nome, cognome, email, bilancio, gruppi;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    Button deleteProfile;
    int bilancio2, gruppi2;


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

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nome.setText(value.getString("nome"));
                cognome.setText(value.getString("cognome"));
                email.setText(value.getString("e-mail"));
                bilancio2 = Math.toIntExact((Long) value.get("bilancio"));
                bilancio.setText(String.valueOf(bilancio2));
                gruppi2 = Math.toIntExact((Long) value.get("gruppi"));
                gruppi.setText(String.valueOf(gruppi2));

            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    delete(bilancio2,gruppi2,view);
                }
            }
        });
    }

    private void delete(int bilancio2, int gruppi2, View view) {
        if(bilancio2 != 0 || gruppi2 != 0){
            Toast.makeText(ProfileActivity.this, "non puoi cancellare un profilo quando partecipi ad un gruppo o hai un bilancio diverso da zero", Toast.LENGTH_SHORT).show();
        }
        else{
            final AlertDialog.Builder cancelDialog = new AlertDialog.Builder(view.getContext());
            cancelDialog.setTitle("Cancellare il profilo?");
        }
    }

}

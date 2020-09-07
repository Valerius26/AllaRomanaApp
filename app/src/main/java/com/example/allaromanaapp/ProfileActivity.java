package com.example.allaromanaapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    TextView nome, cognome, email, bilancio, gruppi;
    ImageView profilePicture;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String userID;
    Button deleteProfile;
    int bilancio2, gruppi2;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        nome = findViewById(R.id.nomeText);
        cognome = findViewById(R.id.cognomeText);
        email = findViewById(R.id.emailText);
        bilancio = findViewById(R.id.balanceText);
        gruppi = findViewById(R.id.groupText);
        deleteProfile = findViewById(R.id.deleteProfile);
        profilePicture = findViewById(R.id.profileImage);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        userID = fAuth.getCurrentUser().getUid();

        updateBalance();

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
                    Glide.with(ProfileActivity.this).load(value.getString("immagine")).circleCrop().into(profilePicture);
                }
            }
        });

        deleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null)
                {
                    canc(bilancio2,gruppi2,view,user);
                }
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "seleziona l'immagine"), PICK_IMAGE);
            }
        });
    }

    private void updateBalance() {
        fStore.collection("users").document(userID).collection("debts").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Integer debt = 0;
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            debt = debt + Integer.valueOf(documentSnapshot.getString("debito"));
                        }
                        getCredits(debt);
                    }

                });
    }

    private void getCredits(final Integer debt) {
        fStore.collection("users").document(userID).collection("credits").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Integer credit = 0;
                        for(DocumentSnapshot documentSnapshot: task.getResult()){
                            credit = credit + Integer.valueOf(documentSnapshot.getString("credito"));
                        }
                        update(debt,credit);
                    }

                });
    }

    private void update(Integer debt, Integer credit) {
        Integer balance = credit - debt;
        String balanceString = ""+balance;
        fStore.collection("users").document(userID).update("bilancio",balance);
    }

    private void canc(int bilancio2, int gruppi2, View view, final FirebaseUser user) {
        if(bilancio2 != 0 || gruppi2 != 0){
            Toast.makeText(ProfileActivity.this, "non puoi cancellare un profilo quando partecipi ad un gruppo o hai un bilancio diverso da zero", Toast.LENGTH_SHORT).show();
        }
        else{
            final AlertDialog.Builder cancelDialog = new AlertDialog.Builder(view.getContext());
            cancelDialog.setTitle("Cancellare il profilo?");
            cancelDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"Profilo cancellato",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"Non è stato possibie eliminare il profilo",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            cancelDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            cancelDialog.create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
        }
        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
            profilePicture.setImageBitmap(bitmap);
        }catch(Exception e){
            e.printStackTrace();
        }

        loadImageToFirebase();
    }

    private void loadImageToFirebase() {
        final String image = imageUri != null ? imageUri.toString() : null;
        fStore.collection("users").document(userID).update("immagine", image);
        restartActivity();
        }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}




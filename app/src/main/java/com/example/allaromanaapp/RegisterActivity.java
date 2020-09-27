package com.example.allaromanaapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class RegisterActivity extends AppCompatActivity {

    EditText Nome, Cognome, Email, Password;
    Button RegisterBtn, ChangeBtn;
    TextView LoginBtn;
    FirebaseAuth fAuth;
    ArrayList<String> cardType = new ArrayList<>();
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    Long bilancio = Long.valueOf(0);
    DatabaseHelper cardDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        // Initialize Firebase Auth

        cardDB = new DatabaseHelper(this);
        Nome = findViewById(R.id.nome);
        Cognome = findViewById(R.id.cognome);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        RegisterBtn = findViewById(R.id.registerbtn);
        LoginBtn = findViewById(R.id.loginbtn);
        ChangeBtn = findViewById(R.id.changeLan);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);

        cardType.add("VISA");
        cardType.add("MASTERCARD");
        cardType.add("POSTPAY");
        cardType.add("PAYPAL");
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String nome = Nome.getText().toString().trim();
                final String cognome = Cognome.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    Email.setError(getString(R.string.emailRichiesta));
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Password.setError(getString(R.string.password123));
                    return;
                }

                if(password.length() < 6){
                    Password.setError(getString(R.string.passwordCorta));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //Register card in Sql
                Random rnd = new Random();
                int number = rnd.nextInt(999999);
                String card = String.format("%06d", number);
                int n = (int) (Math.random() * 4);
                int credit = (int) (Math.random() * 1000);
                Long c = Long.valueOf(credit);
                String credito = c.toString();
                boolean insert = cardDB.insertData(card, password,cardType.get(n),credito);
                if(insert)
                    Toast.makeText(getApplicationContext(),"okok",Toast.LENGTH_SHORT).show();

                //Register the user in firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(RegisterActivity.this, R.string.utenteRegistrato, Toast.LENGTH_SHORT).show();
                           userID = fAuth.getCurrentUser().getUid();
                           DocumentReference documentReference = fStore.collection("users").document(userID);
                           Map<String,Object> user = new HashMap<>();
                           user.put("nome", nome);
                           user.put("cognome",cognome);
                           user.put("e-mail",email);
                           user.put("password",password);
                           user.put("bilancio",bilancio);
                           documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   Log.d("TAG", "successo, il profilo è stato creato correttamente per " + userID);
                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   Log.d("TAG", "errore nella creazione dell'utente" );
                               }
                           });
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       }
                       else{
                           Toast.makeText(RegisterActivity.this, R.string.errore, Toast.LENGTH_SHORT).show();
                           progressBar.setVisibility(View.GONE);
                       }
                    }
                });
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        ChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mostra la lista delle lingue tra le quali scegliere
                showChangeLanguageDialog();
            }
        });
    }

    private void showChangeLanguageDialog() {
        final String[] listItems = {getString(R.string.inglese), getString(R.string.italiano)};
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(RegisterActivity.this);
        mBuilder.setTitle(R.string.sceltaLingua);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                 if(i == 0){
                     // Inglese
                     setLocale("en");
                     recreate();
                 }
                 else
                     if(i == 1){
                         //Italiano
                         setLocale("it");
                         recreate();
                     }

                 // Chiude la finestra di dialogo quando la lingua viene selezionata
                 dialogInterface.dismiss();


            }
        });

        AlertDialog mDialog = mBuilder.create();
        //Mostra dialogo di avviso
        mDialog.show();
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        // Salva i dati nelle preferenze condivise
        SharedPreferences.Editor editor = getSharedPreferences("Impostazioni", MODE_PRIVATE).edit();
        editor.putString("La mia lingua", lang);
        editor.apply();

    }

    // Carica la lingua salvata nella preferenza condivisa

    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Impostazioni", Activity.MODE_PRIVATE);
        String language = prefs.getString("La mia lingua", "");
        setLocale(language);
    }
}
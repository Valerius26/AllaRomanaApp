package com.example.allaromanaapp;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    EditText Nome, Cognome, Email, Password;
    Button RegisterBtn;
    TextView LoginBtn;
    FirebaseAuth fAuth;

    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    Double bilancio = 0.00;
    String balance ="BILANCIO\n" + bilancio + " $";
    Integer gruppi = 0;
    String group = "GRUPPI\n" + gruppi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Initialize Firebase Auth

        Nome = findViewById(R.id.nome);
        Cognome = findViewById(R.id.cognome);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        RegisterBtn = findViewById(R.id.registerbtn);
        LoginBtn = findViewById(R.id.loginbtn);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);

        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String email = Email.getText().toString().trim();
                final String password = Password.getText().toString().trim();
                final String nome = Nome.getText().toString().trim();
                final String cognome = Cognome.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                    Email.setError("L'email è richiesta");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Password.setError("L'email è richiesta");
                    return;
                }

                if(password.length() < 6){
                    Password.setError("La password è troppo corta");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);


                //Register the user in firebase

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Toast.makeText(RegisterActivity.this, "Utente registrato", Toast.LENGTH_SHORT).show();
                           userID = fAuth.getCurrentUser().getUid();
                           DocumentReference documentReference = fStore.collection("users").document(userID);
                           Map<String,Object> user = new HashMap<>();
                           user.put("nome", nome);
                           user.put("cognome",cognome);
                           user.put("e-mail",email);
                           user.put("password",password);
                           user.put("bilancio",balance);
                           user.put("gruppi",group);
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
                           Toast.makeText(RegisterActivity.this, "Errore !", Toast.LENGTH_SHORT).show();
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
    }
}
package com.example.allaromanaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    Button profileBtn, balanceBtn, historyBtn, payBtn, messageBtn, settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        profileBtn = (Button) findViewById(R.id.buttonProfile);
        balanceBtn = (Button) findViewById(R.id.buttonDebitoCredito);
        historyBtn = (Button) findViewById(R.id.storyBtn);
        payBtn = (Button)findViewById(R.id.buttonGroup);
        messageBtn = (Button) findViewById(R.id.buttonMessage);
        settingsBtn = (Button) findViewById(R.id.buttonImpostation);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddUsers.class));
            }
        });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut(); //logout
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

}
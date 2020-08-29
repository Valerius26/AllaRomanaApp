package com.example.allaromanaapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


public class balanceActivity extends AppCompatActivity {

    ImageButton back;
    TextView title;
    Button otherPage;
    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        back = (ImageButton) findViewById(R.id.back);
        title = findViewById(R.id.debtTitle);
        otherPage = findViewById(R.id.goCredit);
        recyclerView = findViewById(R.id.recycler4);



        otherPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),balCreditActivity.class));
            }
        });



    }

    public void home(){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }
}

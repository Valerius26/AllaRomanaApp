package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class balCreditActivity extends AppCompatActivity {
    ImageButton back;
    TextView title;
    Button otherPage;
    RecyclerView recyclerView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balcredit_activity);

        back = findViewById(R.id.back2);
        title = findViewById(R.id.creditTitle);
        otherPage = findViewById(R.id.goDebt);
        recyclerView = findViewById(R.id.recycler5);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        otherPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),balanceActivity.class));
            }
        });
    }
}

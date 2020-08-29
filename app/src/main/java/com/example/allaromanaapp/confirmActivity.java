package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class confirmActivity extends AppCompatActivity {

    TextView title,info;
    Button confirm;
    String creatorID,accountID;
    ArrayList<String> debtors = new ArrayList<>();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        db = FirebaseFirestore.getInstance();

        title = findViewById(R.id.titleInfo);
        info = findViewById(R.id.info);
        confirm = findViewById(R.id.confirmBtn);


    }

}

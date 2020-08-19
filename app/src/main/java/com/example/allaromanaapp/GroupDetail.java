package com.example.allaromanaapp;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


public class GroupDetail extends AppCompatActivity {

    private String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupdetail_activity);

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupId");

        

    }
}
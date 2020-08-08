package com.example.allaromanaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupActivity extends AppCompatActivity {

    FloatingActionButton CreateGroupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        CreateGroupBtn = (FloatingActionButton) findViewById(R.id.createGroupBtn);

        CreateGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewGroupActivity.class));
            }
        });

    }

}

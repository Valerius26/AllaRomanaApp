package com.example.allaromanaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SpashScreenActivity extends AppCompatActivity {

    ImageView logo;
    private static int splashTimeout = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalshscreen);

        logo = (ImageView) findViewById(R.id.logo);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.mysplashanimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SpashScreenActivity.this,LoginActivity.class));
                finish();
            }
        },splashTimeout);


    }
}

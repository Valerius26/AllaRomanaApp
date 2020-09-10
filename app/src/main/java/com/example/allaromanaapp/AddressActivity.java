package com.example.allaromanaapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressActivity extends AppCompatActivity {

    TextView textView;
    Geocoder geocoder;
    List<Address> addresses;
    Button sendPosBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Intent intent = getIntent();
        Double latitude = Double.valueOf(intent.getStringExtra("latitude"));
        Double longitude = Double.valueOf(intent.getStringExtra("longitude"));

        sendPosBtn = findViewById(R.id.positionBtn);
        textView = findViewById(R.id.addressText);

        geocoder = new Geocoder(this, Locale.getDefault());

        try{
            addresses = geocoder.getFromLocation(latitude,longitude,1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAdminArea();

            String fullAddress = address + ", " + city;
            textView.setText(fullAddress);

        }catch (IOException e){
            e.printStackTrace();
        }


        sendPosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddressActivity.this, SelectUser.class));
            }
        });
    }
}
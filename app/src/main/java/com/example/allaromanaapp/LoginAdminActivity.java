package com.example.allaromanaapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginAdminActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan,goAdmin;
    private TextView textViewName;
    private EditText stringAdmin;


    //qr code scanner object
    private IntentIntegrator qrScan;

    String admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_admin_acctivity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewName = (TextView) findViewById(R.id.TextViewName);
        stringAdmin = (EditText) findViewById(R.id.stringInput);
        goAdmin = (Button) findViewById(R.id.goAdmin);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

        goAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringa = stringAdmin.getText().toString().trim();

                if(TextUtils.isEmpty(stringa)){
                    stringAdmin.setError("Inserire la stringa dopo aver scansionato il qr-code");
                    return;
                }

                if(stringa.equals(admin)){
                    startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                }else{
                    stringAdmin.setError("Non sei l'amministratore!");
                }
            }
        });
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    textViewName.setText(result.getContents());
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    admin = textViewName.getText().toString().trim();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onClick(View view) {
        //initiating the qr code scan
        qrScan.initiateScan();
    }
}
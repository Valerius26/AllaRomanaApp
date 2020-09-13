package com.example.allaromanaapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class PosActivity extends AppCompatActivity {

    NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if(nfcAdapter==null){
            Toast.makeText(PosActivity.this,"NFC non supported",Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(PosActivity.this,"supported",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();

        disableForegroundDispatchSystem();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            Toast.makeText(this, "NfcIntent", Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage("My String content");

            writeNdefMessage(tag, ndefMessage);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void disableForegroundDispatchSystem(){

        nfcAdapter.disableForegroundDispatch(this);
        Intent intent = new Intent(PosActivity.this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(PosActivity.this,0,intent,0);
        IntentFilter[] intentFilters = new IntentFilter[] {};
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,intentFilters,null);
    }

    private void enableForegroundDispatchSystem() {
        try {
            Intent intent = new Intent(PosActivity.this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
            PendingIntent pendingIntent = PendingIntent.getActivity(PosActivity.this, 0, intent, 0);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
        }catch (Exception e){
            Toast.makeText(PosActivity.this,e.toString(),Toast.LENGTH_LONG).show();

        }
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {

        try {

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_SHORT).show();
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(PosActivity.this, "Tag is writable", Toast.LENGTH_SHORT).show();

        }catch (IOException | FormatException e) {
            e.printStackTrace();
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage){

        try{

            if(tag == null){
                Toast.makeText(this, "Tag object cannot be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                formatTag(tag, ndefMessage);
            }else{
                ndef.connect();

                if(!ndef.isWritable()){
                    Toast.makeText(PosActivity.this, "Tag is not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
            }

        } catch (IOException | FormatException e) {
            e.printStackTrace();
        }

    }

    private NdefMessage createNdefMessage(String content){
        NdefRecord ndefRecord = NdefRecord.createTextRecord("en",content);

        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});

        return ndefMessage;
    }
}

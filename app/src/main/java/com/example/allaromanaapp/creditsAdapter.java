package com.example.allaromanaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class creditsAdapter extends RecyclerView.Adapter<creditsHolder>{

    ArrayList<Creditors> creditsList;
    Context context;
    balCreditActivity credAct;
    String Debtorname,Debtorsurname,id_dest,currentUserID;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    Double credit;
    String date;

    public creditsAdapter(ArrayList<Creditors> creditsList, Context context, balCreditActivity credAct) {
        this.creditsList = creditsList;
        this.context = context;
        this.credAct = credAct;
        Calendar calendar = Calendar.getInstance();
        date = DateFormat.getDateInstance().format(calendar.getTime());
    }


    @NonNull
    @Override
    public creditsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.balcreditlist,parent,false);

        creditsHolder cdHolder = new creditsHolder(itemView);

        cdHolder.setOnClickListener(new creditsHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                final AlertDialog.Builder notificationDialog = new AlertDialog.Builder(view.getContext());
                notificationDialog.setTitle(context.getString(R.string.sendNotTitle));
                notificationDialog.setMessage(context.getString(R.string.sendNotBody) + " " + Debtorname +" "+Debtorsurname+"?");

                notificationDialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendNotification(id_dest);
                    }
                });

                notificationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                notificationDialog.create().show();
            }

        });



        return cdHolder;
    }

    private void sendNotification(String id_dest) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserID = firebaseAuth.getUid();
        recoversData(currentUserID);

    }

    private void recoversData(String currentUserID) {
        DocumentReference documentReference = db.collection("users").document(currentUserID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {

                } else {
                    String currentUserName = value.getString("nome");
                    String currentUserSurname = value.getString("cognome");
                    send(currentUserName,currentUserSurname);
                }
            }
        });
    }

    private void send(String currentUserName, String currentUserSurname) {
        String finalcredit = String.format("%.2f", credit);
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("idMittente",currentUserID);
        if(credit != 1){
            hashMap.put("testo",context.getString(R.string.payMeDebt) + " " + finalcredit + " " + context.getString(R.string.valute));
        }else{
            hashMap.put("testo",context.getString(R.string.payMeDebt) + " " + finalcredit + " " + context.getString(R.string.valuteS));
        }

        hashMap.put("nomeMittente",currentUserName);
        hashMap.put("cognomeMittente",currentUserSurname);
        hashMap.put("letto","no");
        hashMap.put("daPagare", finalcredit);
        hashMap.put("data",date);


        db.collection("users").document(id_dest).collection("notify")
                .add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(context,R.string.notificationSended, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Non è stato possibile inviare la notifica", Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onBindViewHolder(@NonNull creditsHolder holder, int position) {
        Debtorname = creditsList.get(position).getName();
        Debtorsurname = creditsList.get(position).getSurname();
        id_dest = creditsList.get(position).getId();
        credit = creditsList.get(position).getDebt();
        holder.userName.setText(Debtorname + " " + Debtorsurname);
        holder.credit.setText(context.getString(R.string.creditString));
        holder.creditNum.setText(credit + " " + context.getString(R.string.simbol));
        holder.info.setText(context.getString(R.string.sendIfPress));
        holder.data.setText(creditsList.get(position).getData());
    }

    @Override
    public int getItemCount() {
        return creditsList.size();
    }
}

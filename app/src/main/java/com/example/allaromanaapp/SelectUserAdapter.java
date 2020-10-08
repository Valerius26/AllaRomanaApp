package com.example.allaromanaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserHolder> {

    List<User> usersList;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String address;
    String currentDate;

    public SelectUserAdapter(List<User> usersList,Context context, String address) {
        this.usersList = usersList;
        this.context = context;
        this.address = address;
        Calendar calendar = Calendar.getInstance();
        currentDate = DateFormat.getDateInstance().format(calendar.getTime());
    }


    @NonNull
    @Override
    public SelectUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist,parent,false);

        SelectUserHolder selectUserHolder = new SelectUserHolder(itemView);

        selectUserHolder.setOnClickListener(new SelectUserHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                String clickedUserID = usersList.get(position).getIdUser();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                final String id = usersList.get(position).getIdUser();
                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder confirm = new AlertDialog.Builder(view.getContext());
                confirm.setTitle(context.getString(R.string.posSendTitle));
                confirm.setMessage(context.getString(R.string.doYouWantSePo) + " " + usersList.get(position).getNome() + " " + usersList.get(position).getCognome()+"?");
                confirm.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // extract the email and send reset link
                        firebaseAuth = FirebaseAuth.getInstance();
                        String currentUserID = firebaseAuth.getUid();
                        recoverSender(currentUserID,id);

                    }
                });

                confirm.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       //close the dialog

                    }
                });

                confirm.create().show();

            }
        });

        return selectUserHolder;
    }

    private void recoverSender(final String currentUserID, final String id) {
        db.collection("users").document(currentUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){

                }else{
                    String name = value.getString("nome");
                    String surname = value.getString("cognome");
                    sendNotification(name,surname,currentUserID,id,address);
                }
            }
        });
    }

    private void sendNotification(String name, String surname, String currentUserID, String id, String address) {
        int da_pagare = 0;

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomeMittente",name);
        hashMap.put("cognomeMittente",surname);
        hashMap.put("idMittente",currentUserID);
        hashMap.put("daPagare",""+da_pagare);
        hashMap.put("letto","no");
        hashMap.put("testo",context.getString(R.string.thisIsMyPosi) +
                "\n" + address);
        hashMap.put("data",currentDate);

        db.collection("users").document(id).collection("notify").add(hashMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        Toast.makeText(context,context.getString(R.string.posSent),Toast.LENGTH_SHORT).show();
                        context.startActivity(new Intent(context,MainActivity.class));
                    }
                });
    }

    @Override
    public void onBindViewHolder(@NonNull SelectUserHolder holder, int position) {
        String name = usersList.get(position).getNome();
        String surname = usersList.get(position).getCognome();
        holder.fullName.setText(name + " " + surname);
        holder.info.setText(context.getString(R.string.selectIfPress));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

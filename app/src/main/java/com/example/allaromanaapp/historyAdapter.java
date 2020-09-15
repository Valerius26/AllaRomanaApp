package com.example.allaromanaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class historyAdapter extends RecyclerView.Adapter<historyHolder> {

    ArrayList<Pyments> historyList;
    Context context;
    historyActivity hisAct;
    FirebaseFirestore db;
    String userID;
    FirebaseAuth firebaseAuth;
    public historyAdapter(ArrayList historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
        this.hisAct = hisAct;
    }


    @NonNull
    @Override
    public historyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.paymentlist,parent,false);

        historyHolder hiHolder = new historyHolder(itemView);

        hiHolder.setOnClickListener(new historyHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedUserID = historyList.get(position).getId();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);

            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final AlertDialog.Builder notificationDialog = new AlertDialog.Builder(view.getContext());
                notificationDialog.setTitle("Elimina pagamento");
                notificationDialog.setMessage("Sei sicuro di voler eliminare questo pagamento?");
                notificationDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteP(historyList.get(position).getId_ref());
                        historyList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, historyList.size());
                    }
                });

                notificationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                notificationDialog.create().show();
            }
        });

        return hiHolder;

    }

    private void deleteP(String id_ref) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();

        db.collection("users").document(userID).collection("payment")
                .document(id_ref).delete();


    }


    @Override
    public void onBindViewHolder(@NonNull historyHolder holder, int position) {
        String name = historyList.get(position).getName();
        String surname = historyList.get(position).getSurname();
        String payment = historyList.get(position).getDebt().toString();
        holder.userName.setText(name + " " + surname);
        holder.sold.setText(payment + " $");

    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }



}

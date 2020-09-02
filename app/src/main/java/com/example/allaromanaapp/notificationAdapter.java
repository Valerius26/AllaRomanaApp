package com.example.allaromanaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationHolde> {

    ArrayList<notify> notificationList;
    Context context;
    notificationActivity notAct;

    public notificationAdapter(ArrayList notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
        this.notAct = notAct;
    }


    @NonNull
    @Override
    public notificationHolde onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notificationlist,parent,false);

        notificationHolde ntHolder = new notificationHolde(itemView);

        ntHolder.setOnClickListener(new creditsHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String notID = notificationList.get(position).getId();
                Long daPagare = notificationList.get(position).getDebito();
                Intent intent = new Intent(context,NotDetailActivity.class);
                intent.putExtra("idNotifica", notID);
                intent.putExtra("debito",""+daPagare);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        return ntHolder;

    }


    @Override
    public void onBindViewHolder(@NonNull notificationHolde holder, int position) {
        String name = notificationList.get(position).getNome();
        String surname = notificationList.get(position).getCognome();
        String letto = notificationList.get(position).getLetto();
        holder.userName.setText(name + " " + surname);
        if(letto.equals("si"))
            holder.iconMsg.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}

package com.example.allaromanaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class notificationAdapter extends RecyclerView.Adapter<notificationHolde> {

    ArrayList<notify> notificationList;
    Context context;
    notificationActivity notAct;

    public notificationAdapter(ArrayList notificationList, Context context, notificationActivity notAct) {
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
        holder.userName.setText(name + " " + surname);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}

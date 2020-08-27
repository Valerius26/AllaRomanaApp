package com.example.allaromanaapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewAdapterSelectingPay extends RecyclerView.Adapter<ViewHolderAddUsers>{

    SelectPaying selectPaying;
    Context context;
    ArrayList<partecipant> partecipants;

    public ViewAdapterSelectingPay(SelectPaying selectPaying, Context context, ArrayList partecipants) {
        this.selectPaying = selectPaying;
        this.context = context;
        this.partecipants = partecipants;
    }


    @NonNull
    @Override
    public ViewHolderAddUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partecipantslist,parent,false);

        ViewHolderAddUsers viewHolderAddUsers = new ViewHolderAddUsers(itemView);
        viewHolderAddUsers.setOnClickListener(new ViewHolderAddUsers.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        return viewHolderAddUsers;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAddUsers holder, int position) {
        String name = partecipants.get(position).getNomeP();
        String surname = partecipants.get(position).getCognomeP();
        Log.d("namesurname",name + " " + surname );
        holder.fullName.setText(name + " " + surname);
    }

    @Override
    public int getItemCount() {
        return partecipants.size();
    }
}

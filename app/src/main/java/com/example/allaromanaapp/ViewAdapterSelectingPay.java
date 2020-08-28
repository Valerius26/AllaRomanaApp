package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewAdapterSelectingPay extends RecyclerView.Adapter<ViewHolderAddUsers>{

    SelectPaying selectPaying;
    Context context;
    ArrayList<partecipant> partecipants;
    String payingUser;

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
                String clickedUserID = partecipants.get(position).getIdPartecipante();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                payingUser = partecipants.get(position).getIdPartecipante();
                String name = partecipants.get(position).getNomeP();
                String surname = partecipants.get(position).getCognomeP();
                Toast.makeText(context,name + " " + surname + "sarà il pagante", Toast.LENGTH_SHORT).show();
            }
        });



        return viewHolderAddUsers;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAddUsers holder, int position) {
        String name = partecipants.get(position).getNomeP();
        String surname = partecipants.get(position).getCognomeP();
        holder.fullName.setText(name + " " + surname);


    }

    @Override
    public int getItemCount() {
        return partecipants.size();
    }

    public String getPayingUser(){
        return payingUser;
    }

    public ArrayList<String> getDebtors() {
        ArrayList<String> debtors = new ArrayList<>();
        for(int position = 0; position < partecipants.size(); position++){
            String deptor = partecipants.get(position).getIdUtente();
            if(deptor!=payingUser){
                debtors.add(deptor);
            }
        }
        return debtors;
    }
}

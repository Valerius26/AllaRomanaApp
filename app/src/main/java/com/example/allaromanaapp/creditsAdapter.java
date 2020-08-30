package com.example.allaromanaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class creditsAdapter extends RecyclerView.Adapter<creditsHolder>{

    ArrayList<Creditors> creditsList;
    Context context;
    balCreditActivity credAct;

    public creditsAdapter(ArrayList<Creditors> creditsList, Context context, balCreditActivity credAct) {
        this.creditsList = creditsList;
        this.context = context;
        this.credAct = credAct;
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

            }
        });

        return cdHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull creditsHolder holder, int position) {
        String name = creditsList.get(position).getName();
        String surname = creditsList.get(position).getSurname();
        Long credit = creditsList.get(position).getDebt();
        holder.userName.setText(name + " " + surname);
        holder.creditNum.setText(credit+" $");
    }

    @Override
    public int getItemCount() {
        return creditsList.size();
    }
}

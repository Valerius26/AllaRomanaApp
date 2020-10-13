package com.example.allaromanaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class debtsAdapter extends RecyclerView.Adapter<detsHolder> {

    ArrayList<Creditors> debtsList;
    Context context;
    balanceActivity balAct;
    FirebaseFirestore db;

    public debtsAdapter(ArrayList debtsList, Context context) {
        this.debtsList = debtsList;
        this.context = context;
        this.balAct = balAct;
    }


    @NonNull
    @Override
    public detsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.balancelist,parent,false);

        detsHolder dtHolder = new detsHolder(itemView);

        dtHolder.setOnClickListener(new detsHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedUserID = debtsList.get(position).getId();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                String creditorID = debtsList.get(position).getId();
                String name = debtsList.get(position).getName();
                String surname = debtsList.get(position).getSurname();
                Double debt = debtsList.get(position).getDebt();
                Intent intent = new Intent(context,payActivity.class);
                intent.putExtra("name",name);
                intent.putExtra("surname",surname);
                intent.putExtra("debt",""+debt);
                intent.putExtra("creditorID",creditorID);
                context.startActivity(intent);
            }
        });

        return dtHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull detsHolder holder, int position) {
        String name = debtsList.get(position).getName();
        String surname = debtsList.get(position).getSurname();
        Double debt = debtsList.get(position).getDebt();
        holder.userName.setText(name + " " + surname);
        holder.debtNum.setText(debt+" "+ context.getString(R.string.simbol));
        holder.data.setText(debtsList.get(position).getData());
    }

    @Override
    public int getItemCount() {
        return debtsList.size();
    }
}
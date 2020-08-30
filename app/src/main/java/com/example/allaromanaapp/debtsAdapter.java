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

    public debtsAdapter(balanceActivity balAct, ArrayList debtsList, Context context) {
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

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        return dtHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull detsHolder holder, int position) {
        String name = debtsList.get(position).getName();
        String surname = debtsList.get(position).getSurname();
        Long debt = debtsList.get(position).getDebt();
        holder.userName.setText(name + " " + surname);
        holder.debtNum.setText(debt+" $");
    }

    @Override
    public int getItemCount() {
        return debtsList.size();
    }
}
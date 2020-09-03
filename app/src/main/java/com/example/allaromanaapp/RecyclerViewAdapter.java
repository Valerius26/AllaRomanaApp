package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    List<group> groupList;
    Context context;

    public RecyclerViewAdapter(List<group> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grouplist,parent,false);


        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(itemView);

        recyclerViewHolder.setOnClickListener(new RecyclerViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {


            }
        });

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.name.setText(groupList.get(position).getTitle());
        holder.description.setText(groupList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

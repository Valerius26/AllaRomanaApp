package com.example.allaromanaapp;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView Title,Description,Enter;


    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        Title = itemView.findViewById(R.id.groupTitle);
        Description = itemView.findViewById(R.id.DescriptionName);
        Enter = itemView.findViewById(R.id.GoToGroup);
    }
}

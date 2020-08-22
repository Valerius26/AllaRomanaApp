package com.example.allaromanaapp;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewHolder3 extends RecyclerView.ViewHolder {

    TextView Name;
    ImageView Profile;


    public RecyclerViewHolder3(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.userName);
        Profile = itemView.findViewById(R.id.iconUser);
    }
}

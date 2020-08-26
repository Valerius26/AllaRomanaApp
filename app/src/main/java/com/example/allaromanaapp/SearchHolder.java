package com.example.allaromanaapp;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static androidx.core.content.ContextCompat.startActivity;

public class SearchHolder extends RecyclerView.ViewHolder {

    TextView Name;
    ImageView Profile;
    FloatingActionButton addPartecipant;


    public SearchHolder(@NonNull View itemView) {
        super(itemView);

        Name = itemView.findViewById(R.id.userName);
        Profile = itemView.findViewById(R.id.iconUser);
        addPartecipant = itemView.findViewById(R.id.addPartecipant);
    }
}

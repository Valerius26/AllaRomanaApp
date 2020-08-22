package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.acl.Group;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewAdapter2 extends RecyclerView.Adapter<RecyclerViewHolder2> {

    GroupDetail groupDetailActivity;
    ArrayList<partecipant> partecipants;
    private Context context;



    public RecyclerViewAdapter2( GroupDetail groupDetailActivity, ArrayList<partecipant> partecipants, Context context ){
        this.groupDetailActivity = groupDetailActivity;
        this.partecipants = partecipants;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(groupDetailActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.partecipantlist, parent, false);

        return new RecyclerViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder2 holder2, int position) {
        partecipant partecipante = partecipants.get(position);
        final String partecipantID = partecipante.getIdPartecipante();


        holder2.Name.setText(partecipants.get(position).getNome() + " " + partecipants.get(position).getCognome());

    }

    @Override
    public int getItemCount() {
        return partecipants.size();
    }
}

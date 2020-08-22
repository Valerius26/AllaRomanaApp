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

public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewHolder3> {

    AddPartecipantActivity addPartecipantActivity;
    ArrayList<user> users;
    private Context context;



    public RecyclerViewAdapter3( AddPartecipantActivity addPartecipantActivity, ArrayList<user> users, Context context ){
        this.addPartecipantActivity = addPartecipantActivity;
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(addPartecipantActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.userlist, parent, false);

        return new RecyclerViewHolder3(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder3 holder3, int position) {
        user utente = users.get(position);
        final String userID = utente.getIdUser();


        holder3.Name.setText(users.get(position).getNome() + " " + users.get(position).getCognome());
        holder3.Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //apri il profilo
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", userID);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

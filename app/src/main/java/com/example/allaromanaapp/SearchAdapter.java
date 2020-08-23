package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.acl.Group;
import java.util.ArrayList;

import static androidx.core.content.ContextCompat.startActivity;

public class SearchAdapter extends RecyclerView.Adapter<SearchHolder> {

    AddPartecipantActivity addPartecipantActivity;
    ArrayList<user> usersSearched;
    private Context context;



    public SearchAdapter( AddPartecipantActivity addPartecipantActivity, ArrayList<user> usersSearched, Context context ){
        this.addPartecipantActivity = addPartecipantActivity;
        this.usersSearched = usersSearched;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(addPartecipantActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.userlist, parent, false);

        return new SearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, int position) {
        user utente = usersSearched.get(position);
        final String userID = utente.getIdUser();


        String name = usersSearched.get(position).getNome() + " " + usersSearched.get(position).getCognome();
        holder.Name.setText(name);
        holder.Profile.setOnClickListener(new View.OnClickListener() {
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
        return usersSearched.size();
    }
}

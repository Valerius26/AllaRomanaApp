package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class RecVieAdapterGroupDet extends RecyclerView.Adapter<RecVieHolderGroupDet>  {

    List<User> userList;
    Context context;
    FirebaseFirestore db;
    private ArrayList<User> partecipants;
    GroupDetail groupDetail;

    public RecVieAdapterGroupDet(List<User> userList, Context context, GroupDetail groupDetail) {
        this.userList = userList;
        this.context = context;
        this.groupDetail = groupDetail;
        this.partecipants = this.groupDetail.getPartecipants();
    }

    @NonNull
    @Override
    public RecVieHolderGroupDet onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partecipantslist,parent,false);


        RecVieHolderGroupDet recVieHolderGroupDet = new RecVieHolderGroupDet(itemView);

        recVieHolderGroupDet.setOnClickListener(new RecVieHolderGroupDet.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                String clickedUserID = userList.get(position).getIdUser();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(partecipants.contains(userList.get(position))){
                    partecipants.remove(userList.get(position));
                    groupDetail.setPartecipants(partecipants);
                    Toast.makeText(context,userList.get(position).getNome() + " " + userList.get(position).getCognome() +
                            " " + "rimosso", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }else{
                    partecipants.add(userList.get(position));
                    groupDetail.setPartecipants(partecipants);
                    Toast.makeText(context,userList.get(position).getNome() + " " + userList.get(position).getCognome() +
                            " " + "aggiunto", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                }

            }
        });

        return recVieHolderGroupDet;
    }

    @Override
    public void onBindViewHolder(@NonNull RecVieHolderGroupDet holder, int position) {
        String name = userList.get(position).getNome();
        String surname = userList.get(position).getCognome();
        holder.fullName.setText(name + " " + surname);
        if(partecipants.contains(userList.get(position))){
            holder.info.setText("Rimuovi");
            holder.info.setTextColor(Color.RED);
        }else{
            holder.info.setText("Tieni premuto per selezionare");
            holder.info.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}

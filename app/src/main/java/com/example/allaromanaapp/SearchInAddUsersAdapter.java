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
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SearchInAddUsersAdapter extends RecyclerView.Adapter<ViewHolderAddUsers> {

    AddUsers addUsers;
    List<User> usersList;
    Context context;
    String creatorID,accountID,inAllAccountID;

    public SearchInAddUsersAdapter(AddUsers addUsers, List<User> usersList, Context context, String creatorID, String accountID, String inAllAccountID) {
        this.addUsers = addUsers;
        this.usersList = usersList;
        this.context = context;
        this.creatorID = creatorID;
        this.accountID = accountID;
        this.inAllAccountID = inAllAccountID;
    }

    @NonNull
    @Override
    public ViewHolderAddUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist,parent,false);

        ViewHolderAddUsers viewHolderAddUsers = new ViewHolderAddUsers(itemView);

        viewHolderAddUsers.setOnClickListener(new ViewHolderAddUsers.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                String clickedUserID = usersList.get(position).getIdUser();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        return viewHolderAddUsers;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAddUsers holder, final int position) {

        holder.fullName.setText(usersList.get(position).getNome() + " " + usersList.get(position).getCognome());
        holder.addPartecipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String selectedUserID = usersList.get(position).getIdUser();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

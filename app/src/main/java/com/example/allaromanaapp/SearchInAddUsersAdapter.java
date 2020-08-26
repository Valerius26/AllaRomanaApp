package com.example.allaromanaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SearchInAddUsersAdapter extends RecyclerView.Adapter<ViewHolderAddUsers> {

    AddUsers addUsers;
    List<User> usersList;
    Context context;

    public SearchInAddUsersAdapter(AddUsers addUsers, List<User> usersList, Context context) {
        this.addUsers = addUsers;
        this.usersList = usersList;
        this.context = context;
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

                String fullName = usersList.get(position).getCognome() + " " + usersList.get(position).getNome();
                Toast.makeText(context,""+fullName,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        return viewHolderAddUsers;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAddUsers holder, int position) {

        holder.fullName.setText(usersList.get(position).getNome() + " " + usersList.get(position).getCognome());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

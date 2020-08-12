package com.example.allaromanaapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.acl.Group;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    GroupActivity groupActivity;
    ArrayList<group> groups;


    public RecyclerViewAdapter(GroupActivity groupActivity, ArrayList<group> groups ){
        this.groupActivity = groupActivity;
        this.groups = groups;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(groupActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.grouplist, parent, false);


        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
         holder.Title.setText(groups.get(position).getTitle());
         holder.Description.setText(groups.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}

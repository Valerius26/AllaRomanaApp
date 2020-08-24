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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    GroupActivity groupActivity;
    ArrayList<group> groups;
    private Context context;



    public RecyclerViewAdapter( GroupActivity groupActivity, ArrayList<group> groups, Context context ){
        this.groupActivity = groupActivity;
        this.groups = groups;
        this.context = context;
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
         group gruppo = groups.get(position);
         final String groupID = gruppo.getGroupID();
         final String creatorID = gruppo.getCreatorID();

             holder.Title.setText(groups.get(position).getTitle());
             holder.Description.setText(groups.get(position).getDescription());


         holder.Enter.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 //apri il gruppo
                 Intent intent = new Intent(context,GroupDetail.class);
                 intent.putExtra("idGruppo", groupID);
                 intent.putExtra("idCreatore",creatorID);
                 context.startActivity(intent);
             }
         });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}

package com.example.allaromanaapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    ImageView iconUser,btn_remove;
    TextView name,description,go_inside;
    View usersView;


    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        usersView = itemView;

        iconUser = itemView.findViewById(R.id.iconGroup);
        name = itemView.findViewById(R.id.groupTitle);
        description = itemView.findViewById(R.id.DescriptionName);
        go_inside = itemView.findViewById(R.id.GoToGroup);
        btn_remove = itemView.findViewById(R.id.deleteBtn);

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemLongClick(v, getAdapterPosition());
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(view, getAdapterPosition());

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                clickListener.onItemLongClick(view,getAdapterPosition());
                return true;
            }
        });

    }

    private RecyclerViewHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(RecyclerViewHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }

}

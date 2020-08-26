package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchHolder extends RecyclerView.ViewHolder {

    ImageView profile;
    TextView fullName;
    View usersView;

    private ViewHolderAddUsers.ClickListener clickListener;

    public SearchHolder(@NonNull View itemView) {
        super(itemView);


        usersView = itemView;

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

        profile = itemView.findViewById(R.id.iconUser);
        fullName = itemView.findViewById(R.id.userName);

    }

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(ViewHolderAddUsers.ClickListener clickListener){
        this.clickListener = clickListener;
    }
}

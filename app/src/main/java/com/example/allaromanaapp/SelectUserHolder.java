package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SelectUserHolder extends RecyclerView.ViewHolder {

    ImageView profile;
    TextView fullName,info;
    View usersView;

    public SelectUserHolder(@NonNull View itemView) {
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

        info = itemView.findViewById(R.id.info);
        profile = itemView.findViewById(R.id.iconUser);
        fullName = itemView.findViewById(R.id.userName);
    }

    private SelectUserHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(SelectUserHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }
}

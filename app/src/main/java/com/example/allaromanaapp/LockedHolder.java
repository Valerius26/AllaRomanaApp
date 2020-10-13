package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LockedHolder extends RecyclerView.ViewHolder {

    ImageView iconUser;
    TextView info,userName,debt,debtNum,date;
    View lockedView;

    public LockedHolder(@NonNull View itemView) {
        super(itemView);

        lockedView = itemView;


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

        userName = itemView.findViewById(R.id.userName);
        info = itemView.findViewById(R.id.info);
        iconUser = itemView.findViewById(R.id.iconUser);
        debt = itemView.findViewById(R.id.debt);
        debtNum = itemView.findViewById(R.id.debtNum);
        date = itemView.findViewById(R.id.data);
    }

    private LockedHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(LockedHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }



}

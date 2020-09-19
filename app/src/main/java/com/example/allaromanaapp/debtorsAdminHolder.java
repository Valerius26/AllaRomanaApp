package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class debtorsAdminHolder extends RecyclerView.ViewHolder {

    ImageView iconUser;
    TextView info,userName,debt,debtNum;
    View debtorView;

    public debtorsAdminHolder(@NonNull View itemView) {
        super(itemView);

        debtorView = itemView;


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
    }

    private debtorsAdminHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(debtorsAdminHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }



}

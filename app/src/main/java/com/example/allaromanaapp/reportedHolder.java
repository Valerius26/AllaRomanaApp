package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class reportedHolder extends RecyclerView.ViewHolder {

    ImageView iconUser;
    TextView userName,info,email,debtNum,date;
    View reportedView;

    public reportedHolder(@NonNull View itemView) {
        super(itemView);

        reportedView = itemView;


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
        email = itemView.findViewById(R.id.debt);
        debtNum = itemView.findViewById(R.id.debtNum);
        date = itemView.findViewById(R.id.data);
    }

    private reportedHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(reportedHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }



}

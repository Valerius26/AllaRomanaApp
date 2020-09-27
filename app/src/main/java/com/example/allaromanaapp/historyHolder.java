package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class historyHolder extends RecyclerView.ViewHolder {

    ImageView icon_payment;
    TextView sold,userName,data;
    View historyView;

    public historyHolder(@NonNull View itemView) {
        super(itemView);

        historyView = itemView;


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
        sold = itemView.findViewById(R.id.sold);
        icon_payment = itemView.findViewById(R.id.iconPayment);
        data = itemView.findViewById(R.id.data);
    }

    private historyHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(historyHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }



}

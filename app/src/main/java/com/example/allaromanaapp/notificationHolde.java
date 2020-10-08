package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class notificationHolde extends RecyclerView.ViewHolder {

    ImageView iconMsg;
    TextView info,userName,date;
    View notificationView;

    public notificationHolde(@NonNull View itemView) {
        super(itemView);

        notificationView = itemView;


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
        iconMsg = itemView.findViewById(R.id.iconMessage);
        date = itemView.findViewById(R.id.data);
    }

    private notificationHolde.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(notificationHolde.ClickListener clickListener){
        this.clickListener = clickListener;
    }



}

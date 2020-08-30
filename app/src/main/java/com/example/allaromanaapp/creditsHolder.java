package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class creditsHolder extends RecyclerView.ViewHolder{

    TextView userName,info,credit,creditNum;
    ImageView iconUser;
    View creditView;

    public creditsHolder(@NonNull View itemView) {
        super(itemView);
        creditView = itemView;


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
        credit = itemView.findViewById(R.id.credit);
        creditNum = itemView.findViewById(R.id.creditNum);
    }

    private creditsHolder.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(creditsHolder.ClickListener clickListener){
        this.clickListener = clickListener;
    }


}

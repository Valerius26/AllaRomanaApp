package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class recVieGroupCompleteHolder extends RecyclerView.ViewHolder {

    ImageView iconUser;
    TextView fullName,info;
    View usersView;

    public recVieGroupCompleteHolder(@NonNull View itemView) {
        super(itemView);
        usersView = itemView;

        iconUser = itemView.findViewById(R.id.iconUser);
        fullName = itemView.findViewById(R.id.userName);
        info = itemView.findViewById(R.id.info);

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

    private RecVieHolderGroupDet.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(RecVieHolderGroupDet.ClickListener clickListener){
        this.clickListener = clickListener;
    }

}

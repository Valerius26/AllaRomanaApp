package com.example.allaromanaapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ViewHolderAddUsers extends RecyclerView.ViewHolder {

    ImageView profile;
    TextView fullName;
    RelativeLayout partecipantCard;
    View usersView;

    public ViewHolderAddUsers(@NonNull View itemView) {
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

        partecipantCard = itemView.findViewById(R.id.partecipantCard);
        profile = itemView.findViewById(R.id.iconUser);
        fullName = itemView.findViewById(R.id.userName);
    }

    private ViewHolderAddUsers.ClickListener clickListener;

    public interface ClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnClickListener(ViewHolderAddUsers.ClickListener clickListener){
        this.clickListener = clickListener;
    }
}

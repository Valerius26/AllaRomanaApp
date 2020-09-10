package com.example.allaromanaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserHolder> {

    List<User> usersList;
    Context context;
    FirebaseFirestore db;

    public SelectUserAdapter(List<User> usersList,Context context) {
        this.usersList = usersList;
        this.context = context;
    }


    @NonNull
    @Override
    public SelectUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist,parent,false);

        SelectUserHolder selectUserHolder = new SelectUserHolder(itemView);

        selectUserHolder.setOnClickListener(new SelectUserHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                String clickedUserID = usersList.get(position).getIdUser();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                db = FirebaseFirestore.getInstance();

                final EditText resetMail = new EditText(view.getContext());
                final AlertDialog.Builder confirm = new AlertDialog.Builder(view.getContext());
                confirm.setTitle("Invio Posizione");
                confirm.setMessage("Vuoi inviare la posizione a " + usersList.get(position).getNome() + " " + usersList.get(position).getCognome()+"?");
                confirm.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // extract the email and send reset link

                    }
                });

                confirm.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       //close the dialog

                    }
                });

                confirm.create().show();

            }
        });

        return selectUserHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectUserHolder holder, int position) {
        String name = usersList.get(position).getNome();
        String surname = usersList.get(position).getCognome();
        holder.fullName.setText(name + " " + surname);
        holder.info.setText("Tieni premuto per selezionare");
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

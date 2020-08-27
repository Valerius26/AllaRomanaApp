package com.example.allaromanaapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchInAddUsersAdapter extends RecyclerView.Adapter<ViewHolderAddUsers> {

    AddUsers addUsers;
    List<User> usersList;
    Context context;
    String creatorID,accountID,inAllAccountID;
    FirebaseFirestore db;

    public SearchInAddUsersAdapter(AddUsers addUsers, List<User> usersList, Context context, String creatorID, String accountID, String inAllAccountID) {
        this.addUsers = addUsers;
        this.usersList = usersList;
        this.context = context;
        this.creatorID = creatorID;
        this.accountID = accountID;
        this.inAllAccountID = inAllAccountID;
    }

    @NonNull
    @Override
    public ViewHolderAddUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userlist,parent,false);

        ViewHolderAddUsers viewHolderAddUsers = new ViewHolderAddUsers(itemView);

        viewHolderAddUsers.setOnClickListener(new ViewHolderAddUsers.ClickListener() {
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
                createPartecipant(usersList.get(position).getIdUser(),usersList.get(position).getNome(),
                        usersList.get(position).getCognome());
            }
        });

        return viewHolderAddUsers;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAddUsers holder, final int position) {

        holder.fullName.setText(usersList.get(position).getNome() + " " + usersList.get(position).getCognome());

    }

    private void createPartecipant(final String selectedUserID, final String name, final String surname) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Creatore",creatorID);
        hashMap.put("IdAccount in creatore",accountID);
        hashMap.put("IdAccount in all", inAllAccountID);

        db.collection("users").document(selectedUserID).collection("accounts").add(hashMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        createPartecipantInCreator(name,surname,selectedUserID);
                    }
                });

    }

    public void createPartecipantInCreator(final String name, final String surname, String selectedUserID){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("nomePartecipante",name);
        hashMap.put("cognomePartecipante",surname);
        hashMap.put("idUtente",selectedUserID);

        db.collection("users").document(creatorID).collection("accounts")
                .document(accountID).collection("partecipants").add(hashMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, name + " " + surname + " aggiunto", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

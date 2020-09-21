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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewAdapterAddUsers extends RecyclerView.Adapter<ViewHolderAddUsers> {

    List<User> usersList;
    Context context;
    FirebaseFirestore db;
    String creatorID, accountID;

    public ViewAdapterAddUsers(List<User> usersList, Context context, String creatorID, String accountID) {
        this.usersList = usersList;
        this.context = context;
        this.creatorID = creatorID;
        this.accountID = accountID;
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
                partecipantExists(usersList.get(position).getIdUser(),usersList.get(position).getNome(),
                        usersList.get(position).getCognome());

            }
        });

        return viewHolderAddUsers;
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolderAddUsers holder, final int position) {

        String name = usersList.get(position).getNome();
        String surname = usersList.get(position).getCognome();
        holder.fullName.setText(name + " " + surname);



    }



    private void partecipantExists(final String selectedUserID, final String nome, final String cognome) {
        db.collection("users").document(creatorID).collection("accounts")
                .document(accountID).collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> duplicate = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String idUtente = documentSnapshot.getString("idUtente");
                    if(idUtente.equals(selectedUserID)){
                        duplicate.add(idUtente);
                        Toast.makeText(context,"non puoi aggiungere un utente che già partecipa al conto",Toast.LENGTH_SHORT).show();
                    }

                }
                if(duplicate.size()==0) {

                    createPartecipantInCreator(nome, cognome, selectedUserID);
                }
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

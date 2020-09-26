package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecVieAdapterGroupDet extends RecyclerView.Adapter<RecVieHolderGroupDet>  {

    ArrayList<User> userList;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String currentuserID,groupID;

    public RecVieAdapterGroupDet(ArrayList userList, Context context, String groupID) {
        this.userList = userList;
        this.context = context;
        this.groupID = groupID;
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserID = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public RecVieHolderGroupDet onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partecipantslist,parent,false);


        RecVieHolderGroupDet recVieHolderGroupDet = new RecVieHolderGroupDet(itemView);

        recVieHolderGroupDet.setOnClickListener(new RecVieHolderGroupDet.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                String clickedUserID = userList.get(position).getIdUser();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                partecipantExists(userList.get(position).getIdUser(),userList.get(position).getNome(),
                        userList.get(position).getCognome());
            }
        });

        return recVieHolderGroupDet;
    }

    @Override
    public void onBindViewHolder(@NonNull RecVieHolderGroupDet holder, int position) {
        String name = userList.get(position).getNome();
        String surname = userList.get(position).getCognome();
        holder.fullName.setText(name + " " + surname);


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void partecipantExists(final String selectedUserID, final String nome, final String cognome) {
        db.collection("users").document(currentuserID).collection("groups")
                .document(groupID).collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> duplicate = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    String idUtente = documentSnapshot.getString("idUtente");
                    if(idUtente.equals(selectedUserID)){
                        duplicate.add(idUtente);
                        Toast.makeText(context,context.getString(R.string.partExists),Toast.LENGTH_SHORT).show();
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
        db.collection("users").document(currentuserID).collection("groups")
                .document(groupID).collection("partecipants").add(hashMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(context, name + " " + surname + " " + context.getString(R.string.added), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}

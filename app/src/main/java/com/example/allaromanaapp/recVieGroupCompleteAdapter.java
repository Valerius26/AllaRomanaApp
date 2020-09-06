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

public class recVieGroupCompleteAdapter extends RecyclerView.Adapter<recVieGroupCompleteHolder>  {

    ArrayList<User> userList;
    Context context;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    String currentuserID,groupID;

    public recVieGroupCompleteAdapter(ArrayList userList, Context context, String groupID) {
        this.userList = userList;
        this.context = context;
        this.groupID = groupID;
        firebaseAuth = FirebaseAuth.getInstance();
        currentuserID = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public recVieGroupCompleteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partecipantslist,parent,false);


        recVieGroupCompleteHolder recVieGroupCompleteHolder = new recVieGroupCompleteHolder(itemView);

        recVieGroupCompleteHolder.setOnClickListener(new RecVieHolderGroupDet.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                db = FirebaseFirestore.getInstance();
                String clickedUserID = userList.get(position).getIdRef();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(userList.get(position).getIdRef().equals(currentuserID))
                {
                    Toast.makeText(context,"non puoi eliminarti",Toast.LENGTH_SHORT).show();
                }
                else {
                    db = FirebaseFirestore.getInstance();
                    minTwoPartecipant(userList.get(position).getIdUser(), position);
                }
            }
        });

        return recVieGroupCompleteHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull recVieGroupCompleteHolder holder, int position) {
        String name = userList.get(position).getNome();
        String surname = userList.get(position).getCognome();
        holder.fullName.setText(name + " " + surname);
        holder.info.setText("Rimuovi");
        holder.info.setTextColor(Color.RED);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public ArrayList getPartecipantsList(){
     return userList;
    }


    private void minTwoPartecipant(final String id, final int position) {
        db.collection("users").document(currentuserID).collection("groups").document(groupID)
                .collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<String> partecipants = new ArrayList<>();
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    String id = documentSnapshot.getId();
                    partecipants.add(id);
                }
                if(partecipants.size() < 3){
                    Toast.makeText(context,R.string.aggiungiPartecipante, Toast.LENGTH_SHORT).show();

                }
                else{
                    db.collection("users").document(currentuserID).collection("groups")
                            .document(groupID).collection("partecipants").document(id).delete();
                    Toast.makeText(context,"utente eliminato", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, userList.size());
                }

            }
        });
    }



}

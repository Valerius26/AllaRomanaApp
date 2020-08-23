package com.example.allaromanaapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewHolder3> {

    AddPartecipantActivity addPartecipantActivity;
    ArrayList<user> users;
    private Context context;
    String groupID;


    public RecyclerViewAdapter3( AddPartecipantActivity addPartecipantActivity, ArrayList<user> users, String groupID, Context context ){
        this.addPartecipantActivity = addPartecipantActivity;
        this.users = users;
        this.context = context;
        this.groupID = groupID;
    }

    @NonNull
    @Override
    public RecyclerViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(addPartecipantActivity.getBaseContext());
        View view = layoutInflater.inflate(R.layout.userlist, parent, false);

        return new RecyclerViewHolder3(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder3 holder3, int position) {
        user utente = users.get(position);
        final String userID = utente.getIdUser();



        holder3.Name.setText(users.get(position).getNome() + " " + users.get(position).getCognome());
        holder3.Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //apri il profilo
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", userID);
                context.startActivity(intent);
            }
        });
        holder3.AddPartecipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.d("NOMEGRUPPO",groupID);
            }
        });

    }

    private void CreatePartecipant(String userID, String groupID) {

/*            String groupID = documentReference.getId();



            HashMap<String,String> hashMap1 = new HashMap<>();
            hashMap1.put("Ruolo", "creatore");
            hashMap1.put("idUtente", ""+userID);
            hashMap1.put("idGruppo", ""+groupID);
            hashMap1.put("nomePartecipante",""+nome);
            hashMap1.put("cognomePartecipante",""+cognome);


            CollectionReference collectionReference1 = fStore.collection("users").document(userID).collection("groups").document(groupID).collection("partecipants");
            collectionReference1.add(hashMap1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


     */
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

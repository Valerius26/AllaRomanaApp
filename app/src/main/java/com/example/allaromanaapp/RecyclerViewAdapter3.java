package com.example.allaromanaapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewAdapter3 extends RecyclerView.Adapter<RecyclerViewHolder3>{

    AddPartecipantActivity addPartecipantActivity;
    ArrayList<user> users;
    private Context context;
    String groupID,userID;
    FirebaseFirestore fStore;
    String title,description,creatorID,nome,cognome;

    public RecyclerViewAdapter3( AddPartecipantActivity addPartecipantActivity, ArrayList<user> users, String groupID, String creatorID, Context context ){
        this.addPartecipantActivity = addPartecipantActivity;
        this.users = users;
        this.context = context;
        this.groupID = groupID;
        this.creatorID = creatorID;
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
        userID = utente.getIdUser();



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
               startGroup(userID,groupID,creatorID);
               Log.d("NOMEGRUPPO",groupID);
               Toast.makeText(context,"nuovo partecipante aggiunto", Toast.LENGTH_SHORT).show();
               Intent intent = new Intent(context, GroupDetail.class);
               intent.putExtra("idGruppo",groupID);
               context.startActivity(intent);

            }
        });

    }


    private void startGroup(String userID, String groupID, String creatorID) {

        fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("users").document(creatorID).
                collection("groups").document(groupID);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                title = value.getString("Nome gruppo");
                description = value.getString("Descrizione");
            }
        });

            createGroup(title, description,creatorID,userID);
    }

    private void createGroup(String groupTitle, String groupDescription, String creatoreID, final String userID) {

        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Nome gruppo", ""+groupTitle);
        hashMap.put("Descrizione", ""+groupDescription);
        hashMap.put("Creato da", ""+creatoreID);
        hashMap.put("ID gruppo",""+groupID);

        DocumentReference documentReference1 = fStore.collection("users").document(userID);
        documentReference1.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                nome = value.getString("nome");
                cognome = value.getString("cognome");
            }
        });

        //crea il gruppo
        CollectionReference collectionReference = fStore.collection("users").document(userID).collection("groups");
        collectionReference.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                createPartecipant( nome, cognome, userID, creatorID, groupID);
                IncrementGroup();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void IncrementGroup() {

        DocumentReference dr = fStore.collection("users").document(userID);

        dr.update("gruppi", FieldValue.increment(1));

    }

    private void createPartecipant( String nome, final String cognome, String userID,
                                   String creatorID, String groupID) {



        HashMap<String,String> hashMap1 = new HashMap<>();
        hashMap1.put("Ruolo", "partecipante");
        hashMap1.put("idUtente", ""+userID);
        hashMap1.put("idGruppo", ""+groupID);
        hashMap1.put("nomePartecipante",""+nome);
        hashMap1.put("cognomePartecipante",""+cognome);

        final CollectionReference collectionReference1 = fStore.collection("users").document(creatorID).collection("groups").document(groupID).collection("partecipants");
        collectionReference1.add(hashMap1).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }



    @Override
    public int getItemCount() {
        return users.size();
    }
}

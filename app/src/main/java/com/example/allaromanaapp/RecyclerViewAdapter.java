package com.example.allaromanaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    List<group> groupList;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String id;

    public RecyclerViewAdapter(List<group> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        id = firebaseAuth.getUid();
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grouplist,parent,false);


        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(itemView);

        recyclerViewHolder.setOnClickListener(new RecyclerViewHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context,SelectPayingInGroup.class);
                intent.putExtra("idGruppo",groupList.get(position).getGroupID());
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int position) {
                final AlertDialog.Builder notificationDialog = new AlertDialog.Builder(view.getContext());
                notificationDialog.setTitle(context.getString(R.string.deleteGroup));
                notificationDialog.setMessage(context.getString(R.string.areYouSureGr));
                notificationDialog.setPositiveButton(context.getString(R.string.si), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteG(position, groupList.get(position).getGroupID(),groupList.get(position).getTitle(),groupList.get(position).getDescription(),groupList.get(position).getCreatorID());
                    }
                });
                notificationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                notificationDialog.create().show();
            }

        });

        return recyclerViewHolder;
    }


    private void deleteG(final int position, final String group_id, final String title, final String description, String creator){

       if(id.equals(creator)){
           db.collection("users").document(id).collection("groups").document(group_id).collection("partecipants")
                   .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                     for(DocumentSnapshot documentSnapshot: task.getResult()){
                         deleteInPartecipant(documentSnapshot.getString("idUtente"),title,description);
                     }
                   Toast.makeText(context,context.getString(R.string.groupDeleted),Toast.LENGTH_SHORT).show();
                   groupList.remove(position);
                   notifyItemRemoved(position);
                   notifyItemRangeChanged(position, groupList.size());
               }
           });
       }else{
           db.collection("users").document(id).collection("groups").document(group_id)
                   .collection("partecipants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
               @Override
               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                   int partNum = task.getResult().size();
                   if(partNum != 2) {
                       for (DocumentSnapshot documentSnapshot : task.getResult()) {
                           String id_utente = documentSnapshot.getString("idUtente");
                           if (id_utente.equals(id)) {

                           } else {
                               deletePInPartecipant(title, description, id_utente);
                           }
                       }
                       delete(group_id, id);
                       Toast.makeText(context, context.getString(R.string.partecipantDelete), Toast.LENGTH_SHORT).show();
                       groupList.remove(position);
                       notifyItemRemoved(position);
                       notifyItemRangeChanged(position, groupList.size());
                   }
                   else{
                           Toast.makeText(context, context.getString(R.string.dontRemove), Toast.LENGTH_SHORT).show();
                       }

               }
           });
       }
    }

    private void deletePInPartecipant(final String title, final String description, final String id_part) {
        db.collection("users").document(id_part).collection("groups")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(final DocumentSnapshot documentSnapshot : task.getResult()){
                    if(documentSnapshot.getString("Nome gruppo").equals(title) && documentSnapshot.getString("Descrizione").equals(description))
                    {
                        final String groupID = documentSnapshot.getId();
                        db.collection("users").document(id_part).collection("groups")
                                .document(groupID).collection("partecipants")
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 for(DocumentSnapshot documentSnapshot1: task.getResult()){
                                     if(documentSnapshot1.getString("idUtente").equals(id)){
                                         deleteP(id_part,groupID,documentSnapshot1.getId());
                                         break;
                                     }
                                 }
                            }
                        });
                        break;
                    }
                }
            }
        });
    }

    private void deleteP(String id_part, String group_id, String idUtente) {
        db.collection("users").document(id_part).collection("groups")
                .document(group_id).collection("partecipants").document(idUtente).delete();

    }


    private void deleteInPartecipant(final String idUtente, final String title, final String description) {
        db.collection("users").document(idUtente).collection("groups").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         for(DocumentSnapshot documentSnapshot: task.getResult()){
                             if(documentSnapshot.getString("Nome gruppo").equals(title) &&
                             documentSnapshot.getString("Descrizione").equals(description)){
                                 delete(documentSnapshot.getId(),idUtente);
                             }
                         }
                    }
                });
    }

    private void delete(String id,String idUtente) {
        db.collection("users").document(idUtente).collection("groups")
                .document(id).delete();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.name.setText(groupList.get(position).getTitle());
        holder.description.setText(groupList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }
}

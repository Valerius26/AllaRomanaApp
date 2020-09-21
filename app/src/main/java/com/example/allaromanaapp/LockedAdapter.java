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

public class LockedAdapter extends RecyclerView.Adapter<LockedHolder> {

    ArrayList<Creditors> lockedList;
    Context context;
    AdminActivity adminActivity;
    FirebaseFirestore db;

    public LockedAdapter(ArrayList lockedList, Context context) {
        this.lockedList = lockedList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public LockedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.balancelist,parent,false);

        LockedHolder lockedHolder = new LockedHolder(itemView);

        lockedHolder.setOnClickListener(new LockedHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedUserID = lockedList.get(position).getId();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                deleteLocked(lockedList.get(position).getId(), position);
                sendNotification(lockedList.get(position).getId());
            }
        });



        return lockedHolder;
    }


   private void deleteLocked(final String clickedId, final int position){
        db.collection("blocked").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    if(documentSnapshot.getString("idUtente").equals(clickedId)){
                        db.collection("blocked").document(documentSnapshot.getId()).delete();
                        Toast.makeText(context,"L'utente è stato sbloccato",Toast.LENGTH_SHORT).show();
                        lockedList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, lockedList.size());
                        break;
                    }
                }
            }
        });
   }


    @Override
    public void onBindViewHolder(@NonNull LockedHolder holder, int position) {
        String name = lockedList.get(position).getName();
        String surname = lockedList.get(position).getSurname();
        Long debt = lockedList.get(position).getDebt();
        holder.userName.setText(name + " " + surname);
        holder.debtNum.setText(debt+" $");
        holder.info.setText("Tieni premuto per sbloccare");
    }

    @Override
    public int getItemCount() {
        return lockedList.size();
    }

    private void sendNotification(String id) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("idMittente","Admin");
        hashMap.put("testo","Sei stato sbloccato");
        hashMap.put("nomeMittente","Admin");
        hashMap.put("cognomeMittente","");
        hashMap.put("letto","no");
        hashMap.put("daPagare",""+0);

        db.collection("users").document(id).collection("notify").add(hashMap);
    }
}
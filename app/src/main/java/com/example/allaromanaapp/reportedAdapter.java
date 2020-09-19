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

public class reportedAdapter extends RecyclerView.Adapter<reportedHolder> {

    ArrayList<Reported> reportedList;
    Context context;
    AdminActivity adminActivity;
    FirebaseFirestore db;

    public reportedAdapter(ArrayList reportedList, Context context) {
        this.reportedList = reportedList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public reportedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.balancelist,parent,false);

        reportedHolder reportedHolder = new reportedHolder(itemView);

        reportedHolder.setOnClickListener(new reportedHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedUserID = reportedList.get(position).getId_ref();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                saveLocked(reportedList.get(position).getId_ref(),reportedList.get(position).getName(),
                        reportedList.get(position).getSurname());
            }
        });



        return reportedHolder;
    }

    private void saveLocked(final String id, final String nome, final String cognome) {
        db.collection("blocked").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int vero = 0;
                for (DocumentSnapshot documentSnapshot : task.getResult()){
                    if(documentSnapshot.getString("idUtente").equals(id)){
                        vero = 1;
                        Toast.makeText(context,"L'utente è stato già bloccato",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                save(id,nome,cognome,vero);
            }
        });
    }

    private void save(String id, final String nome, final String cognome, int esistente) {
        if(esistente==0) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("idUtente", id);
            hashMap.put("nome", nome);
            hashMap.put("cognome", cognome);

            db.collection("blocked").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(context, "" + nome + " " + cognome + " bloccato", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onBindViewHolder(@NonNull reportedHolder holder, int position) {
        String e_mail = reportedList.get(position).getE_mail();
        String name = reportedList.get(position).getName();
        String surname = reportedList.get(position).getSurname();
        holder.userName.setText(name + " " + surname);
        holder.email.setText(e_mail);
        holder.debtNum.setVisibility(View.INVISIBLE);
        holder.info.setText("Tieni premuto per bloccare questo utente");
    }

    @Override
    public int getItemCount() {
        return reportedList.size();
    }
}
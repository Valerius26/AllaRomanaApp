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

public class debtorsAdminAdapter extends RecyclerView.Adapter<debtorsAdminHolder> {

    ArrayList<Creditors> debtsList;
    Context context;
    AdminActivity adminActivity;
    FirebaseFirestore db;

    public debtorsAdminAdapter(ArrayList debtsList, Context context) {
        this.debtsList = debtsList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public debtorsAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.balancelist,parent,false);

        debtorsAdminHolder debtorsAdminHolder = new debtorsAdminHolder(itemView);

        debtorsAdminHolder.setOnClickListener(new debtorsAdminHolder.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String clickedUserID = debtsList.get(position).getId();
                Intent intent = new Intent(context,NotCurrentProfileActivity.class);
                intent.putExtra("idUtente", clickedUserID);
                context.startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                saveLocked(debtsList.get(position).getId(),debtsList.get(position).getName(),
                        debtsList.get(position).getSurname());
            }
        });



        return debtorsAdminHolder;
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

    private void save(final String id, final String nome, final String cognome, int esistente) {
        if(esistente==0) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("idUtente", id);
            hashMap.put("nome", nome);
            hashMap.put("cognome", cognome);

            db.collection("blocked").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Toast.makeText(context, "" + nome + " " + cognome + " bloccato", Toast.LENGTH_SHORT).show();
                    sendNotification(id);
                }
            });
        }
    }


    @Override
    public void onBindViewHolder(@NonNull debtorsAdminHolder holder, int position) {
        String name = debtsList.get(position).getName();
        String surname = debtsList.get(position).getSurname();
        Long debt = debtsList.get(position).getDebt();
        holder.userName.setText(name + " " + surname);
        holder.debtNum.setText(debt+" $");
        holder.info.setText("Tieni premuto per bloccare questo utente");
    }

    @Override
    public int getItemCount() {
        return debtsList.size();
    }

    private void sendNotification(String id) {
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("idMittente","Admin");
        hashMap.put("testo","Sei stato bloccato a causa dei troppi debiti\n" +
                "Verrai sbloccato quando i tuoi debiti diminuiranno");
        hashMap.put("nomeMittente","Admin");
        hashMap.put("cognomeMittente","");
        hashMap.put("letto","no");
        hashMap.put("daPagare",""+0);

        db.collection("users").document(id).collection("notify").add(hashMap);
    }
}
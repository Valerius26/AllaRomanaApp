package com.example.allaromanaapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DebtorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DebtorsFragment extends Fragment {

    FirebaseFirestore db;
    ArrayList<Creditors> debtors = new ArrayList<>();
    RecyclerView re;
    View v;
    debtorsAdminAdapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DebtorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DebtorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DebtorsFragment newInstance(String param1, String param2) {
        DebtorsFragment fragment = new DebtorsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_debtors, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.v = view;

        init();
        loadData();
    }

    private void loadData() {
        db = FirebaseFirestore.getInstance();
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 for(DocumentSnapshot documentSnapshot: task.getResult()){
                     totalDebt(documentSnapshot.getId(),documentSnapshot.getString("nome"),documentSnapshot.getString("cognome"));
                 }

            }
        });
    }

    private void totalDebt(final String id, final String name, final String surname) {
        final NumberFormat nf = NumberFormat.getInstance();
        db.collection("users").document(id).collection("debts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(!task.getResult().isEmpty()){
                    Double total = Double.valueOf(0);
                    for(DocumentSnapshot documentSnapshot : task.getResult()){
                        try {
                            total = total + nf.parse(documentSnapshot.getString("debito")).doubleValue();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if(total > 99){
                        debtors.add(new Creditors(id,name,surname,total,""));
                    }

                    Collections.sort(debtors, new Comparator<Creditors>() {
                        @Override
                        public int compare(Creditors d, Creditors d1) {
                            return d.getName().compareTo(d1.getName());
                        }
                    });
                    adapter = new debtorsAdminAdapter(debtors, getContext());
                    re.setAdapter(adapter);
                }
            }
        });
    }

    private void init() {

        re = (RecyclerView)v.findViewById(R.id.recyclerDebtor);
        re.setHasFixedSize(true);
        re.setLayoutManager(new LinearLayoutManager(getContext()));
        re.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));


    }


}
package com.example.allaromanaapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReportedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReportedFragment extends Fragment {

    FirebaseFirestore db;
    ArrayList<Reported> reporteds = new ArrayList<>();
    RecyclerView re;
    View v;
    reportedAdapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReportedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReportedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReportedFragment newInstance(String param1, String param2) {
        ReportedFragment fragment = new ReportedFragment();
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
        return inflater.inflate(R.layout.fragment_reported, container, false);
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
        db.collection("reports").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(DocumentSnapshot documentSnapshot : task.getResult()){
                    recoverData(documentSnapshot.getString("idUtente"),documentSnapshot.getId());
                }
            }
        });
    }

    private void recoverData(final String idUtente, final String idReport) {
        db.collection("users").document(idUtente).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){

                }else{
                    Reported repUser = new Reported(idReport,value.getString("nome"),
                            value.getString("cognome"),value.getString("e-mail"),idUtente);
                    reporteds.add(repUser);
                }
                Collections.sort(reporteds, new Comparator<Reported>() {
                    @Override
                    public int compare(Reported r, Reported r1) {
                        return r.getE_mail().compareTo(r1.getE_mail());
                    }
                });
                adapter = new reportedAdapter(reporteds,getContext());
                re.setAdapter(adapter);
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
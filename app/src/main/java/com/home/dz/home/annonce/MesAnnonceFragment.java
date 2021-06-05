package com.home.dz.home.annonce;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home.dz.R;
import com.home.dz.adapter.MesAnnonceAdapter;
import com.home.dz.bean.Factory;
import com.home.dz.bean.Home;
import com.home.dz.home.HomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MesAnnonceFragment extends Fragment {

    private RecyclerView listeAnnonce;
    private DatabaseReference databaseAnnonce;
    private MesAnnonceAdapter adapter;
    private List<Home> homeList = new ArrayList<>();
    private HomeActivity activity;
    private ProgressDialog progressDoalog ;

    public MesAnnonceFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ValidFragment")
    public MesAnnonceFragment(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mes_annonce, container, false);

        listeAnnonce = v.findViewById(R.id.listemesannonce_rv);
        databaseAnnonce = FirebaseDatabase.getInstance().getReference("home");
        progressDoalog = new ProgressDialog(activity);
        progressDoalog.setMessage("Loading ...");

        getMesAnnonces(this);

        return v;
    }

    private void getMesAnnonces( final MesAnnonceFragment context) {

        progressDoalog.show();
        databaseAnnonce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                homeList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Home home = postSnapshot.getValue(Home.class);

                    if(home.getUser().getEmail().equals(Factory.getUser().getEmail())){
                        homeList.add(home);
                    }

                }

                progressDoalog.dismiss();

                adapter = new MesAnnonceAdapter(homeList, activity, context);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                listeAnnonce.setLayoutManager(mLayoutManager);
                listeAnnonce.setItemAnimator(new DefaultItemAnimator());
                listeAnnonce.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressDoalog.dismiss();
            }
        });
    }

    public void deleteAnnonce( int position ){
        String idHome = homeList.get(position).getIdHome();

        DatabaseReference databaseReference = databaseAnnonce.child(idHome);
        databaseReference.removeValue();
        homeList.remove(position);
        adapter.notifyDataSetChanged();
    }

}

package com.home.dz.home.annonce;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home.dz.R;
import com.home.dz.adapter.AnnonceAdapter;
import com.home.dz.bean.Home;
import com.home.dz.data.FavorisDataHelper;
import com.home.dz.home.HomeActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListeAnnoncesFragment extends Fragment {


    private RecyclerView listeAnnonce;
    private DatabaseReference databaseAnnonce;
    private AnnonceAdapter adapter;
    private List<Home> homeList = new ArrayList<>();
    private HomeActivity activity;
    private ProgressDialog progressDoalog ;
    private FavorisDataHelper dataHelper;
    private BottomSheetBehavior bottomSheetBehavior;
    private TextView filter;
    private EditText prixMin, prixMax;
    private Button recherche;
    private Spinner wilaya;
    private String[] listeWilaya = {"Wilaya1", "Wilaya2", "Wilaya3"};
    private String sWilaya;
    private long lPrixMin = 0, lPrixMax = 0;

    public ListeAnnoncesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public ListeAnnoncesFragment(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_liste_annonces, container, false);

        ConstraintLayout bottomSheetLayout = v.findViewById (R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        filter = v.findViewById(R.id.filter_btmsh_et);
        prixMin = v.findViewById(R.id.prixmin_btmsh_et);
        prixMax = v.findViewById(R.id.prixmax_btmsh_et);
        recherche = v.findViewById(R.id.rech_btmsh_btn);
        wilaya = v.findViewById(R.id.wilaya_btmsh_sp);

        ArrayAdapter adapter = new ArrayAdapter(activity,android.R.layout.simple_spinner_item,listeWilaya);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wilaya.setAdapter(adapter);

        listeAnnonce = v.findViewById(R.id.listeannonce_rv);
        databaseAnnonce = FirebaseDatabase.getInstance().getReference("home");
        progressDoalog = new ProgressDialog(activity);
        progressDoalog.setMessage("Loading ...");
        dataHelper = new FavorisDataHelper(activity);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });

        recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechercherAnnonce();
            }
        });

        wilaya.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sWilaya = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getAnnonces(this);

        return v;
    }

    private void rechercherAnnonce() {

        lPrixMin = 0;
        lPrixMax = 0;
        if(!prixMin.getText().toString().trim().isEmpty()){
            lPrixMin = Long.parseLong(prixMin.getText().toString().trim());
        }

        if(!prixMax.getText().toString().trim().isEmpty()){
            lPrixMax = Long.parseLong(prixMax.getText().toString().trim());
        }

        if(lPrixMin < 0){
            Toast.makeText(activity, "Le prix min n'est pas valide", Toast.LENGTH_LONG).show();
            return;
        }

        if(lPrixMin >= lPrixMax){
            Toast.makeText(activity, "Le prix max doit etre superieur au peix min", Toast.LENGTH_LONG).show();
            return;
        }

        progressDoalog.show();
        databaseAnnonce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                homeList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Home home = postSnapshot.getValue(Home.class);

                    if(sWilaya.equals(home.getWilaya()) && lPrixMax >= home.getPrix() && lPrixMin <= home.getPrix()){
                        homeList.add(home);
                    }

                }

                progressDoalog.dismiss();

                adapter.notifyDataSetChanged();

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, databaseError.getMessage(),Toast.LENGTH_LONG).show();
                progressDoalog.dismiss();
            }
        });

    }

    public void ajouterFavori(int position){

        Home home = homeList.get(position);

        List<Home> homeFavoris = dataHelper.getFavoris();

        if(homeFavoris.contains(home)){

            Toast.makeText(activity, "L'annonce " + home.getTitre() + " existe déjà dans favoris", Toast.LENGTH_LONG).show();

        }else {
            dataHelper.ajouterFavori(home);

            Toast.makeText(activity, "L'annonce " + home.getTitre() + " est ajoutée au favoris", Toast.LENGTH_LONG).show();
        }

    }

    private void getAnnonces(final ListeAnnoncesFragment fragment) {

        progressDoalog.show();
        databaseAnnonce.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                homeList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Home home = postSnapshot.getValue(Home.class);
                    homeList.add(home);
                }

                progressDoalog.dismiss();

                adapter = new AnnonceAdapter(fragment,homeList,activity);
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

}

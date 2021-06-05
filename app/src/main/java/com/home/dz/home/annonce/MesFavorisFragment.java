package com.home.dz.home.annonce;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.home.dz.R;
import com.home.dz.adapter.MesFavorisAdapter;
import com.home.dz.bean.Home;
import com.home.dz.data.FavorisDataHelper;
import com.home.dz.home.HomeActivity;

import java.util.List;


public class MesFavorisFragment extends Fragment {


    private RecyclerView listeAnnonce;
    private MesFavorisAdapter adapter;
    private List<Home> homeList = null;
    private HomeActivity activity;
    private ProgressDialog progressDoalog ;
    private FavorisDataHelper dataHelper;

    public MesFavorisFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public MesFavorisFragment(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mes_favoris, container, false);
        dataHelper = new FavorisDataHelper(activity);

        listeAnnonce = v.findViewById(R.id.listemesfavoris_rv);

        progressDoalog = new ProgressDialog(activity);
        progressDoalog.setMessage("Loading ...");

        progressDoalog.show();

        homeList = dataHelper.getFavoris();
        adapter = new MesFavorisAdapter(homeList,activity, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        listeAnnonce.setLayoutManager(mLayoutManager);
        listeAnnonce.setItemAnimator(new DefaultItemAnimator());
        listeAnnonce.setAdapter(adapter);

        progressDoalog.dismiss();


        return v;
    }

    public void deleteFavori(int position){
        Home home = homeList.get(position);
        homeList.remove(home);
        adapter.notifyDataSetChanged();
    }

}

package com.home.dz.home.annonce;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.home.dz.R;
import com.home.dz.bean.Home;
import com.home.dz.home.annonce.listannonces.ListeAnnonceFragment;
import com.home.dz.home.annonce.listannonces.MapsAnnoncesFragment;

import java.util.List;

public class ListeAnnoncesActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    private Fragment fragment;
    private List<Home> searchedHomes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_annonces);

        fragment = new ListeAnnonceFragment(this);
        loadFragment(fragment);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if(id == R.id.navigation_liste_annonce){
            fragment = new ListeAnnonceFragment(this);
        }

        if(id == R.id.navigation_map_annonce){
            fragment = new MapsAnnoncesFragment(this);
        }


        return loadFragment(fragment);
    }

    public List<Home> getSearchedHomes() {
        return searchedHomes;
    }

    public void setSearchedHomes(List<Home> searchedHomes) {
        this.searchedHomes = searchedHomes;
    }
}

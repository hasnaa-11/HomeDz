package com.home.dz.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.home.dz.R;
import com.home.dz.bean.Factory;
import com.home.dz.home.annonce.AddHomeActivity;
import com.home.dz.home.annonce.ListeAnnoncesFragment;
import com.home.dz.home.annonce.MesAnnonceFragment;
import com.home.dz.home.annonce.MesFavorisFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView email, user,initials;
    private Fragment fragment = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragment = new ListeAnnoncesFragment(this);
        replaceFragment(fragment);
        toolbar.setTitle("Annonces");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        View headerView = navigationView.getHeaderView(0);
        user = headerView.findViewById(R.id.user_header_tv);
        email = headerView.findViewById(R.id.email_header_tv);
        initials = headerView.findViewById(R.id.initials_header_tv);

        String sNom = Factory.getUser().getNom();
        String sPrenom = Factory.getUser().getPrenom();

        email.setText(Factory.getUser().getEmail());
        user.setText(sPrenom + " " + sNom);

        String initialNom = String.valueOf(sNom.charAt(0)).toUpperCase();
        String initialPrenom = String.valueOf(sPrenom.charAt(0)).toUpperCase();
        initials.setText(initialNom+initialPrenom);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_rech_annonce) {
            fragment = new ListeAnnoncesFragment(this);
            replaceFragment(fragment);
            // Handle the camera action
        } else if (id == R.id.nav_ajouter_annonce) {

            Intent ajouterAnnonce = new Intent(this, AddHomeActivity.class);
            startActivity(ajouterAnnonce);

        } else if (id == R.id.nav_mes_annonces) {
            fragment = new MesAnnonceFragment(this);
            replaceFragment(fragment);
        } else if (id == R.id.nav_mes_favoris) {
            fragment = new MesFavorisFragment(this);
            replaceFragment(fragment);
        } else if (id == R.id.nav_deconnecter) {
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment monFragment){
        if(monFragment != null){
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame,monFragment);
            transaction.commit();
        }
    }
}

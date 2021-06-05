package com.home.dz.home.annonce;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.dz.R;
import com.home.dz.bean.Home;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import java.util.ArrayList;
import java.util.List;

public class AfficherAnnonceActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private TextView titre, description, prix, adresse, surfcae, wilaya, email, phone, user;
    private ImageView photo, call;
    private Home home;
    private MapView mapView;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_afficher_annonce);

        Intent intent = getIntent();

        home = (Home) intent.getSerializableExtra("home");

        titre = findViewById(R.id.affi_titre_tv);
        description = findViewById(R.id.affi_desc_tv);
        prix = findViewById(R.id.affi_prix_tv);
        adresse = findViewById(R.id.affi_adr_tv);
        surfcae = findViewById(R.id.affi_surf_tv);
        wilaya = findViewById(R.id.affi_wilaya_tv);
        email = findViewById(R.id.affi_email_tv);
        phone = findViewById(R.id.affi_phone_tv);
        user = findViewById(R.id.affi_user_tv);
        photo = findViewById(R.id.affi_photo_img);
        call = findViewById(R.id.affi_call_img);

        mapView = findViewById(R.id.affi_carte_mv);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        call.setOnClickListener(this);

        titre.setText(home.getTitre());
        description.setText(home.getDescription());
        prix.setText(Long.toString(home.getPrix()));
        adresse.setText(home.getAdresse());
        wilaya.setText(home.getWilaya());
        surfcae.setText(Float.toString(home.getSurface()));
        Picasso.get().load(home.getUrlPhoto()).into(photo);
        email.setText(home.getUser().getEmail());
        phone.setText(home.getUser().getPhone());
        user.setText("Contacter" + home.getUser().getPrenom() + " " + home.getUser().getNom());




    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", home.getUser().getPhone(), null));
        startActivity(intent);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        Intent intent = getIntent();

        Home mHome = (Home) intent.getSerializableExtra("home");

        double homeLat = mHome.getLatitude();
        double homeLong = mHome.getLongitude();

        List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(homeLong, homeLat)));

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        AfficherAnnonceActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
                .withSource(new GeoJsonSource(SOURCE_ID,
                        FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                iconImage(ICON_ID),
                                iconAllowOverlap(true),
                                iconIgnorePlacement(true)
                        )
                ), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

// Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.


            }
        });

        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(new LatLng(homeLat,homeLong))
                        .zoom(14)
                        .build()), 4000);
    }
}

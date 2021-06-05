package com.home.dz.home.annonce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.home.dz.R;
import com.home.dz.bean.Factory;
import com.home.dz.bean.Home;
import com.home.dz.bean.User;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
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
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;
import com.mapbox.mapboxsdk.plugins.places.picker.model.PlacePickerOptions;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddHomeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {

    private String[] listeWilaya = {"Wilaya1", "Wilaya2", "Wilaya3"};
    private EditText titre, description, prix, surface;
    private Spinner wilaya;
    private ImageView photo, galerie;
    private Button valider;
    private TextView email, phone, adresse;
    private Uri imageUri;

    private String sTitre, sDescription, sAdresse, sWilaya;
    private long lPrix;
    private float fSurface;
    private DatabaseReference myRef;
    private ProgressDialog progressDoalog ;
    private MapView mapView;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private MapboxMap mapboxMap;
    private double homeLong;
    private double homeLat;
    private List<Feature> symbolLayerIconFeatureList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_add_home);

        titre = findViewById(R.id.anno_titre_et);
        description = findViewById(R.id.anno_desc_et);
        prix = findViewById(R.id.anno_prix_et);
        surface = findViewById(R.id.anno_surface_et);
        adresse = findViewById(R.id.anno_adresse_tv);
        wilaya = findViewById(R.id.anno_wilaya_sp);
        photo = findViewById(R.id.anno_photo_img);
        galerie = findViewById(R.id.anno_galerie_img);
        email = findViewById(R.id.anno_user_email_tv);
        phone = findViewById(R.id.anno_user_phone_tv);
        valider = findViewById(R.id.anno_valider_btn);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        myRef = FirebaseDatabase.getInstance().getReference();

        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Loading ...");

        surface.setText("0");
        prix.setText("0");
        User user = Factory.getUser();
        email.setText(user.getEmail());
        phone.setText(user.getPhone());

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,listeWilaya);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wilaya.setAdapter(adapter);

        galerie.setOnClickListener(this);
        valider.setOnClickListener(this);
        adresse.setOnClickListener(this);
        wilaya.setOnItemSelectedListener(this);
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

        int id = v.getId();

        if(id == R.id.anno_galerie_img){
            getPhotoFromGallery();
        }

        if(id == R.id.anno_valider_btn){
            ajouterAnnonce();
        }

        if(id == R.id.anno_adresse_tv){
            getAdresse();
        }

    }

    private void getAdresse(){
        PlaceOptions placeOptions = PlaceOptions.builder().build(PlaceOptions.MODE_CARDS);
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(getString(R.string.mapbox_access_token))
                .placeOptions(placeOptions)
                .build(this);
        startActivityForResult(intent, 200);
    }

    private void ajouterAnnonce() {

        sTitre = titre.getText().toString().trim();
        sDescription = description.getText().toString().trim();
        sAdresse = adresse.getText().toString().trim();

        fSurface = 0;
        lPrix = 0;

        if(!surface.getText().toString().trim().isEmpty()){
           fSurface = Float.parseFloat(surface.getText().toString().trim());
        }

        if(!prix.getText().toString().trim().isEmpty()){
           lPrix = Long.parseLong(prix.getText().toString().trim());
        }

        if(sTitre.isEmpty()){
            Toast.makeText(getApplicationContext(),"Le titre est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if(sDescription.isEmpty()){
            Toast.makeText(getApplicationContext(), "La description est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if(sAdresse.equals("Adresse")){
            Toast.makeText(getApplicationContext(), "L'adresse est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if(fSurface <= 0){
            Toast.makeText(getApplicationContext(), "La surface est invalide", Toast.LENGTH_LONG).show();
            return;
        }

        if(lPrix <= 0){
            Toast.makeText(getApplicationContext(), "Le prix est invalide", Toast.LENGTH_LONG).show();
            return;
        }

        if(imageUri == null){
            Toast.makeText(getApplicationContext(),"L'image est invalide", Toast.LENGTH_LONG).show();
            return;
        }


        if(imageUri != null)
        {

            progressDoalog.show();
            StorageReference image = FirebaseStorage.getInstance().getReference().child("Images").child(imageUri.getLastPathSegment()+ UUID.randomUUID().toString());
            image.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //here
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful());
                    Uri downloadUrl = urlTask.getResult();
                    final String sdownload_url = String.valueOf(downloadUrl);
                    String idHome = myRef.push().getKey();
                    Home home = new Home();
                    home.setIdHome(idHome);
                    home.setTitre(sTitre);
                    home.setDescription(sDescription);
                    home.setPrix(lPrix);
                    home.setAdresse(sAdresse);
                    home.setWilaya(sWilaya);
                    home.setSurface(fSurface);
                    home.setLongitude(homeLong);
                    home.setLatitude(homeLat);
                    home.setUrlPhoto(sdownload_url);
                    home.setUser(Factory.getUser());
                    myRef.child("home").child(idHome).setValue(home);
                    progressDoalog.dismiss();

                }
            });
        }

    }

    private void getPhotoFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 100){
            imageUri = data.getData();
            photo.setImageURI(imageUri);
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 200) {

            symbolLayerIconFeatureList.clear();
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            String place = selectedCarmenFeature.address();

            if(place == null){
                place = selectedCarmenFeature.placeName();
            }

            if(place == null){
                place = selectedCarmenFeature.text();
            }

            adresse.setText(place);

            homeLat = ((Point) selectedCarmenFeature.geometry()).latitude();
            homeLong = ((Point) selectedCarmenFeature.geometry()).longitude();

            symbolLayerIconFeatureList.add(Feature.fromGeometry(
                    Point.fromLngLat(homeLong, homeLat)));

            //mapView.getMapAsync(this);

            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                    ((Point) selectedCarmenFeature.geometry()).longitude()))
                            .zoom(14)
                            .build()), 4000);

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        sWilaya = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        this.mapboxMap = mapboxMap;
        symbolLayerIconFeatureList = new ArrayList<>();
        symbolLayerIconFeatureList.add(Feature.fromGeometry(
                Point.fromLngLat(-0.43765412653680935, 35.79738794207789)));

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                .withImage(ICON_ID, BitmapFactory.decodeResource(
                        AddHomeActivity.this.getResources(), R.drawable.mapbox_marker_icon_default))
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
    }

}

package com.home.dz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import com.home.dz.bean.Home;
import com.home.dz.bean.User;

import java.util.ArrayList;
import java.util.List;

public class FavorisDataHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favoris.db";

    public FavorisDataHelper(@Nullable Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String req = "create table home (idHome TEXT primary key, titre TEXT, description TEXT, adresse TEXT, wilaya TEXT, prix TEXT, surface TEXT, userNom TEXT, userPrenom, userEmail TEXT, userTelephone TEXT,image TEXT)";
        db.execSQL(req);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS home");
        onCreate(db);
    }

    public List<Home> getFavoris(){

        List<Home> favoris = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from home", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){

            String idHome = res.getString(res.getColumnIndex("idHome"));
            String titre = res.getString(res.getColumnIndex("titre"));
            String description = res.getString(res.getColumnIndex("description"));
            String adresse = res.getString(res.getColumnIndex("adresse"));
            String prix = res.getString(res.getColumnIndex("prix"));
            String surface = res.getString(res.getColumnIndex("surface"));
            String wilaya = res.getString(res.getColumnIndex("wilaya"));
            String userNom = res.getString(res.getColumnIndex("userNom"));
            String userPrenom = res.getString(res.getColumnIndex("userPrenom"));
            String userEmail = res.getString(res.getColumnIndex("userEmail"));
            String userTelephone = res.getString(res.getColumnIndex("userTelephone"));
            String image = res.getString(res.getColumnIndex("image"));

            Home home = new Home();
            home.setIdHome(idHome);
            home.setTitre(titre);
            home.setDescription(description);
            home.setAdresse(adresse);
            home.setPrix(Long.parseLong(prix));
            home.setSurface(Float.parseFloat(surface));
            home.setWilaya(wilaya);
            List<String> photos = new ArrayList<>();
            photos.add(image);
            home.setUrlPhotos(photos);

            User user = new User();
            user.setEmail(userEmail);
            user.setPrenom(userPrenom);
            user.setPhone(userTelephone);
            user.setNom(userNom);

            home.setUser(user);

            favoris.add(home);

            res.moveToNext();
        }

        return favoris;

    }

    public void ajouterFavori(Home home){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("idHome", home.getIdHome());
        contentValues.put("titre", home.getTitre());
        contentValues.put("description", home.getDescription());
        contentValues.put("adresse", home.getAdresse());
        contentValues.put("prix", Long.toString(home.getPrix()));
        contentValues.put("surface", Float.toHexString(home.getSurface()));
        contentValues.put("wilaya", home.getWilaya());
        contentValues.put("image", home.getUrlPhotos().get(0));
        contentValues.put("userNom", home.getUser().getNom());
        contentValues.put("userPrenom", home.getUser().getPrenom());
        contentValues.put("userEmail", home.getUser().getEmail());
        contentValues.put("userTelephone", home.getUser().getPhone());


        db.insert("home", null, contentValues);
    }

    public void deleteFavori(Home home){

        String idHome = home.getIdHome();

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("home", "idHome = ? ", new String[] { idHome });

    }
}
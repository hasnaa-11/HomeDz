package com.home.dz.user;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.home.dz.R;
import com.home.dz.bean.User;

public class NewAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nom, prenom, email, password, phone;
    private String sNom, sPrenom, sEmail, sPassword, sPhone;
    private Button inscription;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDoalog ;
    private DatabaseReference firebaseUser;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);


        nom = findViewById(R.id.nc_nom_et);
        prenom = findViewById(R.id.nc_prenom_et);
        email = findViewById(R.id.nc_email_et);
        password = findViewById(R.id.nc_password_et);
        phone = findViewById(R.id.nc_phone_et);

        inscription = findViewById(R.id.nc_btn);

        progressDoalog = new ProgressDialog(this);
        progressDoalog.setMessage("Loading ...");

        mAuth = FirebaseAuth.getInstance();
        firebaseUser =  FirebaseDatabase.getInstance().getReference();

        inscription.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        sNom = nom.getText().toString().trim();
        sPrenom = prenom.getText().toString().trim();
        sEmail = email.getText().toString().trim();
        sPassword = password.getText().toString().trim();
        sPhone = phone.getText().toString().trim();


        if(sNom.isEmpty()){
            Toast.makeText(getApplicationContext(), "Le Nom est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if(sPrenom.isEmpty()){
            Toast.makeText(getApplicationContext(), "Le Prénom est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if(sEmail.isEmpty()){
            Toast.makeText(getApplicationContext(), "L'Email est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if( sPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Le Mot de passe est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }

        if( sPhone.isEmpty()){
            Toast.makeText(getApplicationContext(), "Le Téléphone est obligatoire", Toast.LENGTH_LONG).show();
            return;
        }


        progressDoalog.show();

        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String idUser = mAuth.getUid();
                            user = new User();
                            user.setIdUser(idUser);
                            user.setNom(sNom);
                            user.setPrenom(sPrenom);
                            user.setEmail(sEmail);
                            user.setPassword(sPassword);
                            user.setPhone(sPhone);
                            //firebaseUser.child(idUser).setValue(user);
                            firebaseUser.child("user").child(idUser).setValue(user);
                            progressDoalog.dismiss();
                            finish();
                        } else {
                            progressDoalog.dismiss();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}

package com.home.dz.user;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.home.dz.R;
import com.home.dz.bean.Factory;
import com.home.dz.bean.User;
import com.home.dz.home.HomeActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private Button login;
    private TextView inscriptionTV;
    private Intent inscriptionIntent, homeIntent;
    private String sEmail, sPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = findViewById(R.id.email_login_et);
        password = findViewById(R.id.password_login_et);
        login = findViewById(R.id.login_btn);
        inscriptionTV = findViewById(R.id.insc_login_tv);

        inscriptionIntent = new Intent(this, NewAccountActivity.class);
        homeIntent = new Intent(this, HomeActivity.class);

        mAuth = FirebaseAuth.getInstance();

        login.setOnClickListener(this);
        inscriptionTV.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();


        if(id == R.id.insc_login_tv){
            startActivity(inscriptionIntent);
        }

        if(id == R.id.login_btn){
            homeActivity();
        }

    }

    private void homeActivity() {

        sEmail = email.getText().toString().trim();
        sPassword = password.getText().toString().trim();

        if(sEmail.isEmpty()){
            Toast.makeText(getApplicationContext(), "Le champ Email est obligatoir", Toast.LENGTH_LONG).show();
            return;
        }

        if(sPassword.isEmpty()){
            Toast.makeText(getApplicationContext(), "Le champ Mot de passe est obligatoir", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            getCurrentUser();
                        } else {
                            Toast.makeText(getApplicationContext(), "Email ou Mot de passe invalide", Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }

    private void getCurrentUser(){

        String idUser = mAuth.getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("user").child(idUser);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String idUser = dataSnapshot.child("idUser").getValue().toString();
                String nom = dataSnapshot.child("nom").getValue().toString();
                String prenom = dataSnapshot.child("prenom").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String password = dataSnapshot.child("password").getValue().toString();
                String phone = dataSnapshot.child("phone").getValue().toString();

                User user = new User();
                user.setIdUser(idUser);
                user.setNom(nom);
                user.setPrenom(prenom);
                user.setEmail(email);
                user.setPhone(phone);
                user.setPassword(password);

                Factory.setUser(user);

                startActivity(homeIntent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}

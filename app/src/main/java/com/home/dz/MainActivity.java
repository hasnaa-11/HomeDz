package com.home.dz;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.dz.user.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private TextView logoTV;
    private ImageView logoImg;
    private Intent loginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logoImg = findViewById(R.id.logo_img);
        logoTV = findViewById(R.id.logo_tv);
        loginIntent = new Intent(this, LoginActivity.class);

        Animation annimation = AnimationUtils.loadAnimation(this, R.anim.animation);

        logoTV.startAnimation(annimation);
        logoImg.startAnimation(annimation);

        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    startActivity(loginIntent);
                    finish();
                }
            }
        };

        timer.start();
    }
}

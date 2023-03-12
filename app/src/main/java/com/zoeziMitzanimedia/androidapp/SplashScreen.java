package com.zoeziMitzanimedia.androidapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    ZoeziPreferenceManager preferenceManager;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        preferenceManager = new ZoeziPreferenceManager(this);
        intent = new Intent(getApplicationContext(), MainActivity.class);

        if (preferenceManager.isFirstVisit()){
            preferenceManager.setFirstVisit();
            intent = new Intent(getApplicationContext(), TransitionActivity.class);
        }

        new Handler().postDelayed(() -> {
            startActivity(intent);
            overridePendingTransition(R.anim.right,R.anim.left);
            finish();
        },3000);
    }
}

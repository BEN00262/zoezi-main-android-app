package com.zoeziMitzanimedia.androidapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class NoInternet extends AppCompatActivity {
    CheckInternet checkInternet = new CheckInternet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
    }

    private class CheckInternet extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DetectInternetConnection.checkInternetConnection(getApplicationContext())) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.right, R.anim.left);
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(checkInternet, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(checkInternet);
    }
}


package com.zoeziMitzanimedia.androidapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ZoeziPreferenceManager extends ContextWrapper {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @SuppressLint("CommitPrefEdits")
    public ZoeziPreferenceManager(Context base) {
        super(base);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferencesEditor = sharedPreferences.edit();
    }

    public String getPreviousURL(){
        return sharedPreferences.getString("previousURL","");
    }

    public void setPreviousURL(String url){
        sharedPreferencesEditor.putString("previousURL",url);
        sharedPreferencesEditor.commit();
    }

    public void setFirstVisit() {
        sharedPreferencesEditor.putBoolean("isFirstVisit",false);
        sharedPreferencesEditor.commit();
    }

    public boolean isFirstVisit() {
        return sharedPreferences.getBoolean("isFirstVisit",  true);
    }

    public int isVerified(){
        // check if the verification is there if not set it to ZoeziStatus.INITIAL
        return sharedPreferences.getInt("isVerified",ZoeziStatus.INITIAL.ordinal());
    }

    public void setVerification(){
        sharedPreferencesEditor.putInt("isVerified",ZoeziStatus.NOT_VERIFIED.ordinal());
        sharedPreferencesEditor.commit();
    }

    public void verify(){
        sharedPreferencesEditor.putInt("isVerified",ZoeziStatus.VERIFIED.ordinal());
        sharedPreferencesEditor.commit();
    }
}


package com.example.dimonn445.museum;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by godun on 26.04.2016.
 */
public class Prefs {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public Prefs(Context context) {
        prefs = context.getSharedPreferences("SAVED_EXHIBITS", Context.MODE_PRIVATE);
    }

    public String getExhPref() {
        return prefs.getString("SAVED_EXHIBITS", null);
    }

    public void setExhPref(String s) {
        editor = prefs.edit();
        editor.putString("SAVED_EXHIBITS", s);
        editor.apply();
//        editor.commit();
    }

    public boolean containsPref(String s) {
        return prefs.contains(s);
    }
}

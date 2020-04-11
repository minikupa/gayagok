package com.gayagok.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceUtil {

    private final SharedPreferences prefs;

    public SharedPreferenceUtil(Context context){
        prefs = context.getSharedPreferences("pref", MODE_PRIVATE);
    }

    public SharedPreferenceUtil(Context context, String pre){
        prefs = context.getSharedPreferences(pre, MODE_PRIVATE);
    }

    public SharedPreferenceUtil putString(String key, String value) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.apply();

        return this;
    }

    public String getString(String key, String defaultKey) {
        return prefs.getString(key, defaultKey);
    }

    void remove(String key){
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove(key);
        editor.apply();

    }

}

package com.example.tejasvedantham.pttmobile2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** ref: https://abedxh.medium.com/user-session-in-android-4d8f937c966a */
public class SessionManager {

    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setUserId(String userId) {
        prefs.edit().putString("userId", userId).commit();
    }

    public String getUserId() {
        String userId = prefs.getString("userId","");
        return userId;
    }
}

package com.saralvigyan.saralvigyan.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Mangesh kokare on 13-11-2017.
 */

public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";

    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    private SharedPreferences pref;
    private Editor editor;
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.commit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public String getMobileNumber() {
        return pref.getString(KEY_PHONE_NUMBER, null);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_PHONE_NUMBER, mobileNumber);
        editor.commit();
    }

    public void setLogin() {

        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }
    
    /*public void setLogin(String userType, String phone, boolean isset) {

        editor.putString(KEY_USER_TYPE, userType);
        editor.putString(KEY_PHONE, phone);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }*/

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("mobile", pref.getString(KEY_PHONE, null));
        return profile;
    }
}

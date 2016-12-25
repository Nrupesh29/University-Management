package com.nrupeshpatel.university.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

import com.nrupeshpatel.university.FacultyActivity;
import com.nrupeshpatel.university.HODActivity;
import com.nrupeshpatel.university.LoginActivity;
import com.nrupeshpatel.university.MainActivity;
import com.nrupeshpatel.university.StudentActivity;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "ITMUniverseLogin";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";

    // Email address (make variable public to access from outside)
    public static final String KEY_USERTYPE = "user_type";

    public static final String KEY_DEPARTMENT = "department";
    public static final String KEY_SEMESTER = "semester";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String username, String user_type, String department, String semester) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_USERNAME, username);

        // Storing email in pref
        editor.putString(KEY_USERTYPE, user_type);

        editor.putString(KEY_DEPARTMENT, department);
        editor.putString(KEY_SEMESTER, semester);

        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));

        // user email id
        user.put(KEY_USERTYPE, pref.getString(KEY_USERTYPE, null));

        user.put(KEY_DEPARTMENT, pref.getString(KEY_DEPARTMENT, null));
        user.put(KEY_SEMESTER, pref.getString(KEY_SEMESTER, null));

        // return user
        return user;
    }

    public void checkLogin() {
        // Check login status
        if (this.isLoggedIn()) {

            Intent i = null;
            // user is not logged in redirect him to Login Activity

            if (pref.getString(KEY_USERTYPE, null).equals("Admin")) {
                i = new Intent(_context, MainActivity.class);
            } else if (pref.getString(KEY_USERTYPE, null).equals("Head of department")) {
                i = new Intent(_context, HODActivity.class);
            } else if (pref.getString(KEY_USERTYPE, null).equals("Faculty")) {
                i = new Intent(_context, FacultyActivity.class);
            } else if (pref.getString(KEY_USERTYPE, null).equals("Student")) {
                i = new Intent(_context, StudentActivity.class);
            }

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);

        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
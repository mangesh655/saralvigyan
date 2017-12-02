package com.saralvigyan.saralvigyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.saralvigyan.saralvigyan.R;
import com.saralvigyan.saralvigyan.helper.SessionManager;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SessionManager session;
    private TextView name, email, mobile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        btnLogout = findViewById(R.id.btn_logout);
        // enabling toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        session = new SessionManager(getApplicationContext());


        // Checking if user session
        // if not logged in, take user to sms screen
        if (!session.isLoggedIn()) {
            logout();
        }

        // Displaying user information from shared preferences
        HashMap<String, String> profile = session.getUserDetails();
        name.setText("Name: " + profile.get("name"));
        email.setText("Email: " + profile.get("email"));
        mobile.setText("Mobile: " + profile.get("mobile"));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
    }

    /**
     * Logging out user
     * will clear all user shared preferences and navigate to
     * sms activation screen
     */
    private void logout() {
        session.clearSession();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);

        finish();
    }
}
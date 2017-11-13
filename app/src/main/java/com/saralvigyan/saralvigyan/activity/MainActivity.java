package com.saralvigyan.saralvigyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.saralvigyan.saralvigyan.R;
import com.saralvigyan.saralvigyan.helper.SQLiteHandler;
import com.saralvigyan.saralvigyan.helper.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;

    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        txtName = (TextView) findViewById(R.id.name);
        //      txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = findViewById(R.id.btn_student_logout);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        Log.e("Name : ", name);
        Log.e("email : ", email);

        // Displaying the user details on the screen
        // txtName.setText(name);
        // txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

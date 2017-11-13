package com.saralvigyan.saralvigyan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.saralvigyan.saralvigyan.R;
import com.saralvigyan.saralvigyan.fragment.registrationFragment.StudentRegistrationFragment;
import com.saralvigyan.saralvigyan.fragment.registrationFragment.TeacherRegistrationFragment;

public class RegistrationActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent registration = getIntent();
        user = registration.getStringExtra("USER");


        if (user.equals("STUDENT")) {

            Log.e("Actor", "student");

            StudentRegistrationFragment studentRegistrationFragment = new StudentRegistrationFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, studentRegistrationFragment)
                    .commit();
        } else if (user.equals("TEACHER")) {

            TeacherRegistrationFragment teacherRegistrationFragment = new TeacherRegistrationFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_layout, teacherRegistrationFragment)
                    .commit();
        }
    }

}

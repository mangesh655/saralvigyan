package com.saralvigyan.saralvigyan.utils;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.regex.Pattern;

/**
 * Created by Mangesh kokare on 12-11-2017.
 */

public class Validation {

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private static final String NAME_PATTERN = "((?=.*[a-z])(?=.*[A-Z])(?=.*[ ]).{1,30})";
    private static final String PHONE_PATTERN = "^[0-9]{10}$";

    private Activity activity;

    public Validation(Activity activity) {
        this.activity = activity;
    }

    public boolean validateName(TextInputLayout inputLayout, EditText editText) {

        String name = editText.getText().toString().trim();

        if (!Pattern.compile(NAME_PATTERN).matcher(name).matches()) {
            inputLayout.setError("Enter valid Name");
            requestFocus(editText);
            return false;
        } else inputLayout.setErrorEnabled(false);

        return true;
    }


    public boolean validateEmail(TextInputLayout inputLayout, EditText editText) {
        String email = editText.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputLayout.setError("Enter valid email address");
            requestFocus(editText);
            return false;
        } else inputLayout.setErrorEnabled(false);

        return true;
    }

    public boolean validatePhone(TextInputLayout inputLayout, EditText editText) {
        String phone = editText.getText().toString().trim();

        if (!Pattern.compile(PHONE_PATTERN).matcher(phone).matches()) {
            inputLayout.setError("Enter valid mobile number");
            requestFocus(editText);
            return false;
        } else inputLayout.setErrorEnabled(false);

        return true;
    }

    public boolean validatePassword(TextInputLayout inputLayout, EditText editText) {

        String password = editText.getText().toString().trim();

        if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()) {
            inputLayout.setError("Password must Contain : " +
                    "\n 1. At least One Uppercase letter" +
                    "\n 2. At least One lowercase letter" +
                    "\n 3. At least One digit" +
                    "\n 4. At least One of the special" +
                    "\n    characters : @#$%" +
                    "\n 5. length : 6-20 characters");
            requestFocus(editText);
            return false;
        } else inputLayout.setErrorEnabled(false);

        return true;
    }

    public boolean validateConfirmPassword(TextInputLayout inputLayout, EditText etPassword, EditText etConfirmPassword) {
        if (etConfirmPassword.getText().toString().trim().isEmpty()) {
            inputLayout.setError("Enter password again");
            requestFocus(etConfirmPassword);
            return false;
        } else if (!etConfirmPassword.getText().toString().trim().equals(etPassword.getText().toString().trim())) {
            inputLayout.setError("Password do not match");
            requestFocus(etConfirmPassword);
            return false;
        } else inputLayout.setErrorEnabled(false);

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

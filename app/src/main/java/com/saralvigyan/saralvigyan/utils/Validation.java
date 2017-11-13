package com.saralvigyan.saralvigyan.utils;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mangesh kokare on 12-11-2017.
 */

public class Validation {

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
    private Activity activity;
    private Matcher matcher;
    private Pattern pattern;

    public Validation(Activity activity) {
        this.activity = activity;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean validateEmail(EditText editText) {
        String email = editText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            editText.setError("Enter valid email address");
            requestFocus(editText);
            return false;
        }

        return true;
    }

    public boolean validatePassword(EditText editText) {

        String password = editText.getText().toString().trim();

        if (password.isEmpty()) {
            editText.setError("Password cannot be empty");
            requestFocus(editText);
            return false;
        } else if (!Pattern.compile(PASSWORD_PATTERN).matcher(password).matches()) {
            editText.setError("Password must Contain : " +
                    "\n 1. At least One Uppercase letter" +
                    "\n 2. At least One lowercase letter" +
                    "\n 3. At least One digit" +
                    "\n 4. At least One of the special characters : @#$%" +
                    "\n 5. length : 6-20 characters");
            requestFocus(editText);
            return false;
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    public boolean validateConfirmPassword(EditText etPassword, EditText etConfirmPassword) {
        if (etConfirmPassword.getText().toString().trim().isEmpty()) {
            etConfirmPassword.setError("Enter password again");
            requestFocus(etConfirmPassword);
            return false;
        } else if (!etConfirmPassword.getText().toString().trim().equals(etPassword.getText().toString().trim())) {
            etConfirmPassword.setError("Password do not match");
            requestFocus(etConfirmPassword);
            return false;
        }

        return true;
    }
}

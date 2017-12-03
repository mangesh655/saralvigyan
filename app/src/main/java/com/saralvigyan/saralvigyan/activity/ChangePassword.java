package com.saralvigyan.saralvigyan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saralvigyan.saralvigyan.R;
import com.saralvigyan.saralvigyan.app.AppConfig;
import com.saralvigyan.saralvigyan.app.AppController;
import com.saralvigyan.saralvigyan.app.ConnectivityReceiver;
import com.saralvigyan.saralvigyan.helper.SQLiteHandler;
import com.saralvigyan.saralvigyan.helper.SessionManager;
import com.saralvigyan.saralvigyan.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = ChangePassword.class.getName();

    private SQLiteHandler db;
    private SessionManager session;

    private Validation validation;
    private ProgressDialog pDialog;
    private Intent intent;

    private TextInputLayout inputLayoutNewPassword, inputLayoutConfirmNewPassword;
    private EditText etNewPassword, etConfirmNewPassword;
    private Button btnSubmitNewPassword;

    private String strPhone, strNewPassword, strConfirmNewPassword;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        intent = getIntent();
        strPhone = intent.getStringExtra("phone");

        linearLayout = findViewById(R.id.linear_layout);
        inputLayoutNewPassword = findViewById(R.id.input_layout_new_password);
        inputLayoutConfirmNewPassword = findViewById(R.id.input_layout_new_confirm_password);

        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_new_confirm_password);

        btnSubmitNewPassword = findViewById(R.id.btn_submit_new_password);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        validation = new Validation(ChangePassword.this);

        // SQLite database handler
        db = new SQLiteHandler(ChangePassword.this);

        // Session manager
        session = new SessionManager(ChangePassword.this);

        //Check Internet Connection.
        checkConnection();

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            intent = new Intent(ChangePassword.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Edittext Validation
        etNewPassword.addTextChangedListener(new MyTextWatcher(etNewPassword));
        etConfirmNewPassword.addTextChangedListener(new MyTextWatcher(etConfirmNewPassword));

        //button click listeners
        btnSubmitNewPassword.setOnClickListener(this);
    }

    /**
     * Function to update password
     **/

    private void updatePassword(final String phone, final String newPassword) {
        // Tag used to cancel the request
        String tag_string_req = "req_update_password";

        pDialog.setMessage("Updating...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_PASSWORD, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update password Response: " + response.toString());
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");

                    if (!error) {
                        Intent intent = new Intent(ChangePassword.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Updating password Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("new_password", newPassword);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // Method to manually check connection status
    private boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
        return isConnected;
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;

        if (!isConnected) {
            message = "Sorry! Not connected to internet";
            color = Color.RED;

            Snackbar snackbar = Snackbar.make(linearLayout, message, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(color);
            snackbar.show();
        }
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_submit_new_password:
                if (checkConnection()) {
                    if (validation.validatePassword(inputLayoutNewPassword, etNewPassword)
                            && validation.validateConfirmPassword(inputLayoutConfirmNewPassword,
                            etNewPassword, etConfirmNewPassword)) {

                        //validation Successful.
                        strNewPassword = etNewPassword.getText().toString().trim();
                        strConfirmNewPassword = etConfirmNewPassword.getText().toString().trim();

                        //update password
                        updatePassword(strPhone, strNewPassword);

                    } else {

                        //validation Failed. Prompt user to enter credentials
                        Toast.makeText(ChangePassword.this,
                                "Please enter valid credentials!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    showSnack(checkConnection());
                }
                break;
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_new_password:
                    validation.validatePassword(inputLayoutNewPassword, etNewPassword);
                    break;
                case R.id.et_new_confirm_password:
                    validation.validateConfirmPassword(inputLayoutConfirmNewPassword,
                            etNewPassword, etConfirmNewPassword);
                    break;
            }
        }
    }
}

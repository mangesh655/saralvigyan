package com.saralvigyan.saralvigyan.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.saralvigyan.saralvigyan.receiver.MyResultReceiver;
import com.saralvigyan.saralvigyan.service.HttpService;
import com.saralvigyan.saralvigyan.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener, View.OnClickListener, MyResultReceiver.Receiver {

    private static final String TAG = LoginActivity.class.getName();

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    private MyResultReceiver myResultReceiver;

    private SQLiteHandler db;
    private SessionManager session;

    private Validation validation;
    private ProgressDialog pDialog;

    private LinearLayout linearLayout;
    private TextInputLayout inputLayoutPhone, inputLayoutPassword;
    private EditText etPhone, etPassword, etOtp;
    private Button btnSignIn, btnSignUpLink, btnForgotPassword, btnVerifyOtp;
    private TextView tvResendOtp, tvEditMobile;

    private String strPhone, strPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialize all the views
        initializeViews();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        validation = new Validation(LoginActivity.this);

        // SQLite database handler
        db = new SQLiteHandler(LoginActivity.this);

        // Session manager
        session = new SessionManager(LoginActivity.this);

        //Receiving wrong otp result flag
        myResultReceiver = new MyResultReceiver(new Handler());
        myResultReceiver.setReceiver(this);

        //Check Internet Connection.
        checkConnection();

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //Edittext Validation
        etPhone.addTextChangedListener(new MyTextWatcher(etPhone));
        etPassword.addTextChangedListener(new MyTextWatcher(etPassword));

        //button click listeners
        btnSignIn.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        btnSignUpLink.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);

        tvEditMobile.setOnClickListener(this);
        tvResendOtp.setOnClickListener(this);

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (session.isWaitingForSms()) {
            viewPager.setCurrentItem(1);
            //Starting timer to resend otp
            startSendOtpTimer();
        }
    }

    private void startSendOtpTimer() {
        new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOtp.setClickable(false);
                tvResendOtp.setText("seconds remaining: " + millisUntilFinished / 1000);

                //hiding edit mobile text button
                tvEditMobile.setVisibility(View.GONE);
            }

            public void onFinish() {
                tvResendOtp.setClickable(true);
                tvResendOtp.setText("Resend OTP");

                //Showing edit mobile text button
                tvEditMobile.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    /**
     * Method to Initialize all the views from
     * activity_register.xml
     */

    private void initializeViews() {

        inputLayoutPhone = findViewById(R.id.input_layout_phone);
        inputLayoutPassword = findViewById(R.id.input_layout_password);


        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etOtp = findViewById(R.id.et_otp);

        btnSignIn = findViewById(R.id.btn_sign_in);
        btnForgotPassword = findViewById(R.id.btn_forgot_password);
        btnSignUpLink = findViewById(R.id.btn_sign_up_link);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);

        tvResendOtp = findViewById(R.id.tv_resend_otp);
        tvEditMobile = findViewById(R.id.tv_edit_mobile);

        viewPager = findViewById(R.id.viewPagerVertical);
        linearLayout = findViewById(R.id.layout_login);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_sign_in:
                if (checkConnection()) {
                    if (validation.validatePhone(inputLayoutPhone, etPhone) && validation.validatePassword(inputLayoutPassword, etPassword)) {

                        //validation Successful.
                        strPhone = etPhone.getText().toString().trim();
                        strPassword = etPassword.getText().toString().trim();

                        //login user
                        checkLogin(strPhone, strPassword);
                    } else {

                        //validation Failed. Prompt user to enter credentials
                        Toast.makeText(LoginActivity.this,
                                "Please enter valid credentials!", Toast.LENGTH_LONG)
                                .show();
                    }
                } else {
                    showSnack(checkConnection());
                }

                break;
            case R.id.btn_forgot_password:
                forgotPassword();
                break;
            case R.id.btn_sign_up_link:
                Intent singUpIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(singUpIntent);
                finish();
                break;
            case R.id.btn_verify_otp:
                verifyOtp();
                break;
            case R.id.tv_edit_mobile:
                viewPager.setCurrentItem(0);
                session.setIsWaitingForSms(false);
                break;
            case R.id.tv_resend_otp:
                sendOtp(session.getMobileNumber());
                break;
        }
    }

    /**
     * Method to reset password
     */
    private void forgotPassword() {

        if (checkConnection()) {
            if (validation.validatePhone(inputLayoutPhone, etPhone)) {

                strPhone = etPhone.getText().toString().trim();
                session.setMobileNumber(strPhone);

                // boolean flag saying device is waiting for sms
                //session.setIsWaitingForSms(true);
                // moving the screen to next pager item i.e otp screen
                //viewPager.setCurrentItem(1);
                //Starting resend otp timer
                //startSendOtpTimer();

                //call function to send forgot password request.
                sendOtp(strPhone);

            } else {
                Toast.makeText(getApplicationContext(), "Please provide your mobile number to reset password", Toast.LENGTH_LONG);
            }
        } else {
            showSnack(checkConnection());
        }
    }

    /**
     * function to verify login details in mysql db
     */

    private void checkLogin(final String phone, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging you in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin();

                        JSONObject user = jObj.getJSONObject("user");
                        String userType = user.getString("user_type");
                        String phone = user.getString("phone");
                        String created_at = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(phone, userType, created_at);
                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(LoginActivity.this,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(LoginActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * phone, password) to register url
     */

    private void sendOtp(final String phone) {
        // Tag used to cancel the request
        String tag_string_req = "req_send_otp";

        pDialog.setMessage("Sending OTP ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sending OTP Response: " + response.toString());
                hideDialog();

                try {

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {

                        // boolean flag saying device is waiting for sms
                        session.setIsWaitingForSms(true);
                        // moving the screen to next pager item i.e otp screen
                        viewPager.setCurrentItem(1);
                        //Starting send otp timer
                        startSendOtpTimer();

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();


                    } else {
                        //Error occurred in sending otp.
                        Toast.makeText(getApplicationContext(),
                                message, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Resending OTP Error: " + error.getMessage());
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

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp() {
        String otp = etOtp.getText().toString().trim();
        //Log.e("VerifyOTP", session.getMobileNumber().toString());
        if (!otp.isEmpty() && otp.length() == 6) {
            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
            grapprIntent.putExtra("phone", session.getMobileNumber());
            grapprIntent.putExtra("otp", otp);
            grapprIntent.putExtra("operation", "FORGOT_PASSWORD");
            grapprIntent.putExtra("ResultReceiver", myResultReceiver);
            startService(grapprIntent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please enter the valid OTP", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        AppController.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {

        //Wrong OTP.
        //Starting the Login Activity again
        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

        // boolean flag saying device is waiting for sms
        session.setIsWaitingForSms(true);

        // moving the screen to next pager item i.e otp screen
        viewPager.setCurrentItem(1);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        viewPager.setCurrentItem(0);
        session.setIsWaitingForSms(false);
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
                case R.id.et_phone:
                    validation.validatePhone(inputLayoutPhone, etPhone);
                    break;
                case R.id.et_password:
                    validation.validatePassword(inputLayoutPassword, etPassword);
                    break;
            }
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_login;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return findViewById(resId);
        }
    }
}

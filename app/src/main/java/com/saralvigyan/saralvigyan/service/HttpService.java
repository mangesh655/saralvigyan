package com.saralvigyan.saralvigyan.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.saralvigyan.saralvigyan.activity.ChangePassword;
import com.saralvigyan.saralvigyan.activity.MainActivity;
import com.saralvigyan.saralvigyan.app.AppConfig;
import com.saralvigyan.saralvigyan.app.AppController;
import com.saralvigyan.saralvigyan.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import com.saralvigyan.saralvigyan.activity.MainActivity;


/**
 * Created by Mangesh kokare on 28-11-2017.
 */

public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();

    private ResultReceiver rec;

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String phone = intent.getStringExtra("phone");
            String otp = intent.getStringExtra("otp");
            String operation = intent.getStringExtra("operation");
            rec = intent.getParcelableExtra("ResultReceiver");

            verifyOtp(phone, otp, operation);
        }
    }

    /**
     * Posting the OTP to server
     * 1. activating the user
     * 2. forgot password verification
     *
     * @param otp otp received in the SMS
     */
    private void verifyOtp(final String phone, final String otp, final String operation) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_VERIFY_OTP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                try {

                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {

                        if (operation.equals("REGISTER")) {

                            SessionManager sessionManager = new SessionManager(getApplicationContext());
                            sessionManager.setLogin();

                            Intent intent = new Intent(HttpService.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                        } else {

                            Intent intent = new Intent(HttpService.this, ChangePassword.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("phone", phone);
                            startActivity(intent);
                        }

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else {

                        Log.d(TAG, "sending data back to activity");

                        Bundle b = new Bundle();
                        rec.send(0, b);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phone", phone);
                params.put("otp", otp);
                params.put("operation", operation);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }
}

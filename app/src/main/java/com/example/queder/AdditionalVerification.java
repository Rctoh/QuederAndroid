package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdditionalVerification extends AppCompatActivity {

    EditText AVpasswordET, AVnricET;
    MaterialButton AVcontinueButtonMB;
    SharedPreferences sp;
    ProgressBar AVprogressBarPB;

    String userId, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_verification);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        AVpasswordET = findViewById(R.id.AVpasswordET);
        AVnricET = findViewById(R.id.AVnricET);
        AVcontinueButtonMB = findViewById(R.id.AVcontinueButtonMB);
        AVprogressBarPB = findViewById(R.id.AVprogressBarPB);
        sp = getSharedPreferences("login", MODE_PRIVATE);

        Bundle userDetails = getIntent().getExtras();

        userId = userDetails.getString("userId");
        email = userDetails.getString("email");


        AVcontinueButtonMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPasswordNRIC(userId, AVpasswordET.getText().toString(), AVnricET.getText().toString());
                AVcontinueButtonMB.setVisibility(View.INVISIBLE);
                AVprogressBarPB.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkPasswordNRIC(String userId, String enteredPassword, String enteredNRIC) {

        String url = "https://queder-a59fe69a45d0.herokuapp.com/account/checkPasswordNRIC";
        RequestQueue queue = Volley.newRequestQueue(AdditionalVerification.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("Success")) { //If password and NRIC is correct,
                        Intent VerifyOTP = new Intent(getApplicationContext(), VerifyOTP.class);
                        VerifyOTP.putExtra("OTPidFromUser", jsonObject.getString("OTPid"));
                        VerifyOTP.putExtra("accessType", "login");
                        VerifyOTP.putExtra("userId", userId);
                        VerifyOTP.putExtra("password", enteredPassword);
                        VerifyOTP.putExtra("nric", enteredNRIC);
                        VerifyOTP.putExtra("email", email);
                        startActivity(VerifyOTP);
                        finish();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AVprogressBarPB.setVisibility(View.INVISIBLE);
                                AVcontinueButtonMB.setVisibility(View.VISIBLE);
                            }
                        }, 2500);
                    } else if (jsonObject.getString("message").equals("Password or NRIC Incorrect!")){
                        Toast.makeText(AdditionalVerification.this, "Password or NRIC Incorrect!", Toast.LENGTH_SHORT).show();
                        AVprogressBarPB.setVisibility(View.INVISIBLE);
                        AVcontinueButtonMB.setVisibility(View.VISIBLE);
                        AVcontinueButtonMB.setClickable(true);
                        AVcontinueButtonMB.setText("Continue");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    AVcontinueButtonMB.setClickable(true);
                    AVcontinueButtonMB.setText("Continue");
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AVprogressBarPB.setVisibility(View.INVISIBLE);
                AVcontinueButtonMB.setVisibility(View.VISIBLE);
                AVcontinueButtonMB.setClickable(true);
                AVcontinueButtonMB.setText("Continue");
                Toast.makeText(AdditionalVerification.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("userId", userId);
                params.put("enteredPassword", enteredPassword);
                params.put("enteredNRIC", enteredNRIC);
                params.put("email", email);
                return params;

            }
        };
        queue.add(request);
    }
}
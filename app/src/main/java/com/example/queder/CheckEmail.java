package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

public class CheckEmail extends AppCompatActivity {

    EditText CEemailET;
    MaterialButton CEcontinueButtonMB;
    SharedPreferences sp;
    ProgressBar CEprogressBarPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_email);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        CEemailET = findViewById(R.id.CEemailET);
        CEcontinueButtonMB = findViewById(R.id.CEcontinueButtonMB);
        CEprogressBarPB = findViewById(R.id.CEprogressBarPB);
        sp = getSharedPreferences("login", MODE_PRIVATE);

        CEcontinueButtonMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailInput = CEemailET.getText().toString();

                if(emailInput.contains("@")) {
                    checkEmail(emailInput);
                    CEcontinueButtonMB.setVisibility(View.INVISIBLE);
                    CEprogressBarPB.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(CheckEmail.this, "Enter a valid email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkEmail(String email) {

        String url = "https://queder-a59fe69a45d0.herokuapp.com/account/checkEmail";
        RequestQueue queue = Volley.newRequestQueue(CheckEmail.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String messageString = jsonObject.getString("message");

                    // If email exists in DB, which means is logging in
                    if (messageString.equals("oldUser")) {
                        Intent AdditionalVerification = new Intent(getApplicationContext(), AdditionalVerification.class);
                        AdditionalVerification.putExtra("userId", jsonObject.getString("userId"));
                        AdditionalVerification.putExtra("email", email);
                        startActivity(AdditionalVerification);
                        finish();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CEprogressBarPB.setVisibility(View.INVISIBLE);
                                CEcontinueButtonMB.setVisibility(View.VISIBLE);
                            }
                        }, 2500);
                    } else if (messageString.equals("newUser")){ //If dh email in DB, means register
                        toCreateAccount();
                    } else {
                        CEprogressBarPB.setVisibility(View.INVISIBLE);
                        CEcontinueButtonMB.setVisibility(View.VISIBLE);
                        CEcontinueButtonMB.setClickable(true);
                        CEcontinueButtonMB.setText("Continue");
                        Toast.makeText(CheckEmail.this, messageString, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CEcontinueButtonMB.setClickable(true);
                    CEcontinueButtonMB.setText("Continue");
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CEprogressBarPB.setVisibility(View.INVISIBLE);
                CEcontinueButtonMB.setVisibility(View.VISIBLE);
                CEcontinueButtonMB.setClickable(true);
                CEcontinueButtonMB.setText("Continue");
                Toast.makeText(CheckEmail.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;

            }
        };
        queue.add(request);
    }

    private void toCreateAccount() {
        Intent createAccount = new Intent(this, CreateAccount.class);
        createAccount.putExtra("email", CEemailET.getText().toString());
        startActivity(createAccount);
        finish();
    }

}
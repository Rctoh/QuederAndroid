package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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


public class CreateAccount extends AppCompatActivity {

    EditText CAfirstNameET, CAfullNameET, CAnricET, CAemailET, CAphoneNumberET, CApasswordET, CArePasswordET;
    Spinner CAphoneNumberExtS;
    String Email, accessType;
    MaterialButton CAcreateButtonMB;
    ImageView CAshowPasswordIV, CAshowRePasswordIV;
    ProgressBar CAprogressBarPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Bundle bundle = getIntent().getExtras();
        Email = bundle.getString("email");

        CAfirstNameET = findViewById(R.id.CAfirstNameET);
        CAfullNameET = findViewById(R.id.CAfullNameET);
        CAnricET = findViewById(R.id.CAnricET);
        CAemailET = findViewById(R.id.CAemailET);
        CAphoneNumberET = findViewById(R.id.CAphoneNumberET);
        CApasswordET = findViewById(R.id.CApasswordET);
        CArePasswordET = findViewById(R.id.CArePasswordET);
        CAphoneNumberExtS = findViewById(R.id.CAphoneNumberExtS);
        CAcreateButtonMB = findViewById(R.id.CAcreateButtonMB);
        CAshowPasswordIV = findViewById(R.id.CAshowPasswordIV);
        CAshowRePasswordIV = findViewById(R.id.CAshowRePasswordIV);
        CAprogressBarPB = findViewById(R.id.CAprogressBarPB);

        CAemailET.setText(Email);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.phoneExtension_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        CAphoneNumberExtS.setSelection(1);
        CAphoneNumberExtS.setAdapter(adapter);
        CAphoneNumberExtS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CAshowPasswordIV.setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                if (i == 0) {
                    CApasswordET.setTransformationMethod(null);
                    i++;
                    CAshowPasswordIV.setImageDrawable(getDrawable(R.drawable.ic_visible));
                } else if (i == 1) {
                    CApasswordET.setTransformationMethod(new PasswordTransformationMethod());
                    i = 0;
                    CAshowPasswordIV.setImageDrawable(getDrawable(R.drawable.ic_visible_false));

                }
            }
        });

        CAshowRePasswordIV.setOnClickListener(new View.OnClickListener() {
            int i = 0;

            @Override
            public void onClick(View v) {
                if (i == 0) {
                    CArePasswordET.setTransformationMethod(null);
                    CAshowRePasswordIV.setImageDrawable(getDrawable(R.drawable.ic_visible));
                    i++;
                } else if (i == 1) {
                    CArePasswordET.setTransformationMethod(new PasswordTransformationMethod());
                    CAshowRePasswordIV.setImageDrawable(getDrawable(R.drawable.ic_visible_false));
                    i = 0;
                }
            }
        });

        CAcreateButtonMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!CApasswordET.getText().toString().equals(CArePasswordET.getText().toString())) {
                    CApasswordET.setError("Password not the same!");
                    CArePasswordET.setError("Password not the same!");
                } else {
                    checkAccountDetails(CAnricET.getText().toString(), CAphoneNumberET.getText().toString(), Email);
                    CAcreateButtonMB.setVisibility(View.INVISIBLE);
                    CAprogressBarPB.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //END OF ONCREATE

    private void checkAccountDetails (String NRIC, String PhoneNumber, String Email) {
        RequestQueue queue = Volley.newRequestQueue(CreateAccount.this);
        String URL = "https://queder-a59fe69a45d0.herokuapp.com/account/checkAccountDetails";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.e("TAG", "RESPONSE IS " + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("Success")){
                        Intent VerifyOTP = new Intent(getApplicationContext(), VerifyOTP.class);
                        VerifyOTP.putExtra("OTPidFromUser", jsonObject.getString("OTPid"));
                        VerifyOTP.putExtra("accessType", "register");
                        VerifyOTP.putExtra("email", Email);
                        VerifyOTP.putExtra("firstName", CAfirstNameET.getText().toString());
                        VerifyOTP.putExtra("fullName", CAfullNameET.getText().toString());
                        VerifyOTP.putExtra("nric", CAnricET.getText().toString());
                        VerifyOTP.putExtra("phoneNumberEx", CAphoneNumberExtS.getSelectedItem().toString());
                        VerifyOTP.putExtra("phoneNumber", CAphoneNumberET.getText().toString());
                        VerifyOTP.putExtra("password", CApasswordET.getText().toString());
                        startActivity(VerifyOTP);
                        finish();
                    }

                    if (jsonObject.getString("message").equals("NRIC already exist")) {
                        CAnricET.setError("NRIC already exists!");
                    } else if (jsonObject.getString("message").equals("Phone Number already exist")) {
                        CAphoneNumberET.setError("Phone Number already exists!");
                    } else {
                        Toast.makeText(CreateAccount.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                    CAcreateButtonMB.setVisibility(View.VISIBLE);
                    CAprogressBarPB.setVisibility(View.INVISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    CAcreateButtonMB.setVisibility(View.VISIBLE);
                    CAprogressBarPB.setVisibility(View.INVISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CreateAccount.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String,String> getParams() {

                Map<String,String> params = new HashMap<>();
                params.put("nric", NRIC);
                params.put("phoneNumber", PhoneNumber);
                params.put("email", Email);
                return params;
            }
        };

        queue.add(request);
    }

}
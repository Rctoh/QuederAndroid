package com.example.queder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;

public class VerifyOTP extends AppCompatActivity {

    TextView VOsubTextTV, VOresendCodeTV;
    EditText VOinput1ET, VOinput2ET, VOinput3ET, VOinput4ET, VOinput5ET;
    String accessType, email, fullName, firstName, NRIC, phoneNumberEx, phoneNumber, password, OTPidFromUser,
            symmetricEncryptedPatientDetails, userId;
    SharedPreferences sp;

    KeyPair keyPair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        VOsubTextTV = findViewById(R.id.VOsubTextTV);
        VOresendCodeTV = findViewById(R.id.VOresendCodeTV);
        VOinput1ET = findViewById(R.id.VOinput1ET);
        VOinput2ET = findViewById(R.id.VOinput2ET);
        VOinput3ET = findViewById(R.id.VOinput3ET);
        VOinput4ET = findViewById(R.id.VOinput4ET);
        VOinput5ET = findViewById(R.id.VOinput5ET);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        Bundle userDetails = getIntent().getExtras();
        OTPidFromUser = userDetails.getString("OTPidFromUser");
        accessType = userDetails.getString("accessType");
        email = userDetails.getString("email");
        fullName = userDetails.getString("fullName");
        firstName = userDetails.getString("firstName");
        NRIC = userDetails.getString("nric");
        phoneNumberEx = userDetails.getString("phoneNumberEx");
        phoneNumber = userDetails.getString("phoneNumber");
        password = userDetails.getString("password");
        userId = userDetails.getString("userId");

        SpannableString text = new SpannableString("We just sent a 5-digit verification code to " + email + ". Enter the code in the box below to continue.");

        int endNumber = email.length() + 44;

        text.setSpan(new ForegroundColorSpan(getColor(R.color.DarkGreen)), 44, endNumber, 0);

        VOsubTextTV.setText(text, TextView.BufferType.SPANNABLE);

        SpannableString text2 = new SpannableString("Didn't receive a code? Resend code");

        text2.setSpan(new ForegroundColorSpan(getColor(R.color.SecondaryYellow)), 23, 34, 0);

        VOresendCodeTV.setText(text2, TextView.BufferType.SPANNABLE);

        TextView[] otpTextViews = {VOinput1ET, VOinput2ET, VOinput3ET, VOinput4ET, VOinput5ET};

        VOinput1ET.requestFocus();

        for (int i = 0; i < otpTextViews.length; i++) {
            final int finalI = i;
            final TextView currTextView = otpTextViews[i];
            currTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (currTextView.length() == 0 && finalI > 0) {
                        otpTextViews[finalI - 1].requestFocus();
                    } else if (currTextView.length() == 1) {
                        if (finalI < otpTextViews.length - 1) {
                            otpTextViews[finalI + 1].requestFocus();
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }


        VOinput5ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (VOinput5ET.getText().toString().equals("")) {

                } else {
                    String OTPentered = VOinput1ET.getText().toString() + VOinput2ET.getText().toString() + VOinput3ET.getText().toString() + VOinput4ET.getText().toString() + VOinput5ET.getText().toString();

                    if (accessType.equals("login")) {

                        try {
                            keyPair = encryptDecypt.generateAsymmetricKeyPair();
                            String publicKeyString = encryptDecypt.convertPublicKeyToString(keyPair.getPublic());

                            verifyOTPLogin(OTPentered, OTPidFromUser, userId, email, publicKeyString);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                    } else {

                        try {

                            //STEP 1: PUT ALL THE PATIENT DETAILS INTO A JSON OBJECT
                            JSONObject patientDetails = new JSONObject();
                            patientDetails.put("NRIC", NRIC.toUpperCase());
                            patientDetails.put("firstName", firstName);
                            patientDetails.put("fullName", fullName);
                            patientDetails.put("phoneNumberEx", phoneNumberEx);
                            patientDetails.put("phoneNumber", phoneNumber);
                            patientDetails.put("visitHistory", "[]");
                            patientDetails.put("otherInformation", "{}");

                            //STEP 3: CONVERT JSON OBJECT TO STRING
                            String patientDetailsString = patientDetails.toString();

                            //STEP 4: CREATE SYMMETRIC KEY AND ENCRYPT THE JSON OBJECT STRING WITH THE KEY
                            //This symmetric encryption is for when user log in to the account on another device.
                            //This is how they can get back their data
                            encryptDecypt.generateAndStoreUserSymmetricKey(password, NRIC.toUpperCase(), getApplicationContext());
                            String sharedEncryptedData = encryptDecypt.Encrypt(patientDetailsString, getApplicationContext());

                            //STEP 5: GENERATE A ASYMMETRIC ENCRYPTION KEY PAIR FOR THE DEVICE
                            keyPair = encryptDecypt.generateAsymmetricKeyPair();

                            //STEP 6: POST TO THE SERVER
                            verifyOTPRegister(
                                    OTPentered,
                                    OTPidFromUser,
                                    accessType,
                                    firstName,
                                    fullName,
                                    encryptDecypt.convertPublicKeyToString(keyPair.getPublic()),
                                    password,
                                    NRIC,
                                    email,
                                    sharedEncryptedData
                                    );

                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
        });

    }

    public void verifyOTPLogin(String OTPentered, String OTPidFromUser, String userId, String email, String publicKeyString) {

        String url = "https://queder-a59fe69a45d0.herokuapp.com/account/verifyOTP";
        RequestQueue queue = Volley.newRequestQueue(VerifyOTP.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("Success")) {

                        String sharedEncryptedData = jsonObject.getString("sharedEncryptedData");

                        encryptDecypt.generateAndStoreUserSymmetricKey(password, NRIC.toUpperCase(), getApplicationContext());
                        String decryptedPatientData = encryptDecypt.Decrypt(sharedEncryptedData, getApplicationContext());

                        JSONObject patientObject = new JSONObject(decryptedPatientData);

                        firstName = patientObject.getString("firstName");
                        fullName = patientObject.getString("fullName");

                        sp.edit().putString("fullName", fullName).apply();

                        if (fullName.length() > 15) {
                            sp.edit().putString("DisplayName", firstName).apply();
                        } else {
                            sp.edit().putString("DisplayName", fullName).apply();
                        }

                        if (jsonObject.getBoolean("ifStatedAllergies")) {
                            sp.edit().putBoolean("ifStatedAllergies", true).apply();
                        }

                        String jwtToken = jsonObject.getString("jwtToken");
                        JSONObject jwtTokenObject = new JSONObject(jwtToken);
                        String permToken = jwtTokenObject.getString("permToken");

                        sp.edit().putString("permToken", permToken)
                                .putBoolean("logged", true)
                                .putString("deviceId", jsonObject.getString("deviceId"))
                                .putString("encryptedUserId", encryptDecypt.asymmetricEncryption(userId)).apply();

                        toMainpage();



                    } else {
                        Toast.makeText(VerifyOTP.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(VerifyOTP.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("OTPentered", OTPentered);
                params.put("OTPidFromUser", OTPidFromUser);
                params.put("accessType", "login");
                params.put("userId", userId);
                params.put("email", email);
                params.put("publicKeyString", publicKeyString);
                return params;

            }
        };
        queue.add(request);
    }

//    public void updateDetailsLogin(String userId, String userPublicKey) {
//
//        Toast.makeText(this, "called the function", Toast.LENGTH_SHORT).show();
//
//        String url = "https://queder-a59fe69a45d0.herokuapp.com/account/updateDetailsLogin";
//        RequestQueue queue = Volley.newRequestQueue(VerifyOTP.this);
//
//        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    if (jsonObject.getString("status").equals("Success")) {
//
//
//
//                    } else {
//                        Toast.makeText(VerifyOTP.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }, new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                Toast.makeText(VerifyOTP.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();
//
//            }
//        }) {
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded; charset=UTF-8";
//            }
//
//
//            @Override
//            protected Map<String, String> getParams() {
//
//                Map<String, String> params = new HashMap<>();
//                params.put("userId", userId);
//                params.put("userPublicKey", userPublicKey);
//                params.put("deviceUniqueEncryptedData", deviceUniqueEncryptedData);
//                return params;
//
//            }
//        };
//        queue.add(request);
//    }

    public void verifyOTPRegister(String OTPentered, String OTPidFromUser, String accessType,
                                  String firstName, String fullName, String publicKey, String password,
                                  String NRIC, String email, String sharedEncryptedData) {

        String url = "https://queder-a59fe69a45d0.herokuapp.com/account/verifyOTP";
        RequestQueue queue = Volley.newRequestQueue(VerifyOTP.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("Success")) {

                        try {
                            sp.edit().putString("fullName", fullName).apply();

                            if (fullName.length() > 15) {
                                sp.edit().putString("DisplayName", firstName).apply();
                            } else {
                                sp.edit().putString("DisplayName", fullName).apply();
                            }

                            String jwtToken = jsonObject.getString("jwtToken");
                            JSONObject jwtTokenObject = new JSONObject(jwtToken);
                            String permToken = jwtTokenObject.getString("permToken");
                            String encryptedUserId = encryptDecypt.asymmetricEncryption(jsonObject.getString("userId"));

                            sp.edit().putBoolean("logged", true)
                                    .putString("permToken", permToken)
                                    .putString("deviceId", jsonObject.getString("deviceId"))
                                    .putString("encryptedUserId", encryptedUserId).apply();

                            toMainpage();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }


                    } else {
                        Toast.makeText(VerifyOTP.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(VerifyOTP.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("OTPentered", OTPentered);
                params.put("OTPidFromUser", OTPidFromUser);
                params.put("accessType", accessType);
                params.put("userPublicKey", publicKey);
                params.put("password", password);
                params.put("NRIC", NRIC);
                params.put("email", email);
                params.put("sharedEncryptedData", sharedEncryptedData);

                return params;
            }
        };
        queue.add(request);
    }



    private void toMainpage() {
        Intent Mainpage = new Intent(this, Mainpage.class);
        startActivity(Mainpage);
        finish();
    }


}
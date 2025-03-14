package com.example.queder;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class IndividualClinic extends AppCompatActivity {

    String clinicId, imageUrl, clinicName, address, town, openingHours, phoneNumber, doctor, clinicType, clinicProgramme, paymentMethod, publicTransport, carpark, price, rating, currentQueue, queueDetails, doctorPublicKey;
    int patientsAhead = 0;
    TextView ICqueueCountTV, ICclinicNameTV, ICaddressTV, ICdoctorTV, ICclinicNameCardTV, ICclinicAddressCardTV, ICclinicOpeningHoursCardTV, ICclinicContactCardTV, ICdoctorInTodayCardTV, ICclinicTypeCardTV, ICclinicProgrammeCardTV, ICpaymentCardTV, ICpublicTransportCardTV, ICcarparkCardTV, ICclinicRatingCardTV;
    MaterialButton ICgetQueueMB;
    ImageView ICiamgeIV, ICbackIV, ICclinicDetailsMoreIV, ICclinicDetailsCloseIV;
    CardView ICclinicDetailsCollapsedCV, ICclinicDetailsFullCV;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_clinic);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        getBundleInformation();
        setUpViews();
        setUpPusher();

        ICgetQueueMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sp.getBoolean("ifStatedAllergies", false)) {
                    if (sp.getBoolean("gotQueue", false)) {
                        showAlreadyHaveDialog();
                    } else {
                        showSymptomDialog();
                    }
                } else {
                    showAllergiesDialog();
                }
            }
        });

        ICbackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainPage();
            }
        });

        ICclinicDetailsMoreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ICclinicDetailsCollapsedCV.setVisibility(View.GONE);
                ICclinicDetailsFullCV.setVisibility(View.VISIBLE);
            }
        });

        ICclinicDetailsCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ICclinicDetailsCollapsedCV.setVisibility(View.VISIBLE);
                ICclinicDetailsFullCV.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainPage();
    }

    //END OF ON CREATE

    private void getBundleInformation() {
        Bundle details = getIntent().getExtras();
        clinicId = details.getString("clinicId");
        imageUrl = details.getString("imageUrl");
        clinicName = details.getString("clinicName");
        address = details.getString("address");
        town = details.getString("town");
        openingHours = details.getString("openingHours");
        phoneNumber = details.getString("phoneNumber");
        doctor = details.getString("doctor");
        clinicType = details.getString("clinicType");
        clinicProgramme = details.getString("clinicProgramme");
        paymentMethod = details.getString("paymentMethod");
        publicTransport = details.getString("publicTransport");
        carpark = details.getString("carpark");
        price = details.getString("price");
        rating = details.getString("rating");
        currentQueue = details.getString("currentQueue");
        queueDetails = details.getString("queueDetails");
        doctorPublicKey = details.getString("doctorPublicKey");
    }

    private void setUpViews() {

        ICqueueCountTV = findViewById(R.id.ICqueueCountTV);
        ICclinicNameTV = findViewById(R.id.ICclinicNameTV);
        ICaddressTV = findViewById(R.id.ICaddressTV);
        ICdoctorTV = findViewById(R.id.ICdoctorTV);
        ICgetQueueMB = findViewById(R.id.ICgetQueueMB);
        ICiamgeIV = findViewById(R.id.ICimageIV);
        ICbackIV = findViewById(R.id.ICbackIV);

        ICclinicNameCardTV = findViewById(R.id.ICclinicNameCardTV);
        ICclinicAddressCardTV = findViewById(R.id.ICclinicAddressCardTV);
        ICclinicOpeningHoursCardTV = findViewById(R.id.ICclinicOpeningHoursCardTV);
        ICclinicContactCardTV = findViewById(R.id.ICclinicContactCardTV);
        ICdoctorInTodayCardTV = findViewById(R.id.ICdoctorInTodayCardTV);
        ICclinicTypeCardTV = findViewById(R.id.ICclinicTypeCardTV);
        ICclinicProgrammeCardTV = findViewById(R.id.ICclinicProgrammeCardTV);
        ICpaymentCardTV = findViewById(R.id.ICpaymentCardTV);
        ICpublicTransportCardTV = findViewById(R.id.ICpublicTransportCardTV);
        ICcarparkCardTV = findViewById(R.id.ICcarparkCardTV);
        ICclinicRatingCardTV = findViewById(R.id.ICclinicRatingCardTV);
        ICclinicDetailsMoreIV = findViewById(R.id.ICclinicDetailsMoreIV);
        ICclinicDetailsCloseIV = findViewById(R.id.ICclinicDetailsCloseIV);
        ICclinicDetailsCollapsedCV = findViewById(R.id.ICclinicDetailsCollapsedCV);
        ICclinicDetailsFullCV = findViewById(R.id.ICclinicDetailsFullCV);


        try {
            JSONArray queueDetailsJsonArray = new JSONArray(queueDetails);
            patientsAhead = queueDetailsJsonArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (patientsAhead <= 10) {
            ICqueueCountTV.setTextColor(getColor(R.color.DarkGreen));
        } else if (patientsAhead <= 30) {
            ICqueueCountTV.setTextColor(getColor(R.color.SecondaryYellow));
        } else {
            ICqueueCountTV.setTextColor(getColor(R.color.BurgandyRed));
        }

        ICqueueCountTV.setText(String.valueOf(patientsAhead));
        ICclinicNameTV.setText(clinicName);
        ICaddressTV.setText(address);
        ICdoctorTV.setText(doctor);
        Picasso.get().load(imageUrl).into(ICiamgeIV);

        ICclinicNameCardTV.setText(clinicName);
        ICclinicAddressCardTV.setText(address);
        ICclinicOpeningHoursCardTV.setText(openingHours);
        ICclinicContactCardTV.setText(phoneNumber);
        ICdoctorInTodayCardTV.setText(doctor);
        ICclinicTypeCardTV.setText(clinicType);
        ICclinicProgrammeCardTV.setText(clinicProgramme);
        ICpaymentCardTV.setText(paymentMethod);
        ICpublicTransportCardTV.setText(publicTransport);
        ICcarparkCardTV.setText(carpark);
        ICclinicRatingCardTV.setText(rating);
    }

    private void setUpPusher() {

        initialisePusher.init(this);
        Channel channel = initialisePusher.getChannel("Queder");
        channel.bind("queueDetailsChanged", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {

                try {
                    JSONObject eventObject = new JSONObject(event.getData());

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (clinicId.equals(String.valueOf(eventObject.getInt("clinicId")))) {
                                    queueDetails = eventObject.getString("sanitizedQueueDetails");

                                    try {
                                        JSONArray queueDetailsJsonArrary = new JSONArray(queueDetails);

                                        patientsAhead = queueDetailsJsonArrary.length();

                                        if (patientsAhead <= 10) {
                                            ICqueueCountTV.setTextColor(getColor(R.color.DarkGreen));
                                        } else if (patientsAhead <= 30) {
                                            ICqueueCountTV.setTextColor(getColor(R.color.SecondaryYellow));
                                        } else {
                                            ICqueueCountTV.setTextColor(getColor(R.color.BurgandyRed));
                                        }

                                        ICqueueCountTV.setText(String.valueOf(patientsAhead));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
    }



    private void showAlreadyHaveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(IndividualClinic.this);
        builder.setMessage("You already have a queue number! Cancel the current queue number before taking a new one.");
        builder.setTitle("Uh oh...");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        builder.setPositiveButton("Okay", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showAllergiesDialog() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.allergy_dialog, null);
        builder.setView(dialogView);

        MaterialButton ADsubmitMB = dialogView.findViewById(R.id.ADsubmitMB);

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();


        ADsubmitMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ADallergiesET = dialogView.findViewById(R.id.ADallergiesET);
                EditText ADchronicDiseaseET = dialogView.findViewById(R.id.ADchronicDiseaseET);
                String allergies = ADallergiesET.getText().toString();
                String chronicDisease = ADchronicDiseaseET.getText().toString();

                try {

                    String decryptedSharedEncryptedData =
                            encryptDecypt.Decrypt(sp.getString("sharedEncryptedData", ""), getApplicationContext());
                    JSONObject decryptedObject = new JSONObject(decryptedSharedEncryptedData);
                    JSONObject otherInformation = new JSONObject(decryptedObject.getString("otherInformation"));

                    otherInformation.put("allergies", allergies);
                    otherInformation.put("chronicDisease", chronicDisease);
                    
                    String otherInformationString = otherInformation.toString();
                    decryptedObject.put("otherInformation", otherInformationString);

                    String newSharedEncryptedData = encryptDecypt.Encrypt(decryptedObject.toString(), getApplicationContext());

                    updateOtherInformations(newSharedEncryptedData);

                    dialog.dismiss();
                    
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

    }
    
    private void showSymptomDialog() {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.symptom_dialog, null);
        builder.setView(dialogView);

        MaterialButton SDsubmitMB = dialogView.findViewById(R.id.SDsubmitMB);
        CheckBox SDtravelledRecentlyCB = dialogView.findViewById(R.id.SDtravelledRecentlyCB);
        CardView SDtravelledRecentlyCV = dialogView.findViewById(R.id.SDtravelledRecentlyCV);
        SDtravelledRecentlyCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                SDtravelledRecentlyCV.setVisibility(View.VISIBLE);
            } else {
                SDtravelledRecentlyCV.setVisibility(View.GONE);
            }
        });
        SDsubmitMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText SDsymptomET = dialogView.findViewById(R.id.SDsymptomET);
                EditText SDcommentET = dialogView.findViewById(R.id.SDcommentET);
                EditText SDtravelledRecentlyET = dialogView.findViewById(R.id.SDtravelledRecentlyET);

                String symptom = SDsymptomET.getText().toString();
                String comment = SDcommentET.getText().toString();
                String travelledRecently = SDtravelledRecentlyET.getText().toString();

                if (SDsymptomET.getText().toString().isEmpty()) {
                    SDsymptomET.setError("Please fill in your symptoms");
                } else {
                    try {
                        Log.e("hello", sp.getString("sharedEncryptedData", "null"));

                        String decryptedPatientInfo = encryptDecypt.Decrypt(sp.getString("sharedEncryptedData", "null"), getApplicationContext());
                        String asymmetricEncryptedForDoctor = encryptDecypt.encryptDataForDoctor(decryptedPatientInfo, doctorPublicKey);


                        getQueueNumber(symptom, comment, travelledRecently, asymmetricEncryptedForDoctor);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }



                }
            }
        });

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void updateOtherInformations(String newSharedEncryptedData) {

        RequestQueue queue = Volley.newRequestQueue(IndividualClinic.this);
        String URL = "https://queder-a59fe69a45d0.herokuapp.com/patientdetails/updateOtherInformation";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("Success")) {

                        sp.edit().putBoolean("ifStatedAllergies", true).apply();

                        AlertDialog.Builder builder = new AlertDialog.Builder(IndividualClinic.this);
                        builder.setMessage("Information update successful! You can now proceed to get your queue number");
                        builder.setTitle("Yay!");

                        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                        builder.setCancelable(false);

                        // Set the positive button with yes name and a click listener
                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // When the user clicks the "Okay" button, dismiss the dialog
                                dialog.dismiss();
                            }
                        });

                        // Create the Alert dialog
                        AlertDialog alertDialog = builder.create();
                        // Show the Alert Dialog box
                        alertDialog.show();
                    }


                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(IndividualClinic.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("sharedEncryptedData", newSharedEncryptedData);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add your JWT token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sp.getString("permToken", null));
                return headers;
            }
        };

        queue.add(request);
    }

    private void getQueueNumber(String symptom, String comment, String travelledRecently, String asymmetricEncryptedForDoctor) {

        ICgetQueueMB.setClickable(false);

        RequestQueue queue = Volley.newRequestQueue(IndividualClinic.this);
        String URL = "https://queder-a59fe69a45d0.herokuapp.com/queuesystem/getqueuenumber";

        String transactionCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Transaction code acts as just an extra layer of security which ensure that if 2 device click get queue number
                    // at the same time, each of the device will get their own queue number and wont overlap. If overlap, the slower
                    // device will then automatically run the function again (Overlap = transaction code receive not matching as it
                    // is someone else one

                    if (jsonObject.getString("transactionCode").equals(transactionCode)) {
                        Intent QueueDetails = new Intent(getApplicationContext(), QueueDetails.class);
                        QueueDetails.putExtra("clinicId", clinicId);
                        QueueDetails.putExtra("clinicName", clinicName);
                        QueueDetails.putExtra("address", address);
                        QueueDetails.putExtra("openingHours", openingHours);
                        QueueDetails.putExtra("phoneNumber", phoneNumber);
                        QueueDetails.putExtra("doctor", doctor);
                        QueueDetails.putExtra("clinicType", clinicType);
                        QueueDetails.putExtra("clinicProgramme", clinicProgramme);
                        QueueDetails.putExtra("paymentMethod", paymentMethod);
                        QueueDetails.putExtra("publicTransport", publicTransport);
                        QueueDetails.putExtra("carpark", carpark);
                        QueueDetails.putExtra("price", price);
                        QueueDetails.putExtra("rating", rating);
                        QueueDetails.putExtra("queueNumber", String.valueOf(jsonObject.getInt("queueNumber")));
                        QueueDetails.putExtra("newQueueDetails", jsonObject.getString("newQueueDetails"));
                        QueueDetails.putExtra("transactionCode", "2");
                        sp.edit().putBoolean("gotQueue", true).apply();
                        sp.edit().putString("clinicId", clinicId).apply();
                        sp.edit().putString("clinicName", clinicName).apply();
                        sp.edit().putString("queueNumber", String.valueOf(jsonObject.getInt("queueNumber"))).apply();
                        sp.edit().putString("address", address).apply();
                        sp.edit().putString("imageUrl", imageUrl).apply();
                        sp.edit().putString("doctor", doctor).apply();
                        sp.edit().putString("uniqueAppointmentToken", jsonObject.getString("uniqueAppointmentToken")).apply();
                        startActivity(QueueDetails);
                        finish();

                    } else {
                        getQueueNumber(symptom, comment, travelledRecently, asymmetricEncryptedForDoctor);
                    }
                } catch (JSONException e) {
                    ICgetQueueMB.setClickable(true);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ICgetQueueMB.setClickable(true);
                Toast.makeText(IndividualClinic.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("clinicId", clinicId);
                params.put("fullName", sp.getString("fullName", "Error name"));
                params.put("transactionCode", transactionCode);
                params.put("symptom", symptom);
                params.put("comment", comment);
                params.put("travelledRecently", travelledRecently);
                params.put("asymmetricEncryptedForDoctor", asymmetricEncryptedForDoctor);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                // Add your JWT token to the headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + sp.getString("permToken", null));
                return headers;
            }
        };

        queue.add(request);
    }

    public void toMainPage() {
        Intent Mainpage = new Intent(getApplicationContext(), Mainpage.class);
        startActivity(Mainpage);
        finish();
    }


}
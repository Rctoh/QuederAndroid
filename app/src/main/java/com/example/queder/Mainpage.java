package com.example.queder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pusher.pushnotifications.PushNotifications;

public class Mainpage extends AppCompatActivity implements ClinicAdapter.OnNoteListener, SearchAdapter.OnNoteListener {

    CardView MPprofilePicCV;
    ImageView MPprofilePicIV, MPrightArrowIV;
    ImageButton MPhomeButtonIB, MPsearchButtonIB, MPaddNewButtonIB, MPsaveButtonIB, MPeditButtonIB, MPnavBar;

    androidx.appcompat.widget.SearchView MPsearchBar;
    RecyclerView MPtrendingRecyclerRV, MPnearRecyclerRV, MPsearchRecyclerRV;
    MaterialCardView MPqueueSummaryMCV;

    CardView MPsearchRecyclerCardCV;
    TextView MPwelcomeTextTV, MPgreetTV, MPqueueSummaryNameTV, MPqueueSummaryAddressTV, MPqueueNumberTV, MPmainTrendingTV, MPnearTV, MPdoctorTextTV, MPnoClinicTV;
    ClinicAdapter nearClinicAdapter, trendingClinicAdapter, placeHolderAdapter;

    SearchAdapter searchAdapter;
    List<ClinicDetails> clinicDetailsList, clinicDetailsListNear, clinicDetailsListTrending, placeHolderList, searchList;
    Boolean doubleBackToExitPressedOnce = false;
    SharedPreferences sp;
    JSONArray jsonarray;


    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.mainpage);

        MPdoctorTextTV = findViewById(R.id.MPdoctorTextTV);
        MPgreetTV = findViewById(R.id.MPgreetTV);
        MPmainTrendingTV = findViewById(R.id.MPmainTrendingTV);
        MPnavBar = findViewById(R.id.MPnavBar);
        MPnearRecyclerRV = findViewById(R.id.MPnearRecyclerRV);
        MPnearTV = findViewById(R.id.MPnearTV);
        MPnoClinicTV = findViewById(R.id.MPnoClinicTV);
        MPprofilePicCV = findViewById(R.id.MPprofilePicCV);
        MPprofilePicIV = findViewById(R.id.MPprofilePicIV);
        MPqueueNumberTV = findViewById(R.id.MPqueueNumberTV);
        MPqueueSummaryAddressTV = findViewById(R.id.MPqueueSummaryAddressTV);
        MPqueueSummaryMCV = findViewById(R.id.MPqueueSummaryMCV);
        MPqueueSummaryNameTV = findViewById(R.id.MPqueueSummaryNameTV);
        MPrightArrowIV = findViewById(R.id.MPrightArrowIV);
        MPsearchBar = findViewById(R.id.MPsearchBar);
        MPsearchRecyclerCardCV = findViewById(R.id.MPsearchRecyclerCardCV);
        MPsearchRecyclerRV = findViewById(R.id.MPsearchRecyclerRV);
        MPtrendingRecyclerRV = findViewById(R.id.MPtrendingRecyclerRV);
        MPwelcomeTextTV = findViewById(R.id.MPwelcomeTextTV);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        Map<String,?> keys = sp.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " +
                    entry.getValue().toString());
        }

        getDetails();
        setUpRecyclerView();
        setUpPusher();

        //Welcome Message
        MPwelcomeTextTV.setText(getTimeOfDay() + " \uD83D\uDC4B");
        String DisplayName = sp.getString("DisplayName", "User");
        MPgreetTV.setText(DisplayName);

        //Check SP if user has any queue number
        if (sp.getBoolean("gotQueue", false)) {
            showQueueStatus();
        }

        MPprofilePicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        MPsearchBar.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchValue) {
                if (searchValue.isEmpty()) {
                    MPsearchRecyclerCardCV.setVisibility(View.GONE);
                    
                } else {
                    if (searchAdapter != null) {
                        searchAdapter = null;
                        MPsearchRecyclerRV.setAdapter(null);
                    }
                    search(searchValue);
                    MPsearchRecyclerCardCV.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        //MPhomeButtonIB = findViewById(R.id.MPhoneButtonIB);
        //MPsearchButtonIB = findViewById(R.id.MPsearchButtonIB);
        //MPaddNewButtonIB = findViewById(R.id.MPaddNewButtonIB);
        //MPsaveButtonIB = findViewById(R.id.MPsaveButtonIB);
        //MPeditButtonIB = findViewById(R.id.MPeditButtonIB);

//        String ProfilePic = sp.getString("profilePicUrl", "Error");
//        if (!ProfilePic.equals("Error") && !ProfilePic.equals("0")) {
//            Picasso.get().load(ProfilePic).into(MPprofilePicIV);
//        }

//        String publickey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuvV6DLzQCEPf+pZ+A989lHMPiczXy6s2RKLMdQ7YX5F+9tHG4j69m0f2mzO3KcjfsIk6E3GFBgvtUDkSL9ajcn4ACrBBMsqQhZMgjnlVMUaKu8tlt+dRzNXXQ45qWqIRVoYa0Fg4yMRbvnMmFW+ZySMMl9ZVCoPKtgSlXX/7DJL334wmnRiyzFdrDWTBIL22HgKlVBf9AMka6SVdr7aOirvdXNaaiC2QBiuwH6pqjDAsBTpSkH7cl9BhRmowErSv3K42v1r1pKVbOPtkIFMPqwJhKIyM1Bm18gsgfLmRcz9TKdh5fpm3zu6Hp5Vwvnh+qhIZaJjbCZKWY2eYw5IhXwIDAQAB";
//        try {
//            Log.e("encrypteddata", encryptDecypt.encryptDataForDoctor("To be encrypted", publickey));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }


        PushNotifications.start(getApplicationContext(), "69a85f78-7980-40a7-b67c-f967b6ee4fca");
        PushNotifications.addDeviceInterest("hello");

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void getDetails() {
        String url = "https://queder-a59fe69a45d0.herokuapp.com/";

        RequestQueue queue = Volley.newRequestQueue(Mainpage.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject messageObject = jsonObject.getJSONObject("message");

                    jsonarray = messageObject.getJSONArray("clinics");


                    ////START OF POPULATING RECYCLER VIEW

                    clinicDetailsList = new ArrayList<>();
                    clinicDetailsListNear = new ArrayList<>();
                    clinicDetailsListTrending = new ArrayList<>();

                    //traversing through all the object for the main list
                    for (int i = 0; i < jsonarray.length(); i++) {

                        JSONObject ClinicList = jsonarray.getJSONObject(i);

                        clinicDetailsList.add(new ClinicDetails(ClinicList.getString("clinicId"), ClinicList.getString("imageUrl"), ClinicList.getString("clinicName"), ClinicList.getString("address"), ClinicList.getString("town"), ClinicList.getString("openingHours"), ClinicList.getString("phoneNumber"), ClinicList.getString("doctor"), ClinicList.getString("clinicType"), ClinicList.getString("clinicProgramme"), ClinicList.getString("paymentMethod"), ClinicList.getString("publicTransport"), ClinicList.getString("carpark"), ClinicList.getString("price"), ClinicList.getString("rating"), ClinicList.getString("currentQueue"), ClinicList.getString("queueDetails"), ClinicList.getString("lastQueueNumber"), ClinicList.optString("doctorPublicKey", "")));

                    }

                    //traversing through all the object for the near list
                    for (int i = 0; i < jsonarray.length() - 2; i++) {

                        JSONObject ClinicList = jsonarray.getJSONObject(i);

                        clinicDetailsListNear.add(new ClinicDetails(ClinicList.getString("clinicId"), ClinicList.getString("imageUrl"), ClinicList.getString("clinicName"), ClinicList.getString("address"), ClinicList.getString("town"), ClinicList.getString("openingHours"), ClinicList.getString("phoneNumber"), ClinicList.getString("doctor"), ClinicList.getString("clinicType"), ClinicList.getString("clinicProgramme"), ClinicList.getString("paymentMethod"), ClinicList.getString("publicTransport"), ClinicList.getString("carpark"), ClinicList.getString("price"), ClinicList.getString("rating"), ClinicList.getString("currentQueue"), ClinicList.getString("queueDetails"), ClinicList.getString("lastQueueNumber"), ClinicList.optString("doctorPublicKey", "")));

                    }

                    //traversing through all the object for the trending list
                    for (int i = 0; i < jsonarray.length() - 3; i++) {

                        JSONObject ClinicList = jsonarray.getJSONObject(i + 3);

                        clinicDetailsListTrending.add(new ClinicDetails(ClinicList.getString("clinicId"), ClinicList.getString("imageUrl"), ClinicList.getString("clinicName"), ClinicList.getString("address"), ClinicList.getString("town"), ClinicList.getString("openingHours"), ClinicList.getString("phoneNumber"), ClinicList.getString("doctor"), ClinicList.getString("clinicType"), ClinicList.getString("clinicProgramme"), ClinicList.getString("paymentMethod"), ClinicList.getString("publicTransport"), ClinicList.getString("carpark"), ClinicList.getString("price"), ClinicList.getString("rating"), ClinicList.getString("currentQueue"), ClinicList.getString("queueDetails"), ClinicList.getString("lastQueueNumber"),ClinicList.optString("doctorPublicKey", "")));

                    }

                    nearClinicAdapter = new ClinicAdapter(Mainpage.this, clinicDetailsListNear, Mainpage.this);

                    MPnearRecyclerRV.setAdapter(nearClinicAdapter);


                    trendingClinicAdapter = new ClinicAdapter(Mainpage.this, clinicDetailsListTrending, Mainpage.this);
                    MPtrendingRecyclerRV.setAdapter(trendingClinicAdapter);

                    ////END OF POPULATING RECYCLER VIEW

                   updateUserInformationForDevice(messageObject);

                    MPqueueSummaryMCV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openFromSummary(sp.getString("clinicId", "Error in getting clinic ID from SP"));
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mainpage.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDetails();
                    }
                }, 3000);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                // Your parameters for the POST request
                Map<String, String> params = new HashMap<>();
                params.put("deviceId", sp.getString("deviceId", null));
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

        // Add the request to the RequestQueue
        queue.add(request);
    }

    private void setUpRecyclerView() {
        MPnearRecyclerRV = findViewById(R.id.MPnearRecyclerRV);
        MPnearRecyclerRV.setHasFixedSize(true);
        MPnearRecyclerRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        MPtrendingRecyclerRV = findViewById(R.id.MPtrendingRecyclerRV);
        MPtrendingRecyclerRV.setHasFixedSize(true);
        MPtrendingRecyclerRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        MPsearchRecyclerRV.setHasFixedSize(true);
        MPsearchRecyclerRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private String getTimeOfDay() {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(timeZone);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }

    private void showQueueStatus() {
        MPqueueSummaryMCV.setVisibility(View.VISIBLE);
        MPqueueSummaryNameTV.setText(sp.getString("clinicName", "Error in Clinic Name SP"));
        MPqueueSummaryAddressTV.setText(sp.getString("address", "Error in Adress SP"));
        MPqueueNumberTV.setText(sp.getString("queueNumber", "Error in Queue Number SP"));
        MPdoctorTextTV.setText(sp.getString("doctor", "Error in Doctor SP"));
        MPnearTV.setPadding(0, 45, 0, 0);
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
                                String pusherClinicId = eventObject.getString("clinicId");

                                // Find the position of the item with the matching ID in clinicDetailsListNear
                                int positionNear = -1;
                                for (int i = 0; i < clinicDetailsListNear.size(); i++) {
                                    if (clinicDetailsListNear.get(i).getClinicId().equals(pusherClinicId)) {
                                        positionNear = i;
                                        break;
                                    }
                                }

                                //Find the position of the item with the matching ID in clinicDetailsListTrending
                                int positionTrending = -1;
                                for (int i = 0; i < clinicDetailsListTrending.size(); i++) {
                                    if (clinicDetailsListTrending.get(i).getClinicId().equals(pusherClinicId)) {
                                        positionTrending = i;
                                        break;
                                    }
                                }

                                JSONArray newQueueDetails = new JSONArray(eventObject.getString("sanitizedQueueDetails"));

                                //If the position was found in clinicDetailsListNear, update the item at that position
                                if (positionNear != -1) {
                                    clinicDetailsListNear.get(positionNear).setQueueDetails(String.valueOf(newQueueDetails));
                                    nearClinicAdapter.notifyItemChanged(positionNear);
                                }

                                //If the position was found in clinicDetailsListTrending, update the item at that position
                                if (positionTrending != -1) {
                                    clinicDetailsListTrending.get(positionTrending).setQueueDetails(String.valueOf(newQueueDetails));
                                    trendingClinicAdapter.notifyItemChanged(positionTrending);
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

    private void updateUserInformationForDevice(JSONObject messageObject) {
        boolean ifAllergiesBooleanPresent = sp.getBoolean("ifStatedAllergies", false);

        try {
            if (messageObject.getBoolean("ifStatedAllergies") && !ifAllergiesBooleanPresent) {
                sp.edit().putBoolean("ifStatedAllergies", true).apply();
            }

            String sharedEncryptedData = messageObject.getString("sharedEncryptedData");
            String storedSharedEncryptedData = sp.getString("sharedEncryptedData", "");


            if (storedSharedEncryptedData.isEmpty() || !storedSharedEncryptedData.equals(sharedEncryptedData)) {
                sp.edit().putString("sharedEncryptedData", sharedEncryptedData).apply();
            }

        } catch (Exception e) {
            Log.e("Error", e.toString());
        }


    }

    private void openFromSummary(String clinicId) {
        for (int i = 0; i < jsonarray.length(); i++) {

            JSONObject ClinicList = null;

            try {
                ClinicList = jsonarray.getJSONObject(i);

                if (clinicId.equals(ClinicList.getString("clinicId"))) {
                    Intent QueueDetails = new Intent(getApplicationContext(), QueueDetails.class);
                    String[] queueDetailKeys = {
                            "transactionCode", "clinicId", "imageUrl", "clinicName", "address",
                            "town", "openingHours", "phoneNumber", "doctor", "clinicType",
                            "clinicProgramme", "paymentMethod", "publicTransport", "carpark",
                            "price", "rating", "currentQueue", "queueDetails", "lastQueueNumber"
                    };
                    for (String key : queueDetailKeys) {
                        QueueDetails.putExtra(key, ClinicList.optString(key, ""));
                    }
                    QueueDetails.putExtra("transactionCode", "1");
                    startActivity(QueueDetails);
                    finish();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onNoteClick(int position, List<ClinicDetails> list) {
        whereToGo(position, list);
    }

    public void whereToGo(int position, List<ClinicDetails> list) {

        if (sp.getBoolean("gotQueue", false)) {

            if (sp.getString("clinicId", "Error in clinic id SP").equals(list.get(position).getClinicId())) {

                // IF USER ALREADY HAVE QUEUE NUMBER FOR THE CLINIC HE CLICKED

                goQueueDetails(list, position);

            } else {

                // IF USER ALREADY HAVE QUEUE NUMBER BUT CLICK ON DIFFERENT CLINIC

                goIndividualClinic(list, position);
            }

        } else {

            // IF USER DONT HAVE ANY QUEUE NUMBER

            goIndividualClinic(list, position);

        }
    }

    public void goIndividualClinic(List<ClinicDetails> list, int position) {

        placeHolderList = list;

        Intent IndividualClinic = new Intent(Mainpage.this, IndividualClinic.class);
        IndividualClinic.putExtra("clinicId", placeHolderList.get(position).getClinicId());
        IndividualClinic.putExtra("imageUrl", placeHolderList.get(position).getImageUrl());
        IndividualClinic.putExtra("clinicName", placeHolderList.get(position).getClinicName());
        IndividualClinic.putExtra("address", placeHolderList.get(position).getAddress());
        IndividualClinic.putExtra("town", placeHolderList.get(position).getTown());
        IndividualClinic.putExtra("openingHours", placeHolderList.get(position).getOpeningHours());
        IndividualClinic.putExtra("phoneNumber", placeHolderList.get(position).getPhoneNumber());
        IndividualClinic.putExtra("doctor", placeHolderList.get(position).getDoctor());
        IndividualClinic.putExtra("clinicType", placeHolderList.get(position).getClinicType());
        IndividualClinic.putExtra("clinicProgramme", placeHolderList.get(position).getClinicProgramme());
        IndividualClinic.putExtra("paymentMethod", placeHolderList.get(position).getPaymentMethod());
        IndividualClinic.putExtra("publicTransport", placeHolderList.get(position).getPublicTransport());
        IndividualClinic.putExtra("carpark", placeHolderList.get(position).getCarpark());
        IndividualClinic.putExtra("price", placeHolderList.get(position).getPrice());
        IndividualClinic.putExtra("rating", placeHolderList.get(position).getRating());
        IndividualClinic.putExtra("currentQueue", placeHolderList.get(position).getCurrentQueue());
        IndividualClinic.putExtra("queueDetails", placeHolderList.get(position).getQueueDetails());
        IndividualClinic.putExtra("lastQueueNumber", placeHolderList.get(position).getLastQueueNumber());
        IndividualClinic.putExtra("doctorPublicKey", placeHolderList.get(position).getDoctorPublicKey());
        startActivity(IndividualClinic);
        finish();
    }

    public void goQueueDetails(List<ClinicDetails> list, int position) {

        placeHolderList = list;

        Intent QueueDetails = new Intent(getApplicationContext(), QueueDetails.class);
        QueueDetails.putExtra("transactionCode", "1");
        QueueDetails.putExtra("clinicId", placeHolderList.get(position).getClinicId());
        QueueDetails.putExtra("imageUrl", placeHolderList.get(position).getImageUrl());
        QueueDetails.putExtra("clinicName", placeHolderList.get(position).getClinicName());
        QueueDetails.putExtra("address", placeHolderList.get(position).getAddress());
        QueueDetails.putExtra("town", placeHolderList.get(position).getTown());
        QueueDetails.putExtra("openingHours", placeHolderList.get(position).getOpeningHours());
        QueueDetails.putExtra("phoneNumber", placeHolderList.get(position).getPhoneNumber());
        QueueDetails.putExtra("doctor", placeHolderList.get(position).getDoctor());
        QueueDetails.putExtra("clinicType", placeHolderList.get(position).getClinicType());
        QueueDetails.putExtra("clinicProgramme", placeHolderList.get(position).getClinicProgramme());
        QueueDetails.putExtra("paymentMethod", placeHolderList.get(position).getPaymentMethod());
        QueueDetails.putExtra("publicTransport", placeHolderList.get(position).getPublicTransport());
        QueueDetails.putExtra("carpark", placeHolderList.get(position).getCarpark());
        QueueDetails.putExtra("price", placeHolderList.get(position).getPrice());
        QueueDetails.putExtra("rating", placeHolderList.get(position).getRating());
        QueueDetails.putExtra("currentQueue", placeHolderList.get(position).getCurrentQueue());
        QueueDetails.putExtra("queueDetails", placeHolderList.get(position).getQueueDetails());
        QueueDetails.putExtra("lastQueueNumber", placeHolderList.get(position).getLastQueueNumber());
        startActivity(QueueDetails);
        finish();
    }

    private void search(String searchValue) {

        RequestQueue queue = Volley.newRequestQueue(Mainpage.this);
        String URL = "https://queder-a59fe69a45d0.herokuapp.com/search";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String responseString = jsonObject.getString("message");

                    Log.e("response", responseString);

                    if (responseString.equals("Uh oh, no clinic found :(")) {
                        MPsearchRecyclerRV.setVisibility(View.INVISIBLE);
                        MPnoClinicTV.setVisibility(View.VISIBLE);
                        MPnoClinicTV.setText(responseString);
                    } else if (responseString.equals("Fail")) {
                        MPsearchRecyclerRV.setVisibility(View.INVISIBLE);
                        MPnoClinicTV.setVisibility(View.VISIBLE);
                        MPnoClinicTV.setText(responseString);

                    } else {

                        JSONArray searchArray = new JSONArray(responseString);

                        MPnoClinicTV.setVisibility(View.INVISIBLE);
                        MPsearchRecyclerRV.setVisibility(View.VISIBLE);
                        searchList = new ArrayList<>();

                        for (int i = 0; i < searchArray.length(); i++) {

                            JSONObject SearchList = searchArray.getJSONObject(i);
                            searchList.add(new ClinicDetails(SearchList.getString("clinicId"),
                                    SearchList.getString("imageUrl"), SearchList.getString("clinicName"),
                                    SearchList.getString("address"), SearchList.getString("town"),
                                    SearchList.getString("openingHours"), SearchList.getString("phoneNumber"),
                                    SearchList.getString("doctor"), SearchList.getString("clinicType"),
                                    SearchList.getString("clinicProgramme"), SearchList.getString("paymentMethod"),
                                    SearchList.getString("publicTransport"), SearchList.getString("carpark"), SearchList.getString("price"),
                                    SearchList.getString("rating"), SearchList.getString("currentQueue"), SearchList.getString("queueDetails"),
                                    SearchList.getString("lastQueueNumber"), SearchList.getString("doctorPublicKey")));

                        }

                        searchAdapter = new SearchAdapter(Mainpage.this, searchList, Mainpage.this);
                        MPsearchRecyclerRV.setAdapter(searchAdapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mainpage.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("searchValue", searchValue);
                return params;
            }
        };

        queue.add(request);
        }



    public void logout() {
        String url = "https://queder-a59fe69a45d0.herokuapp.com/account/logout";

        RequestQueue queue = Volley.newRequestQueue(Mainpage.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String responseString = jsonObject.getString("status");

                    if (responseString.equals("Success")) {
                        sp.edit().clear().apply();
                        sp.edit().putBoolean("firstTimer", false).apply();
                        Intent Login = new Intent(getApplicationContext(), Login.class);
                        startActivity(Login);
                        finish();
                    } else {
                        Toast.makeText(Mainpage.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Mainpage.this, "Connection fail. Please check your network and try again!", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Your parameters for the POST request
                Map<String, String> params = new HashMap<>();
                params.put("deviceId", sp.getString("deviceId", null));
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

        // Add the request to the RequestQueue
        queue.add(request);
    }

}
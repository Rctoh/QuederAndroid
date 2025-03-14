package com.example.queder;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueueDetails extends AppCompatActivity {

    String clinicId, clinicName, address, openingHours, phoneNumber, doctor, clinicType, clinicProgramme, paymentMethod, publicTransport, carpark, rating, newQueueDetails, patientsAhead, queueNumber;
    TextView QDqueueNumberTV, QDclinicNameTV, QDqueueCountTV, QDinformationAccurateTV, QDclinicNameCardTV, QDclinicAddressCardTV, QDclinicOpeningHoursCardTV, QDclinicContactCardTV, QDdoctorInTodayCardTV, QDclinicTypeCardTV,
            QDclinicProgrammeCardTV, QDpaymentCardTV, QDpublicTransportCardTV, QDcarparkCardTV, QDclinicRatingCardTV, QDquickCheckInTV, QDqrTextTV, QDcheckedInNoteTV;
    MaterialButton QDcancelQueueMB;
    ImageView QDbackIV, QDquickRegisterMoreIV, QDquickRegisterCloseIV, QDclinicDetailsMoreIV, QDclinicDetailsCloseIV, QDquickRegisterQRcodeIV, QDcheckedInIV;
    CardView QDquickRegisterCollapsedCV, QDquickRegisterFullCV, QDclinicDetailsCollapsedCV, QDclinicDetailsFullCV;
    SharedPreferences sp;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        toMainPage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_details);

        Bundle details = getIntent().getExtras();

        if (details.getString("transactionCode").equals("1")) {
            clinicId = details.getString("clinicId");
            clinicName = details.getString("clinicName");
            address = details.getString("address");
            openingHours = details.getString("openingHours");
            phoneNumber = details.getString("phoneNumber");
            doctor = details.getString("doctor");
            clinicType = details.getString("clinicType");
            clinicProgramme = details.getString("clinicProgramme");
            paymentMethod = details.getString("paymentMethod");
            publicTransport = details.getString("publicTransport");
            carpark = details.getString("carpark");
            rating = details.getString("rating");
            newQueueDetails = details.getString("queueDetails");


        } else {
            clinicId = details.getString("clinicId");
            clinicName = details.getString("clinicName");
            address = details.getString("address");
            openingHours = details.getString("openingHours");
            phoneNumber = details.getString("phoneNumber");
            doctor = details.getString("doctor");
            clinicType = details.getString("clinicType");
            clinicProgramme = details.getString("clinicProgramme");
            paymentMethod = details.getString("paymentMethod");
            publicTransport = details.getString("publicTransport");
            carpark = details.getString("carpark");
            rating = details.getString("rating");
            queueNumber = details.getString("queueNumber");
            newQueueDetails = details.getString("newQueueDetails");

        }

        sp = getSharedPreferences("login", MODE_PRIVATE);

        QDqueueNumberTV = findViewById(R.id.QDqueueNumberTV);
        QDclinicNameTV = findViewById(R.id.QDclinicNameTV);
        QDqueueCountTV = findViewById(R.id.QDqueueCountTV);
        QDinformationAccurateTV = findViewById(R.id.QDinformationAccurateTV);
        QDclinicNameCardTV = findViewById(R.id.QDclinicNameCardTV);
        QDclinicAddressCardTV = findViewById(R.id.QDclinicAddressCardTV);
        QDclinicOpeningHoursCardTV = findViewById(R.id.QDclinicOpeningHoursCardTV);
        QDclinicContactCardTV = findViewById(R.id.QDclinicContactCardTV);
        QDdoctorInTodayCardTV = findViewById(R.id.QDdoctorInTodayCardTV);
        QDclinicTypeCardTV = findViewById(R.id.QDclinicTypeCardTV);
        QDclinicProgrammeCardTV = findViewById(R.id.QDclinicProgrammeCardTV);
        QDpaymentCardTV = findViewById(R.id.QDpaymentCardTV);
        QDpublicTransportCardTV = findViewById(R.id.QDpublicTransportCardTV);
        QDcarparkCardTV = findViewById(R.id.QDcarparkCardTV);
        QDclinicRatingCardTV = findViewById(R.id.QDclinicRatingCardTV);
        QDquickCheckInTV = findViewById(R.id.QDquickCheckInTV);
        QDqrTextTV = findViewById(R.id.QDqrTextTV);
        QDcheckedInNoteTV = findViewById(R.id.QDcheckedInNoteTV);
        QDcancelQueueMB = findViewById(R.id.QDcancelQueueMB);
        QDbackIV = findViewById(R.id.QDbackIV);
        QDquickRegisterCollapsedCV = findViewById(R.id.QDquickCheckInCollapsedCV);
        QDquickRegisterFullCV = findViewById(R.id.QDquickCheckInFullCV);
        QDclinicDetailsCollapsedCV = findViewById(R.id.QDclinicDetailsCollapsedCV);
        QDclinicDetailsFullCV = findViewById(R.id.QDclinicDetailsFullCV);
        QDquickRegisterMoreIV = findViewById(R.id.QDquickCheckInMoreIV);
        QDquickRegisterCloseIV = findViewById(R.id.QDquickCheckInCloseIV);
        QDclinicDetailsMoreIV = findViewById(R.id.QDclinicDetailsMoreIV);
        QDclinicDetailsCloseIV = findViewById(R.id.QDclinicDetailsCloseIV);
        QDquickRegisterQRcodeIV = findViewById(R.id.QDquickCheckInQRcodeIV);
        QDcheckedInIV = findViewById(R.id.QDcheckedInIV);

        JSONArray queueDetails = null;
        try {
            queueDetails = new JSONArray(newQueueDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (queueDetails.length() == 0) {
            patientsAhead = "0";
        } else {

            queueNumber = sp.getString("queueNumber", "0");

            for (int i = 0; i < queueDetails.length(); i++) {

                try {
                    JSONObject individualQueue = queueDetails.getJSONObject(i);
                    if (String.valueOf(individualQueue.getInt("queueNumber")).equals(queueNumber)) {
                        patientsAhead = String.valueOf(i);
                    } else {
                        Log.e("Error at queueDetals.length", "Error at queueDetails.length");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        if (Integer.parseInt(patientsAhead) <= 10) {
            QDqueueCountTV.setTextColor(getColor(R.color.DarkGreen));
        } else if (Integer.parseInt(patientsAhead) <= 30) {
            QDqueueCountTV.setTextColor(getColor(R.color.SecondaryYellow));
        } else {
            QDqueueCountTV.setTextColor(getColor(R.color.BurgandyRed));
        }

        QDqueueCountTV.setText(patientsAhead);
        QDqueueNumberTV.setText(queueNumber);
        QDclinicNameTV.setText(clinicName);

        QDclinicNameCardTV.setText(clinicName);
        QDclinicAddressCardTV.setText(address);
        QDclinicOpeningHoursCardTV.setText(openingHours);
        QDclinicContactCardTV.setText(phoneNumber);
        QDdoctorInTodayCardTV.setText(doctor);
        QDclinicTypeCardTV.setText(clinicType);
        QDclinicProgrammeCardTV.setText(clinicProgramme);
        QDpaymentCardTV.setText(paymentMethod);
        QDpublicTransportCardTV.setText(publicTransport);
        QDcarparkCardTV.setText(carpark);
        QDclinicRatingCardTV.setText(rating);

        setUpPusher();

        QDbackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainPage();
            }
        });

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("'Information accurate as of' dd/MM/yy HH:mm'hrs' '(Queue Status automatically updates)'");
        String currentDateandTime = sdf.format(new Date());
        QDinformationAccurateTV.setText(currentDateandTime);

        QDcancelQueueMB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(QueueDetails.this);
                builder.setMessage("Are you sure you want to cancel your queue number? You'll need to retake a new number if you change your mind later on.");

                // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                builder.setCancelable(false);

                builder.setPositiveButton("No, Keep My Queue Number", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.dismiss();
                });

                // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("Yes, Cancel It", (DialogInterface.OnClickListener) (dialog, which) -> {
                    cancelQueueNumber();
                });


                AlertDialog alertDialog = builder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getColor(R.color.DarkGreen));
                    }
                });

                alertDialog.show();

            }
        });


        QDquickRegisterMoreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDquickRegisterCollapsedCV.setVisibility(View.GONE);
                QDquickRegisterFullCV.setVisibility(View.VISIBLE);
            }
        });

        QDquickRegisterCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDquickRegisterCollapsedCV.setVisibility(View.VISIBLE);
                QDquickRegisterFullCV.setVisibility(View.GONE);
            }
        });


        QDclinicDetailsMoreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDclinicDetailsCollapsedCV.setVisibility(View.GONE);
                QDclinicDetailsFullCV.setVisibility(View.VISIBLE);

            }
        });

        QDclinicDetailsCloseIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QDclinicDetailsCollapsedCV.setVisibility(View.VISIBLE);
                QDclinicDetailsFullCV.setVisibility(View.GONE);
            }
        });

        if (!sp.getBoolean("checkedIn", false)) {
            showQRCode();
        } else {
            showCheckedInLogo();
        }

    }

    private void showQRCode() {
        String QRcodeText = sp.getString("uniqueAppointmentToken", null);

        if (QRcodeText == null || QRcodeText.isEmpty()) {
            QRcodeText = "Error in QR Code. Please manual register";
        }

        try {
            MultiFormatWriter mWriter = new MultiFormatWriter();
            BitMatrix mMatrix = mWriter.encode(QRcodeText, BarcodeFormat.QR_CODE, 480, 480);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap qrCodeBitmap = mEncoder.createBitmap(mMatrix);

            QDquickRegisterQRcodeIV.setImageBitmap(qrCodeBitmap);
            QDqrTextTV.setText(QRcodeText);

        } catch (WriterException ex) {
            Toast.makeText(this, "Unable to generate QR code. Please try again.", Toast.LENGTH_LONG).show();
        }
    }


    private void showCheckedInLogo() {
        QDquickRegisterQRcodeIV.setVisibility(View.GONE);
        QDqrTextTV.setVisibility(View.GONE);
        QDcheckedInIV.setVisibility(View.VISIBLE);
        QDcheckedInNoteTV.setVisibility(View.VISIBLE);
        QDquickCheckInTV.setText("You have successfully checked in! Please wait for the doctor to call you");
    }

    private void setUpPusher() {
        initialisePusher.init(this);
        Channel channel = null;

        try {
            channel = initialisePusher.getChannel(clinicId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        channel.bind(sp.getString("uniqueAppointmentToken", ""), new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {

                try {
                    JSONObject eventObject = new JSONObject(event.getData());

                    new Handler(Looper.getMainLooper()).post(() -> {

                        try {
                            eventObject.getBoolean("checkedIn");
                            sp.edit().putBoolean("checkedIn", true).apply();
                            QDcheckedInNoteTV.setText("Queue number may not be called in sequence. Please be patient!");
                            showCheckedInLogo();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (JSONException e) {
                    Log.e("JSON Parsing Error", "Failed to parse event data", e);
                }
            }
        });
    }



    private void cancelQueueNumber() {
        RequestQueue queue = Volley.newRequestQueue(QueueDetails.this);
        String URL = "https://queder-a59fe69a45d0.herokuapp.com/queuesystem/cancelqueuenumber";

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Log.e("cancelQueueNumber", jsonObject.toString());

                    if (jsonObject.getString("status").equals("Success")) {
                        JSONObject newClinicObject = new JSONObject(jsonObject.getString("result"));

                        Intent IndividualItem = new Intent(getApplicationContext(), IndividualClinic.class);
                        String[] individualItemKeys = {"clinicId", "imageUrl", "clinicName", "doctor", "address", "town", "phoneNumber", "price",
                                "rating", "currentQueue", "queueDetails", "doctorPublicKey"};

                        for (String key : individualItemKeys) {
                            IndividualItem.putExtra(key, newClinicObject.getString(key));
                        }

                        sp.edit()
                                .putBoolean("gotQueue", false)
                                .remove("clinicId")
                                .remove("clinicName")
                                .remove("queueNumber")
                                .remove("address")
                                .remove("imageUrl")
                                .remove("checkedIn")
                                .apply();

                        startActivity(IndividualItem);
                        finish();
                    } else {
                        Toast.makeText(QueueDetails.this, "Error in cancelling queue number",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(QueueDetails.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();

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



    @Override
    protected void onResume() {

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                SimpleDateFormat sdf = new SimpleDateFormat("'Information accurate as of' dd/MM/yy HH:mm'hrs' '(Queue Status automatically updates)'");
                String currentDateandTime = sdf.format(new Date());
                QDinformationAccurateTV.setText(currentDateandTime);
            }
        }, delay);

        super.onResume();
    }

    public void toMainPage() {
        Intent Mainpage = new Intent(getApplicationContext(), Mainpage.class);
        startActivity(Mainpage);
        finish();
    }
}
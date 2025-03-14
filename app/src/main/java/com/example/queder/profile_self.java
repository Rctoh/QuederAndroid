package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import com.example.queder.Login;
import com.example.queder.Mainpage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class profile_self extends AppCompatActivity {

    TextView PSdisplayNameTV, PSemailTV;
    ImageView PSprofilePicIV;
    LinearLayout PSlogoutLL, PSeditProfileLL, PSaboutLL;
    ImageButton PSbackArrowIB;
    SharedPreferences sp;
    RequestQueue rQueue;
    private String URL = "https://tohruichen.com/recipe/account/getprofiledetails.php";
    String ProfilePic, DisplayName, Email, PhoneNumber1, PhoneNumber2, Bio, Gender;

    @Override
    public void onBackPressed() {
        toMainpage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_self);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        PSprofilePicIV = findViewById(R.id.PSprofilePicIV);
        PSlogoutLL = findViewById(R.id.PSlogoutLL);
        PSdisplayNameTV = findViewById(R.id.PSfullNameTV);
        PSemailTV = findViewById(R.id.PSemailTV);
        PSbackArrowIB = findViewById(R.id.PSbackArrowIB);
        PSeditProfileLL = findViewById(R.id.PSeditprofileLL);
        PSaboutLL = findViewById(R.id.PSaboutLL);

        DisplayName = sp.getString("fullName", "User has not entered Full Name");
        Email = sp.getString("email", "User has not entered Email");

        getProfileDetails(DisplayName);

        PSdisplayNameTV.setText(DisplayName);
        PSemailTV.setText(Email);


        PSbackArrowIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMainpage();
            }
        });

//        PSeditProfileLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toEditProfile();
//            }
//        });

        PSlogoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sp.edit().putBoolean("logged", false).apply();
                toLogin();
            }
        });

//        PSaboutLL.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toAbout();
//            }
//        });



    }


    public void getProfileDetails(String DisplayName) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URL,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));

                            if (jsonObject.getString("message").equals("Successful!")) {
                                ProfilePic = jsonObject.getString("ProfilePic");
                                if (!ProfilePic.isEmpty()) {
                                    sp.edit().putString("ProfilePic", ProfilePic).apply();
                                    Picasso.get().load(ProfilePic).into(PSprofilePicIV);
                                }
                                Email = jsonObject.getString("Email");
                                PhoneNumber1 = jsonObject.getString("PhoneNumber1");
                                PhoneNumber2 = jsonObject.getString("PhoneNumber2");
                                Bio =  jsonObject.getString("Bio");
                                Gender = jsonObject.getString("Gender");

                            } else {
                                Toast.makeText(profile_self.this, "Error", Toast.LENGTH_SHORT).show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(profile_self.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
                        Log.e("Test123", String.valueOf(error.getMessage()));
                        error.printStackTrace();
                    }
                }) {

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params2 = new HashMap<>();
                params2.put("DisplayName", DisplayName);
                return params2;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(profile_self.this);
        rQueue.add(volleyMultipartRequest);
    }

    private void toLogin() {
        Intent login = new Intent(this, Login.class);
        startActivity(login);
        finish();
    }

    private void toMainpage() {
        Intent Mainpage = new Intent(this, com.example.queder.Mainpage.class);
        startActivity(Mainpage);
        finish();
    }

//    private void toEditProfile() {
//        Intent EditProfile = new Intent(this, EditProfile.class);
//
//
//        Bundle bundle = new Bundle();
//
//        EditProfile.putExtra("ProfilePic", ProfilePic);
//        EditProfile.putExtra("DisplayName", DisplayName);
//        EditProfile.putExtra("Email", Email);
//        EditProfile.putExtra("PhoneNumber1", PhoneNumber1);
//        EditProfile.putExtra("PhoneNumber2", PhoneNumber2);
//        EditProfile.putExtra("Bio", Bio);
//        EditProfile.putExtra("Gender", Gender);
//        startActivity(EditProfile);
//
//        finish();
//    }

//    private void toAbout() {
//        Intent aboutRecipe_website = new Intent(this, aboutRecipe_website.class);
//        startActivity(aboutRecipe_website);
//
//    }

    public class VolleyMultipartRequest extends Request<NetworkResponse> {

        private final String twoHyphens = "--";
        private final String lineEnd = "\r\n";
        private final String boundary = "apiclient-" + System.currentTimeMillis();

        private Response.Listener<NetworkResponse> mListener;
        private Response.ErrorListener mErrorListener;
        private Map<String, String> mHeaders;


        public VolleyMultipartRequest(int method, String url,
                                      Response.Listener<NetworkResponse> listener,
                                      Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            this.mListener = listener;
            this.mErrorListener = errorListener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return (mHeaders != null) ? mHeaders : super.getHeaders();
        }

        @Override
        public String getBodyContentType() {
            return "multipart/form-data;boundary=" + boundary;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);

            try {
                // populate text payload
                Map<String, String> params = getParams();
                if (params != null && params.size() > 0) {
                    textParse(dos, params, getParamsEncoding());
                }

                // populate data byte payload
                Map<String, DataPart> data = getByteData();
                if (data != null && data.size() > 0) {
                    dataParse(dos, data);
                }

                // close multipart form data after text and file data
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                return bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Custom method handle data payload.
         *
         * @return Map data part label with data byte
         * @throws AuthFailureError
         */
        protected Map<String, DataPart> getByteData() throws AuthFailureError {
            return null;
        }

        @Override
        protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(
                        response,
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (Exception e) {
                return Response.error(new ParseError(e));
            }
        }

        @Override
        protected void deliverResponse(NetworkResponse response) {
            mListener.onResponse(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            mErrorListener.onErrorResponse(error);
        }

        /**
         * Parse string map into data output stream by key and value.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param params           string inputs collection
         * @param encoding         encode the inputs, default UTF-8
         * @throws IOException
         */
        private void textParse(DataOutputStream dataOutputStream, Map<String, String> params, String encoding) throws IOException {
            try {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    buildTextPart(dataOutputStream, entry.getKey(), entry.getValue());
                }
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Encoding not supported: " + encoding, uee);
            }
        }

        /**
         * Parse data into data output stream.
         *
         * @param dataOutputStream data output stream handle file attachment
         * @param data             loop through data
         * @throws IOException
         */
        private void dataParse(DataOutputStream dataOutputStream, Map<String, DataPart> data) throws IOException {
            for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                buildDataPart(dataOutputStream, entry.getValue(), entry.getKey());
            }
        }

        /**
         * Write string data into header and data output stream.
         *
         * @param dataOutputStream data output stream handle string parsing
         * @param parameterName    name of input
         * @param parameterValue   value of input
         * @throws IOException
         */
        private void buildTextPart(DataOutputStream dataOutputStream, String parameterName, String parameterValue) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" + parameterName + "\"" + lineEnd);
            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(parameterValue + lineEnd);
        }

        /**
         * Write data file into header and data output stream.
         *
         * @param dataOutputStream data output stream handle data parsing
         * @param dataFile         data byte as DataPart from collection
         * @param inputName        name of data input
         * @throws IOException
         */
        private void buildDataPart(DataOutputStream dataOutputStream, DataPart dataFile, String inputName) throws IOException {
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"" +
                    inputName + "\"; filename=\"" + dataFile.getFileName() + "\"" + lineEnd);
            if (dataFile.getType() != null && !dataFile.getType().trim().isEmpty()) {
                dataOutputStream.writeBytes("Content-Type: " + dataFile.getType() + lineEnd);
            }
            dataOutputStream.writeBytes(lineEnd);

            ByteArrayInputStream fileInputStream = new ByteArrayInputStream(dataFile.getContent());
            int bytesAvailable = fileInputStream.available();

            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
        }

        class DataPart {
            private String fileName;
            private byte[] content;
            private String type;

            public DataPart() {
            }

            DataPart(String name, byte[] data) {
                fileName = name;
                content = data;
            }

            String getFileName() {
                return fileName;
            }

            byte[] getContent() {
                return content;
            }

            String getType() {
                return type;
            }

        }
    }

}
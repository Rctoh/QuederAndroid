package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class OnBoard_3 extends AppCompatActivity {

    ImageView OB3nextIV;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_3);

        OB3nextIV = findViewById(R.id.OB3nextIV);
        sp = getSharedPreferences("login", MODE_PRIVATE);

        OB3nextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sp.edit().putBoolean("firstTimer", false).apply();

                Intent Login = new Intent(getApplicationContext(), Login.class);
                startActivity(Login);
                finish();
            }
        });
    }
}
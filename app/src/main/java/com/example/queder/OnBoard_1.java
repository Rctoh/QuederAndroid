package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class OnBoard_1 extends AppCompatActivity {

    ImageView OB1nextIV;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_1);



        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (!sp.getBoolean("firstTimer", true)) {
            Intent Login = new Intent(getApplicationContext(), Login.class);
            startActivity(Login);
            finish();
        }

        OB1nextIV = findViewById(R.id.OB1nextIV);

        OB1nextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent OnBoard2 = new Intent(getApplicationContext(), OnBoard_2.class);
                startActivity(OnBoard2);
                finish();
            }
        });
    }
}
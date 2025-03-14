package com.example.queder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class OnBoard_2 extends AppCompatActivity {

    ImageView OB2nextIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onboard_2);

        OB2nextIV = findViewById(R.id.OB2nextIV);

        OB2nextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent OnBoard3 = new Intent(getApplicationContext(), OnBoard_3.class);
                startActivity(OnBoard3);
                finish();
            }
        });
    }
}
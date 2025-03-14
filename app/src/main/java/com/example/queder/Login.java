package com.example.queder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.w3c.dom.Text;




public class Login extends AppCompatActivity {

    ImageButton LgetStartedIB;
    SharedPreferences sp;
    TextView LsignUpLaterTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);



        LgetStartedIB = findViewById(R.id.LgetStartedIB);
        LsignUpLaterTV = findViewById(R.id.LsignUpLaterTV);

        sp = getSharedPreferences("login", MODE_PRIVATE);
        if (sp.getBoolean("logged", false)) {
            Intent Mainpage = new Intent(this, Mainpage.class);
            startActivity(Mainpage);
            finish();
        }

        LgetStartedIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkEmail = new Intent(getApplicationContext(), CheckEmail.class);
                startActivity(checkEmail);
                finish();
            }
        });

        LsignUpLaterTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent OnBoard1 = new Intent(getApplicationContext(), OnBoard_1.class);
                startActivity(OnBoard1);
                finish();
            }
        });
    }

}
package com.example.spkr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PageLogin extends AppCompatActivity {
    private Button btnwelcome, btn_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_login);

        btnwelcome = findViewById(R.id.WELCOME);
        btn_email = findViewById(R.id.btn_emailActivity);

        btnwelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity1();
            }
        });

        btn_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Email_RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    public void openActivity1() {
        Intent intent2 = new Intent(PageLogin.this , AdminLogin.class);
        startActivity(intent2);
    }

}

package com.ddinc.ftpnow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntenet = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(homeIntenet);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}

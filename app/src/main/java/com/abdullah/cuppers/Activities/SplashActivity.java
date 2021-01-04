package com.abdullah.cuppers.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.abdullah.cuppers.R;

public class SplashActivity extends AppCompatActivity {

    static final int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ProgressBar splashProgressBar = findViewById(R.id.splashProgressBar);
        splashProgressBar.setVisibility(View.VISIBLE);
        splashProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent toSplashActivity = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(toSplashActivity);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}

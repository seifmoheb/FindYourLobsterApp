package com.app.findyourlobster.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;

import maes.tech.intentanim.CustomIntent;

public class SplashScreen extends AppCompatActivity {

    Window window;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    startActivity(new Intent(SplashScreen.this, SecondSplashScreen.class));
                    CustomIntent.customType(SplashScreen.this, "fadein-to-fadeout");
                    finish();
                }

            }

        }, 2000);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

            }
        };
        handler.removeCallbacks(runnable);

    }
}
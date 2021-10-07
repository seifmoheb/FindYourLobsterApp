package com.app.findyourlobster.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.findyourlobster.R;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;

import maes.tech.intentanim.CustomIntent;
import retrofit2.http.GET;

public class SecondSplashScreen extends AppCompatActivity implements PurchasesUpdatedListener {

    Window window;
    ImageView imageView;
    FirebaseAuth auth;
    private Handler handler = new Handler();
    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_splash_screen);
        imageView = findViewById(R.id.logo);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        auth = FirebaseAuth.getInstance();
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    //load();

                }
            }

            @Override
            public void onBillingServiceDisconnected() {

            }
        });
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                    if (sharedPreferences.getBoolean("NewUser", true)) {
                        startActivity(new Intent(SecondSplashScreen.this, IntroActivity.class));
                    } else {
                        if (auth.getCurrentUser() == null) {
                            startActivity(new Intent(SecondSplashScreen.this, LogActivity.class));

                        } else {
                            /*RequestQueue queue = Volley.newRequestQueue(SecondSplashScreen.this);
                            String url ="https://www.googleapis.com/androidpublisher/v1/applications/com.app.findyourlobster/subscriptions/premiumsubscription/purchases/"+;

// Request a string response from the provided URL.
                            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            // Display the first 500 characters of the response string.
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                }
                            });

// Add the request to the RequestQueue.
                            queue.add(stringRequest);*/
                            startActivity(new Intent(SecondSplashScreen.this, MainActivity.class));
                        }
                    }
                    CustomIntent.customType(SecondSplashScreen.this, "right-to-left");
                    finish();
                }

            }

        }, 6900);

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

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {
        Purchase purchase = list.get(0);

    }


}
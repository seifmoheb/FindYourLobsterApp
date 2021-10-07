package com.app.findyourlobster.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.PriceChangeConfirmationListener;
import com.android.billingclient.api.PriceChangeFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.app.findyourlobster.R;
import com.app.findyourlobster.data.Constants;
import com.app.findyourlobster.data.PageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.api.Billing;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements LocationListener, PurchasesUpdatedListener {

    public static Context context;
    private static double longit;
    private static double latt;
    Window window;
    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pagerAdapter;
    LocationManager locationManager;
    FirebaseUser user;

    public static double getLongit() {
        return longit;
    }

    public void setLongit(double longit) {
        this.longit = longit;
    }

    public static double getLatt() {
        return latt;
    }

    public void setLatt(double latt) {
        this.latt = latt;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        context = this;
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 500, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            //Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        String emailUpdated = null;
                        final FirebaseUser user = auth.getCurrentUser();
                        if (user.getEmail().contains(".")) {
                            emailUpdated = user.getEmail().replace(".", " ");
                        } else {
                            emailUpdated = user.getEmail();
                        }
                        // Get new FCM registration token
                        String token = task.getResult();
                        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated);
                        Map mapName = new HashMap();
                        mapName.put("token", token);
                        myRef2.updateChildren(mapName);
                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        //Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setupBillingClient();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.getBoolean("location", false)) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                requestPermissions(permissions, PackageManager.PERMISSION_GRANTED);

            }
        }

    }
    public void setupBillingClient() {
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (requestCode == PackageManager.PERMISSION_GRANTED) {
            editor.putBoolean("location", true);
            editor.commit();
        } else if (requestCode == PackageManager.PERMISSION_DENIED) {
            editor.putBoolean("location", false);
            editor.commit();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setLongit(location.getLongitude());
        setLatt(location.getLatitude());
        FirebaseDatabase database;
        DatabaseReference myRef;
        FirebaseAuth auth;
        String emailUpdated;
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser().getEmail().contains(".")) {
            emailUpdated = auth.getCurrentUser().getEmail().replace(".", " ");
        } else {
            emailUpdated = auth.getCurrentUser().getEmail();
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("AllUsersData").child(emailUpdated).child("userInfo").child("location");
        Map mapLoc = new HashMap();
        mapLoc.put("location", getLatt() + "," + getLongit());
        myRef.updateChildren(mapLoc);

        DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated);
        final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("longit", (float) getLongit());
        editor.putFloat("latt", (float) getLatt());
        editor.commit();


        Map Loc = new HashMap();
        Loc.put("location", HomeFragment.encode(getLatt(), getLongit(), 5));
        myRef4.updateChildren(Loc);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }


    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please Enable GPS and Internet", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private String getDOB() {
        int day, month, year;
        String data[] = user.getDisplayName().split("_");
        String[] date_splitted;
        Calendar calendar, dob;
        String ageString = "";
        date_splitted = data[1].split("/");
        day = Integer.parseInt(date_splitted[0]);
        month = Integer.parseInt(date_splitted[1]);
        year = Integer.parseInt(date_splitted[2]);
        calendar = Calendar.getInstance();
        dob = Calendar.getInstance();
        dob.set(year, month, day);
        int age = calendar.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (calendar.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return ageString = String.valueOf(age);
    }


    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }

}
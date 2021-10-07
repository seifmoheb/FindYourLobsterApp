package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.MoreInfoViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

import static com.app.findyourlobster.ui.MainActivity.getLatt;
import static com.app.findyourlobster.ui.MainActivity.getLongit;

public class MoreInfoActivity extends AppCompatActivity {

    Window window;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FloatingActionButton next, previous, exit;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;
    String emailUpdated;
    ViewPager viewPager;
    List<String> galleryItems = new ArrayList<>();
    int position = 0;
    MoreInfoViewPager moreinfoViewPager;
    TextView counter, name, age, gender, location, bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        viewPager = findViewById(R.id.MoreInfoViewPager);
        exit = findViewById(R.id.exit);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            emailUpdated = bundle.getString("email");
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("AllUsersData").child(emailUpdated).child("images");
        myRef2 = database.getReference("AllUsersData").child(emailUpdated).child("userInfo");
        counter = findViewById(R.id.counter);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        gender = findViewById(R.id.gender);
        location = findViewById(R.id.location);
        bio = findViewById(R.id.bio);
        moreinfoViewPager = new MoreInfoViewPager(this, galleryItems);
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("age").exists()) {
                        age.setText(snapshot.child("age").child("age").getValue().toString());
                    }
                    if (dataSnapshot.child("displayName").exists()) {
                        name.setText(snapshot.child("name").child("displayName").getValue().toString());

                    }
                    if (dataSnapshot.child("description").exists()) {
                        bio.setText(snapshot.child("description").child("description").getValue().toString());
                    }
                    if (dataSnapshot.child("location").exists()) {
                        String[] latAndLong = snapshot.child("location").child("location").getValue().toString().split(",");
                        DecimalFormat precision = new DecimalFormat("0");
                        String finalDist = precision.format(distance(Double.parseDouble(latAndLong[0]), Double.parseDouble(latAndLong[1]), getLatt(), getLongit()));
                        location.setText(finalDist + " Miles away");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.child("image").getValue().toString();
                galleryItems.add(value);
                counter.setText(position + 1 + "/" + galleryItems.size());
                moreinfoViewPager.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewPager.setAdapter(moreinfoViewPager);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(MoreInfoActivity.this, "up-to-down");

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = viewPager.getCurrentItem();
                if (position < galleryItems.size()) {
                    position++;
                    if (position != galleryItems.size()) {
                        counter.setText(position + 1 + "/" + galleryItems.size());
                    }
                    viewPager.setCurrentItem(position);
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                counter.setText(position + 1 + "/" + galleryItems.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = viewPager.getCurrentItem();
                if (position > 0) {
                    position--;
                    counter.setText(position + 1 + "/" + galleryItems.size());
                    viewPager.setCurrentItem(position);
                }
            }
        });

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 0.8684;


        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
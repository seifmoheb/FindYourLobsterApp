package com.app.findyourlobster.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.ConstantAnchorMediaController;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDisplayActivity extends AppCompatActivity {

    FloatingActionButton back,download;
    TextView dateAndTime;
    ImageView imageView;
    String senderString,date,time,uri;
    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        back = findViewById(R.id.back);
        dateAndTime = findViewById(R.id.dateAndTime);
        download = findViewById(R.id.download);

        imageView = findViewById(R.id.imageView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                senderString = "";
                uri = "";
                date = "";
                time = "";
            } else {
                senderString = extras.getString("sender");
                uri = extras.getString("uri");
                date = extras.getString("date");
                time = extras.getString("time");

            }
        } else {
            senderString = (String) savedInstanceState.getSerializable("sender");
            uri = (String) savedInstanceState.getSerializable("uri");
            date = (String) savedInstanceState.getSerializable("date");
            time = (String) savedInstanceState.getSerializable("time");
        }
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        String[] times = time.split(":");

        dateAndTime.setText(date+", "+times[0]+":"+times[1]);
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ChatActivity.context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);
        requestOptions.skipMemoryCache(true);
        requestOptions.fitCenter();

        Glide.with(ChatActivity.context)
                .asBitmap()
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);
        switch (action){
            case (MotionEvent.ACTION_DOWN):
                finish();
                return true;
        }
        return super.onTouchEvent(event);
    }
}
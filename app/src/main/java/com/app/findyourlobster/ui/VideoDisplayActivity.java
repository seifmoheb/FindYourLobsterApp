package com.app.findyourlobster.ui;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.ConstantAnchorMediaController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VideoDisplayActivity extends AppCompatActivity {

    FloatingActionButton back, download;
    TextView dateAndTime;
    VideoView videoView;
    String senderString, date, time, uri;
    FirebaseUser user;
    FirebaseAuth auth;
    MediaController mediaController;
    ProgressBar progressBar;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_display);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        back = findViewById(R.id.back);
        download = findViewById(R.id.download);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        dateAndTime = findViewById(R.id.dateAndTime);

        videoView = findViewById(R.id.videoView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);                  }
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
        String[] times = time.split(":");

        dateAndTime.setText(date + ", " + times[0] + ":" + times[1]);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String emailUpdated = "";
        if (user.getEmail().contains(".")) {
            emailUpdated = user.getEmail().replace(".", " ");
        } else {
            emailUpdated = user.getEmail();
        }

        mediaController = new ConstantAnchorMediaController(this,
                videoView);

        try {
            mediaController.setAnchorView(videoView);

            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(Uri.parse(uri));
        } catch (Exception e) {

        }
        videoView.seekTo(videoView.getCurrentPosition() + 3000);
        videoView.start();
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });

            }
        });
    }


}
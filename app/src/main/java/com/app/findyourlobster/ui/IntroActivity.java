package com.app.findyourlobster.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.IntroViewPagerAdapter;
import com.app.findyourlobster.data.ScreenItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    IntroViewPagerAdapter introViewPagerAdapter;
    Button next, getstarted;
    int position = 0;
    Animation btnAnim;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();
        viewPager = findViewById(R.id.viewpager_intro);
        tabLayout = findViewById(R.id.tabLayout2);
        next = findViewById(R.id.next);
        getstarted = findViewById(R.id.getstarted);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Find your lobster!", "Welcome to Find Your Lobster, now you can meet people with same interests as you, chat and make new friends too", R.drawable.logo));
        mList.add(new ScreenItem("Almost there!", "Your lobster might be waiting for you as well, choose your favourite pictures, star the one you want to appear first and describe your interests and favourite things.", R.drawable.second_intro));
        mList.add(new ScreenItem("Here we go!", "Swipe right/Frame person you are interested in.\nSwipe left/Dislike the unlucky person. Simple and easy until you both are matched! \nAlways remember to have Fun!", R.drawable.intro_image_2));
        auth = FirebaseAuth.getInstance();

        introViewPagerAdapter = new IntroViewPagerAdapter(3, this, mList);
        viewPager.setAdapter(introViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = viewPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    viewPager.setCurrentItem(position);
                }
                if (position == mList.size() - 1) {
                    loadLAstScreen();
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLAstScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getstarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("NewUser", false);
                editor.commit();
                if (auth.getCurrentUser() == null) {
                    startActivity(new Intent(IntroActivity.this, LogActivity.class));

                } else {
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                }
                CustomIntent.customType(IntroActivity.this, "left-to-right");
                finish();
            }
        });

    }

    private void loadLAstScreen() {
        next.setVisibility(View.INVISIBLE);
        getstarted.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        getstarted.setAnimation(btnAnim);
    }
}
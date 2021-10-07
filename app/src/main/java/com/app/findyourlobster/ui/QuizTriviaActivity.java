package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.QuizItems;
import com.app.findyourlobster.data.QuizViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class QuizTriviaActivity extends AppCompatActivity {

    Window window;
    ViewPager viewPager;
    TabLayout tabLayout;
    QuizViewPagerAdapter quizViewPagerAdapter;
    Button next, submit;
    int position = 0;
    FirebaseDatabase database;
    String emailUpdated;
    DatabaseReference myRef;
    FloatingActionButton back;
    Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_trivia);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        viewPager = findViewById(R.id.QuizViewPager);
        tabLayout = findViewById(R.id.tabLayout2);
        next = findViewById(R.id.next);
        submit = findViewById(R.id.submit);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        final List<QuizItems> mList = new ArrayList<>();
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("TriviaQuestions");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                mList.add(new QuizItems(snapshot.child("question").getValue().toString(), snapshot.child("choiceOne").getValue().toString(), snapshot.child("choiceTwo").getValue().toString(), snapshot.child("choiceThree").getValue().toString(), snapshot.child("choiceFour").getValue().toString(), snapshot.child("correctAnswer").getValue().toString()));
                quizViewPagerAdapter.notifyDataSetChanged();

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
        quizViewPagerAdapter = new QuizViewPagerAdapter(mList.size(), this, mList);
        viewPager.setAdapter(quizViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = viewPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    viewPager.setCurrentItem(position);
                }
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()) {
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
    }

    private void loadLAstScreen() {
        next.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
        submit.setAnimation(btnAnim);
    }

}
package com.app.findyourlobster.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.app.findyourlobster.R;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;

import static com.app.findyourlobster.ui.GalleryActivity.base64Encoder;
import static com.app.findyourlobster.ui.GalleryActivity.secureRandom;

public class ProfileFragment extends Fragment {

    TextView name, about, gallery, settings, quiz, requests, goPremium;
    ImageView profileImage;
    FloatingActionButton logOut, invite;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String[] data;
    FirebaseDatabase database;
    String emailUpdated;
    DatabaseReference myRef;
    int day, month, year;
    String[] date_splitted;
    Calendar calendar, dob;
    String ageString = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static String generateToken() {

        byte[] randomBytes = new byte[6];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        logOut = root.findViewById(R.id.logout);
        invite = root.findViewById(R.id.invite);
        name = root.findViewById(R.id.name);
        about = root.findViewById(R.id.about);
        quiz = root.findViewById(R.id.quiz);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        profileImage = root.findViewById(R.id.profileImage);
        gallery = root.findViewById(R.id.gallery);
        settings = root.findViewById(R.id.settings);
        requests = root.findViewById(R.id.requests);
        goPremium = root.findViewById(R.id.premium);


        data = currentUser.getDisplayName().split("_");
        Log.i("name", data[0]);
        Log.i("data", currentUser.getDisplayName());

        if (currentUser.getEmail().contains(".")) {
            emailUpdated = currentUser.getEmail().replace(".", " ");
        } else {
            emailUpdated = currentUser.getEmail();
        }
        name.setText(data[0] + ", " + getDOB());

        invite.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getString(R.string.appLink) + generateToken();
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Check this out");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("UsersProfileImages").child(emailUpdated).child("profileImage");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (MainActivity.context != null) {
                    Glide.with(MainActivity.context)
                            .load(snapshot.getValue().toString())
                            .placeholder(R.color.colorPrimary)
                            .into(profileImage);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (MainActivity.context != null) {

                    Glide.with(MainActivity.context)
                            .load(snapshot.getValue().toString())
                            .placeholder(R.color.colorPrimary)
                            .into(profileImage);
                }
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
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.context, AboutActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.context, "left-to-right");
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.context, GalleryAll.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.context, "left-to-right");
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.context, SettingsActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.context, "left-to-right");
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(MainActivity.context, LogActivity.class));
                CustomIntent.customType(MainActivity.context, "up-to-down");
                ((MainActivity) MainActivity.context).finish();
            }
        });
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.context, QuizTriviaActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.context, "left-to-right");
            }
        });
        requests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.context, RequestsActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.context, "left-to-right");
            }
        });
        goPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.context, GoPremiumActivity.class);
                startActivity(intent);
                CustomIntent.customType(MainActivity.context, "left-to-right");
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUser.reload();
        data = currentUser.getDisplayName().split("_");
        name.setText(data[0] + ", " + getDOB());
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Glide.with(MainActivity.context)
                        .load(snapshot.getValue().toString())
                        .placeholder(R.color.colorPrimary)
                        .into(profileImage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Glide.with(MainActivity.context)
                        .load(snapshot.getValue().toString())
                        .placeholder(R.color.colorPrimary)
                        .into(profileImage);
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
    }

    private String getDOB() {
        date_splitted = data[1].split("/");
        if (!date_splitted[0].equals("dd")) {
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

        } else {
            return "";
        }
    }
}
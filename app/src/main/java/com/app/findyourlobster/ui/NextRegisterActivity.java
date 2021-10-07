package com.app.findyourlobster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class NextRegisterActivity extends AppCompatActivity implements LocationListener {

    private static double longit;
    private static double latt;
    Window window;
    Button register, back;
    FirebaseAuth auth;
    String email, password;
    EditText displayname, date, bio;
    String selectedDate;
    RelativeLayout loadingLayout;
    String emailUpdated;
    LocationManager locationManager;

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
        setContentView(R.layout.activity_next_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 500, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        loadingLayout = findViewById(R.id.loadingLayoutNextRegister);
        register = findViewById(R.id.register);
        auth = FirebaseAuth.getInstance();
        displayname = findViewById(R.id.name);
        bio = findViewById(R.id.bio);
        date = findViewById(R.id.date);
        back = findViewById(R.id.back);
        date.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);

                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    date.setText(current);
                    date.setSelection(sel < current.length() ? sel : current.length());


                }
            }


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                email = "";
                password = "";
            } else {
                email = extras.getString("email");
                password = extras.getString("password");
            }
        } else {
            email = (String) savedInstanceState.getSerializable("email");
            password = (String) savedInstanceState.getSerializable("pass");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                CustomIntent.customType(NextRegisterActivity.this, "right-to-left");

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!displayname.getText().toString().equals("") && !date.getText().toString().equals("") && !bio.getText().toString().equals("")) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(NextRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        final FirebaseUser user = auth.getCurrentUser();
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(displayname.getText().toString() + "_" + date.getText().toString() + "_" + bio.getText().toString())
                                                .build();
                                        user.updateProfile(profileChangeRequest)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (user.getEmail().contains(".")) {
                                                                emailUpdated = user.getEmail().replace(".", " ");
                                                            } else {
                                                                emailUpdated = user.getEmail();
                                                            }
                                                            DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated).child("userInfo");
                                                            Map mapName = new HashMap();
                                                            mapName.put("displayName", displayname.getText().toString());
                                                            myRef2.child("name").updateChildren(mapName);

                                                            Map mapAge = new HashMap();
                                                            mapAge.put("age", getDOB());
                                                            myRef2.child("age").updateChildren(mapAge);
                                                            DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated);
                                                            Map mapAge2 = new <String, Integer>HashMap();
                                                            mapAge2.put("age", Integer.parseInt(getDOB()));
                                                            myRef3.updateChildren(mapAge2);

                                                            Map mapDescription = new HashMap();
                                                            mapDescription.put("description", bio.getText().toString());
                                                            myRef2.child("description").updateChildren(mapDescription);

                                                            Toast.makeText(NextRegisterActivity.this, "Successfully Registered", Toast.LENGTH_LONG).show();

                                                        }
                                                    }
                                                });
                                        updateUI(user);
                                    } else {
                                        loadingLayout.setVisibility(View.INVISIBLE);
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(NextRegisterActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }

                                }

                            });
                } else {
                    if (displayname.getText().toString().equals("")) {
                        displayname.setError("Please provide a display name!");
                    }
                    if (bio.getText().toString().equals("")) {
                        bio.setError("Please provide a describable bio!");
                    }
                }
            }
        });


    }

    private void updateUI(FirebaseUser currentUser) {
        String emailUpdated;
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference("AllUsersData");
        final DatabaseReference myRef;
        FirebaseDatabase database;
        database = FirebaseDatabase.getInstance();
        if (currentUser.getEmail().contains(".")) {
            emailUpdated = currentUser.getEmail().replace(".", " ");
        } else {
            emailUpdated = currentUser.getEmail();
        }
        myRef = database.getReference("UsersProfileImages").child(emailUpdated).child("profileImage");
        final StorageReference storageRef = mStorageRef.child(currentUser.getEmail() + "/profile/profileImage.jpg");

        loadingLayout.setVisibility(View.INVISIBLE);
        startActivity(new Intent(NextRegisterActivity.this, IntroActivity.class));
        CustomIntent.customType(NextRegisterActivity.this, "left-to-right");
        ((RegisterActivity) RegisterActivity.context).finish();
        ((LogActivity) LogActivity.context).finish();
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(NextRegisterActivity.this, "right-to-left");
    }

    private String getDOB() {
        int day, month, year;
        String[] date_splitted;
        Calendar calendar, dob;
        String ageString = "";
        date_splitted = date.getText().toString().split("/");
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
}

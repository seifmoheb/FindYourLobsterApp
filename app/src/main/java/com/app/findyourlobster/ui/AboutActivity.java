package com.app.findyourlobster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class AboutActivity extends AppCompatActivity implements LocationListener {

    static boolean changed;
    private static double longit;
    private static double latt;
    private final int IMG_REQUEST = 1;
    Window window;
    String currentNumber;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    TextView email, dateTextView;
    FloatingActionButton save, cancel, delete;
    EditText name, phone, bio, date;
    String[] data;
    ImageView profileImage;
    String code;
    FloatingActionButton updateImage;
    CountryCodePicker ccp;
    boolean checkNumber = false;
    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (ccp.isValidFullNumber() != true) {
                phone.setError("Wrong number format");
                checkNumber = false;
            } else {
                checkNumber = true;
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    Bitmap bitmap;
    Uri resultUri;
    boolean imageChanged;
    RelativeLayout loadingLayout;
    String number;
    FirebaseDatabase database;
    String emailUpdated;
    DatabaseReference myRef, myRef2;
    RadioButton male, female;
    String gender;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        delete = findViewById(R.id.remove_number);
        loadingLayout = findViewById(R.id.loadingLayoutAbout);
        imageChanged = false;
        updateImage = findViewById(R.id.updateImage);
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 500, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        save = findViewById(R.id.save);
        cancel = findViewById(R.id.cancel);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        date = findViewById(R.id.date);
        bio = findViewById(R.id.bio);
        profileImage = findViewById(R.id.profileImage);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        email.setText(currentUser.getEmail());
        data = currentUser.getDisplayName().split("_");
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(phone);
        code = "44";
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                code = ccp.getSelectedCountryCode();
            }
        });
        name.setText(data[0]);
        number = currentUser.getPhoneNumber();
        ccp.setFullNumber(number);
        currentNumber = ccp.getFullNumber();
        ccp.setHintExampleNumberEnabled(true);

        if (data[1] != null) {
            if (data[1].equals("dd/mm/yyyy")) {
                date.setHint("dd/mm/yyyy");
            } else {
                date.setText(data[1]);
            }
        }
        if (data[2] != null) {
            if (data[2].equals("Specify bio!")) {
                bio.setHint("Specify bio!");
            } else {
                bio.setText(data[2]);
            }
        }
        gender = "";

        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "male";
                }
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gender = "female";
                }
            }
        });

        if (currentUser.getEmail().contains(".")) {
            emailUpdated = currentUser.getEmail().replace(".", " ");
        } else {
            emailUpdated = currentUser.getEmail();
        }


        database = FirebaseDatabase.getInstance();
        myRef2 = database.getReference("AllUsersData").child(emailUpdated).child("userInfo").child("gender");
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    gender = snapshot.getValue().toString();
                    if (gender.equals("male")) {
                        male.setChecked(true);
                    } else {
                        female.setChecked(true);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("gender").exists()) {
                    gender = snapshot.getValue().toString();
                    if (gender.equals("male")) {
                        male.setChecked(true);
                    } else {
                        female.setChecked(true);
                    }
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
        if (gender != null) {
            if (!gender.equals("")) {

            }
        }
        myRef = database.getReference("UsersProfileImages").child(emailUpdated).child("profileImage");

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Glide.with(getApplicationContext())
                        .load(snapshot.getValue().toString())
                        .placeholder(R.color.colorPrimary)
                        .into(profileImage);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Glide.with(getApplicationContext())
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


        phone.addTextChangedListener(mTextWatcher);
        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AboutActivity.this);
            }
        });

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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingLayout.setVisibility(View.VISIBLE);
                if (name.getText().toString() != null && date.getText().toString() != null) {
                    UserProfileChangeRequest profileChangeRequest;

                    if (imageChanged) {
                        profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name.getText().toString() + "_" + date.getText().toString() + "_" + bio.getText().toString() + "_" + code + "x" + phone.getText().toString())
                                .setPhotoUri(resultUri)
                                .build();
                        String emailUpdated;
                        StorageReference mStorageRef;
                        mStorageRef = FirebaseStorage.getInstance().getReference("UsersImages");
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
                        storageRef.putFile(resultUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content
                                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Map map = new HashMap();
                                                map.put("ProfileImage", uri.toString());
                                                myRef.updateChildren(map);
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                    }
                                });

                    } else {
                        profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name.getText().toString() + "_" + date.getText().toString() + "_" + bio.getText().toString() + "_" + code + "x" + phone.getText().toString())
                                .build();
                    }

                    currentUser.updateProfile(profileChangeRequest)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        if (currentUser.getPhoneNumber() != null && !phone.getText().toString().equals("")) {
                                            if (ccp.isValidFullNumber()) {

                                                if (!currentUser.getPhoneNumber().equals(ccp.getFullNumberWithPlus())) {
                                                    Intent intent = new Intent(AboutActivity.this, OtpActivity.class);
                                                    intent.putExtra("number", code + phone.getText().toString());
                                                    startActivity(intent);
                                                    CustomIntent.customType(AboutActivity.this, "left-to-right");

                                                }
                                            }
                                        } else if (currentUser.getPhoneNumber() == null && !phone.getText().toString().equals("")) {

                                            if (ccp.isValidFullNumber()) {

                                                Intent intent = new Intent(AboutActivity.this, OtpActivity.class);
                                                intent.putExtra("number", code + phone.getText().toString());
                                                startActivity(intent);
                                                CustomIntent.customType(AboutActivity.this, "left-to-right");

                                            }

                                        } else if (phone.getText() == null) {
                                            auth.getCurrentUser().updatePhoneNumber(null);
                                        }
                                        if (currentUser.getEmail().contains(".")) {
                                            emailUpdated = currentUser.getEmail().replace(".", " ");
                                        } else {
                                            emailUpdated = currentUser.getEmail();
                                        }
                                        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated).child("userInfo");
                                        Map mapName = new HashMap();
                                        mapName.put("displayName", name.getText().toString());
                                        myRef2.child("name").updateChildren(mapName);

                                        Map mapAge = new HashMap();
                                        mapAge.put("age", getDOB());
                                        myRef2.child("age").updateChildren(mapAge);
                                        DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated);
                                        Map mapAge2 = new <String, Integer>HashMap();
                                        mapAge2.put("age", Integer.parseInt(getDOB()));
                                        myRef3.updateChildren(mapAge2);

                                        if (gender != null) {
                                            Map mapGender = new HashMap();
                                            mapGender.put("gender", gender);
                                            myRef2.child("gender").updateChildren(mapGender);
                                            DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated);
                                            Map mapGender2 = new HashMap();
                                            mapGender2.put("gender", gender);
                                            myRef4.updateChildren(mapGender2);
                                            final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                                            final SharedPreferences.Editor editor = sharedPreferences.edit();
                                            if (gender.equals("male")) {
                                                editor.putString("gender", "male");
                                            } else {
                                                editor.putString("gender", "female");
                                            }
                                            editor.commit();
                                        }
                                        Map mapDescription = new HashMap();
                                        mapDescription.put("description", bio.getText().toString());
                                        myRef2.child("description").updateChildren(mapDescription);

                                        DatabaseReference myRef4 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated);

                                        Map loc = new HashMap();
                                        loc.put("location", HomeFragment.encode(getLatt(), getLongit(), 5));
                                        myRef4.updateChildren(loc);


                                        changed = true;
                                        Toast.makeText(AboutActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                                        loadingLayout.setVisibility(View.INVISIBLE);
                                        finish();
                                        CustomIntent.customType(AboutActivity.this, "right-to-left");


                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AboutActivity.this, "Profile update failed!", Toast.LENGTH_LONG).show();
                            loadingLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                } else {
                    Toast.makeText(AboutActivity.this, "Fill empty fields!", Toast.LENGTH_LONG).show();
                    loadingLayout.setVisibility(View.INVISIBLE);
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(AboutActivity.this, "right-to-left");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageChanged = true;
                resultUri = result.getUri();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(resultUri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

        DecimalFormat precision = new DecimalFormat("0.000000");
        String lat = precision.format(getLatt());
        String lon = precision.format(getLongit());
        String loc = lat + "," + lon;
        Map mapLoc2 = new HashMap();
        String countryName = "";
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(getLatt(), getLongit(), 1);
            if (addresses.size() > 0)
                countryName = addresses.get(0).getCountryName();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        myRef = database.getReference("AllUsersData").child(emailUpdated);
        mapLoc2.put("location", HomeFragment.encode(getLatt(), getLongit(), 5));
        myRef.updateChildren(mapLoc2);
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
}

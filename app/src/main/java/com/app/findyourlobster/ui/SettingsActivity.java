package com.app.findyourlobster.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.apptik.widget.MultiSlider;
import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity {

    Window window;
    String cache_value;
    LinearLayout clear_cache;
    TextView cacheTextView, address, ageTextView, changePassword;
    long size;
    ImageView gps_button;
    LocationManager locationManager;
    boolean GPSStatus;
    Animation btnAnim;
    Switch hideAge, hideLocation, notificationsSwitch;
    CheckBox filter;
    RadioButton male, female, byAge, byLocation, byGender;
    MultiSlider ageSeekBar;
    int value_1 = 16;
    int value_2 = 100;
    FloatingActionButton exit;

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int j = 0; j < children.length; j++) {
                boolean success = deleteDir(new File(dir, children[j]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        clear_cache = findViewById(R.id.clear_cache);
        cacheTextView = findViewById(R.id.cache_text_view);
        address = findViewById(R.id.address);
        hideAge = findViewById(R.id.hide_age);
        hideLocation = findViewById(R.id.hide_location);
        ageSeekBar = findViewById(R.id.ageSeekbar);
        ageTextView = findViewById(R.id.ageTextView);
        changePassword = findViewById(R.id.password);
        filter = findViewById(R.id.textList);
        final SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        byAge = findViewById(R.id.by_age);
        byGender = findViewById(R.id.by_gender);
        byLocation = findViewById(R.id.by_location);
        notificationsSwitch = findViewById(R.id.notifications);
        filter.setChecked(sharedPreferences.getBoolean("Filter", false));
        male.setChecked(sharedPreferences.getBoolean("male", false));
        female.setChecked(sharedPreferences.getBoolean("female", false));

        if (filter.isChecked()) {
            byGender.setEnabled(true);
            byAge.setEnabled(true);
            byLocation.setEnabled(true);
            byLocation.setChecked(sharedPreferences.getBoolean("locationFilter", false));
            byAge.setChecked(sharedPreferences.getBoolean("ageFilter", false));
            byGender.setChecked(sharedPreferences.getBoolean("genderFilter", false));
        } else {
            byGender.setEnabled(false);
            byAge.setEnabled(false);
            byLocation.setEnabled(false);
        }
        exit = findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(SettingsActivity.this, "right-to-left");

            }
        });
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                CustomIntent.customType(SettingsActivity.this, "left-to-right");
            }
        });


        if (byGender.isChecked() && filter.isChecked()) {
            male.setEnabled(true);
            female.setEnabled(true);
        } else {
            male.setEnabled(false);
            female.setEnabled(false);
        }
        if (byAge.isChecked() && filter.isChecked()) {
            ageSeekBar.setEnabled(true);
        } else {
            ageSeekBar.setEnabled(false);
        }

        filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    byGender.setEnabled(true);
                    byAge.setEnabled(true);
                    byLocation.setEnabled(true);
                    editor.putBoolean("Filter", true);

                } else {
                    byGender.setEnabled(false);
                    byAge.setEnabled(false);
                    byLocation.setEnabled(false);
                    byGender.setChecked(false);
                    byAge.setChecked(false);
                    male.setChecked(false);
                    female.setChecked(false);
                    byLocation.setChecked(false);
                    editor.putBoolean("genderFilter", false);
                    editor.putBoolean("ageFilter", false);
                    editor.putBoolean("locationFilter", false);
                    editor.putBoolean("Filter", false);
                    editor.putBoolean("female", false);
                    editor.putBoolean("male", false);

                }
                editor.commit();

            }
        });
        byGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    male.setEnabled(true);
                    female.setEnabled(true);
                    editor.putBoolean("genderFilter", true);

                } else {
                    male.setEnabled(false);
                    female.setEnabled(false);
                    editor.putBoolean("genderFilter", false);
                }
                editor.commit();

            }
        });
        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    editor.putBoolean("male", true);
                } else {
                    editor.putBoolean("male", false);
                }
                editor.commit();
            }
        });

        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    editor.putBoolean("female", true);
                } else {
                    editor.putBoolean("female", false);
                }
                editor.commit();
            }
        });

        byAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ageSeekBar.setEnabled(true);
                    editor.putBoolean("ageFilter", true);
                    male.setChecked(false);
                    female.setChecked(false);
                    editor.putBoolean("female", false);
                    editor.putBoolean("male", false);


                } else {
                    ageSeekBar.setEnabled(false);
                    editor.putBoolean("ageFilter", false);


                }
                editor.commit();

            }
        });
        byLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    editor.putBoolean("locationFilter", true);
                    male.setChecked(false);
                    female.setChecked(false);
                    editor.putBoolean("female", false);
                    editor.putBoolean("male", false);
                } else {
                    editor.putBoolean("locationFilter", false);
                }
                editor.commit();
            }
        });

        ageSeekBar.setMin(18);
        ageSeekBar.setMax(100);
        ageTextView.setText(sharedPreferences.getInt("ageValue1", 16) + "-" + sharedPreferences.getInt("ageValue2", 100) + " years young");


        ageSeekBar.getThumb(0).setValue(sharedPreferences.getInt("ageValue1", 18));
        ageSeekBar.getThumb(1).setValue(sharedPreferences.getInt("ageValue2", 100));

        ageSeekBar.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                String temp = thumb.getTag();

                if (temp.equals("thumb 0")) {
                    value_1 = value;
                } else {
                    value_2 = value;
                }
                ageTextView.setText(value_1 + "-" + value_2 + " years young");
                editor.putInt("ageValue1", value_1);
                editor.putInt("ageValue2", value_2);
                editor.commit();

            }
        });

        hideAge.setChecked(sharedPreferences.getBoolean("hideAge", false));
        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications", true));
        hideAge.setEnabled(false);

        hideAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 400 milliseconds
                vibrator.vibrate(400);

            }
        });
        hideLocation.setEnabled(false);

        hideLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(400);

            }
        });
        hideAge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    editor.putBoolean("hideAge", b);
                    editor.commit();

            }
        });
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("notifications", b);
                editor.commit(); }
        });
        size = getDirSize(this.getCacheDir());
        cache_value = android.text.format.Formatter.formatShortFileSize(this, size);
        gps_button = findViewById(R.id.location);
        cacheTextView.setText(cache_value);

        if (checkGPS()) {
            editor.putBoolean("location", true);
        } else {
            editor.putBoolean("location", false);
        }
        editor.commit();
        address.setText(getAddressFromLatLng(this));
        btnAnim = AnimationUtils.loadAnimation(SettingsActivity.this, R.anim.gps_animation);
        clear_cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getCacheDir();
                deleteDir(file);
                cacheTextView.setText("0 B");
            }
        });
        gps_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(View view) {
                if (!checkGPS()) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                    requestPermissions(permissions, PackageManager.PERMISSION_GRANTED);

                }

                address.setText(getAddressFromLatLng(SettingsActivity.this));
            }
        });
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    private boolean checkGPS() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GPSStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GPSStatus;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public String getAddressFromLatLng(Context context) {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            if (locationManager.isLocationEnabled()) {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    return addresses.get(0).getAddressLine(0);
                } else
                    return "";

            } else
                return "";

        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(SettingsActivity.this, "right-to-left");
    }
}
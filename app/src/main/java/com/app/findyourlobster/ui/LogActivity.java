package com.app.findyourlobster.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.agrawalsuneet.dotsloader.loaders.SlidingLoader;
import com.app.findyourlobster.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class LogActivity extends AppCompatActivity implements LocationListener {

    static Context context;
    private static double longit;
    private static double latt;
    Window window;
    Button email_and_password;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    SignInButton google;
    LoginButton facebook;
    GoogleSignInClient mGoogleSignInClient;
    SlidingLoader slidingLoader;
    RelativeLayout loadingLayout;
    String[] data;
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
        setContentView(R.layout.activity_log);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        context = this;
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        email_and_password = findViewById(R.id.button);
        mAuth = FirebaseAuth.getInstance();
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 500, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        facebook = findViewById(R.id.facebookButton);
        google = findViewById(R.id.googleButton);
        email_and_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogActivity.this, EmailAndPassword.class);
                startActivity(intent);
                CustomIntent.customType(LogActivity.this, "left-to-right");
            }
        });
        callbackManager = CallbackManager.Factory.create();
        slidingLoader = findViewById(R.id.slidingDots);
        loadingLayout = findViewById(R.id.loadingLayout);

        facebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(LogActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                Log.i("errorfacebook", error.getMessage());

            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {

        loadingLayout.setVisibility(View.VISIBLE);
        facebook.setEnabled(false);
        google.setEnabled(false);
        email_and_password.setEnabled(false);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(currentUser.getDisplayName() + "_" + "dd/mm/yyyy" + "_" + "Specify bio!")
                                .build();
                        currentUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                        }
                                    }
                                });
                        updateUI(currentUser);
                        loadingLayout.setVisibility(View.INVISIBLE);
                    }
                } else {
                    facebook.setEnabled(true);
                    google.setEnabled(true);
                    email_and_password.setEnabled(true);
                    loadingLayout.setVisibility(View.INVISIBLE);
                    LoginManager.getInstance().logOut();
                    Toast.makeText(LogActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    Log.i("anotherError", task.toString());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authWithGoogle(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(LogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void authWithGoogle(String idToken) {
        loadingLayout.setVisibility(View.VISIBLE);
        facebook.setEnabled(false);
        google.setEnabled(false);
        email_and_password.setEnabled(false);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingLayout.setVisibility(View.INVISIBLE);
                            final FirebaseUser currentUser = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(currentUser.getDisplayName() + "_" + "dd/mm/yyyy" + "_" + "Specify bio!")
                                    .build();
                            currentUser.updateProfile(profileChangeRequest)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            }
                                        }
                                    });
                            updateUI(currentUser);

                        } else {
                            loadingLayout.setVisibility(View.INVISIBLE);
                            facebook.setEnabled(true);
                            google.setEnabled(true);
                            email_and_password.setEnabled(true);
                            Toast.makeText(LogActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            Log.i("anotherError", task.toString());
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {
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

        Log.i("Entered ui", emailUpdated);
        Log.i("Entered ui", currentUser.getDisplayName());
        DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(emailUpdated).child("userInfo");
        data = currentUser.getDisplayName().split("_");

        Map mapName = new HashMap();
        mapName.put("displayName", data[0]);
        Log.i("Entered ui", data[0]);

        myRef2.child("name").updateChildren(mapName);

        Map mapLoc = new HashMap();
        mapLoc.put("location", getLatt() + "," + getLongit());
        myRef2.child("location").updateChildren(mapLoc);

        Map mapAge = new HashMap();
        mapAge.put("age", getDOB());
        if (getDOB() != "dd/mm/yyyy")
            myRef2.child("age").updateChildren(mapAge);

        Map mapDescription = new HashMap();
        mapDescription.put("description", data[2]);
        myRef2.child("description").updateChildren(mapDescription);
        myRef = database.getReference("UsersProfileImages").child(emailUpdated).child("profileImage");
        final StorageReference storageRef = mStorageRef.child(currentUser.getEmail() + "/profile/profileImage.jpg");
        storageRef.putFile(currentUser.getPhotoUrl())
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
        startActivity(new Intent(LogActivity.this, MainActivity.class));
        CustomIntent.customType(LogActivity.this, "left-to-right");
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(LogActivity.this, "up-to-down");
    }

    private String getDOB() {
        int day, month, year;
        String[] date_splitted;
        Calendar calendar, dob;
        String ageString = "";
        if (!data[1].equals("dd/mm/yyyy")) {
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
        } else {
            return "dd/mm/yyyy";
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setLongit(location.getLongitude());
        setLatt(location.getLatitude());

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
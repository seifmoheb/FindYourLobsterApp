package com.app.findyourlobster.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

public class EmailAndPassword extends AppCompatActivity {

    Window window;
    Button logIn, register;
    FloatingActionButton exitButton;
    EditText email, password;
    FirebaseAuth auth;
    TextView error;
    RelativeLayout loadingLayout;
    String[] data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_and_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        auth = FirebaseAuth.getInstance();
        loadingLayout = findViewById(R.id.loadingLayoutEmail);
        logIn = findViewById(R.id.logIn);
        error = findViewById(R.id.error);
        register = findViewById(R.id.register);
        exitButton = findViewById(R.id.exitButton);
        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmailAndPassword.this, RegisterActivity.class));
                CustomIntent.customType(EmailAndPassword.this, "left-to-right");
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(EmailAndPassword.this, "right-to-left");

            }
        });
        if (email.getText().toString().equals("")) {
            error.setVisibility(View.GONE);
        }
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingLayout.setVisibility(View.VISIBLE);
                if (!email.getText().toString().equals("") || !password.getText().toString().equals("")) {
                    auth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        loadingLayout.setVisibility(View.INVISIBLE);
                                        error.setVisibility(View.GONE);
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                        updateUI(currentUser);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingLayout.setVisibility(View.INVISIBLE);
                            error.setVisibility(View.VISIBLE);
                            error.setText(e.getMessage());
                            Toast.makeText(EmailAndPassword.this, "Log in failed", Toast.LENGTH_SHORT).show();

                        }
                    });
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
        myRef = database.getReference("AllUsersData").child(emailUpdated).child("profileImage");
        final StorageReference storageRef = mStorageRef.child(currentUser.getEmail() + "/profile/profileImage.jpg");
        if (currentUser.getPhotoUrl() != null) {
            storageRef.putFile(currentUser.getPhotoUrl())
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (uri != null) {
                                        Map map = new HashMap();
                                        map.put("ProfileImage", uri.toString());
                                        myRef.updateChildren(map);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
        }

        startActivity(new Intent(EmailAndPassword.this, MainActivity.class));
        CustomIntent.customType(EmailAndPassword.this, "left-to-right");
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(EmailAndPassword.this, "right-to-left");
    }

}
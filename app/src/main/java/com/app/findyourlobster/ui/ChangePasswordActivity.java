package com.app.findyourlobster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import maes.tech.intentanim.CustomIntent;

public class ChangePasswordActivity extends AppCompatActivity {

    static Context context;
    Window window;
    Button continueButton;
    EditText currentPassword;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    TextView invalid;
    FloatingActionButton exit;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (currentPassword.getText() == null) {
                invalid.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        continueButton = findViewById(R.id.continueButton);
        currentPassword = findViewById(R.id.CurrentTextPassword);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        invalid = findViewById(R.id.invalid);
        context = this;
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentPassword.getText().toString().equals("")) {
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword.getText().toString());
                    currentUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent = new Intent(ChangePasswordActivity.this, NextChangePasswordActivity.class);
                            startActivity(intent);
                            CustomIntent.customType(ChangePasswordActivity.this, "left-to-right");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            currentPassword.getText().clear();
                            invalid.setVisibility(View.VISIBLE);
                            invalid.setText(e.getMessage());
                        }
                    });

                }
            }
        });
        exit = findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        currentPassword.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(ChangePasswordActivity.this, "right-to-left");
    }
}
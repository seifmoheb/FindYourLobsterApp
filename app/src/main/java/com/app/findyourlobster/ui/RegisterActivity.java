package com.app.findyourlobster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import maes.tech.intentanim.CustomIntent;

public class RegisterActivity extends AppCompatActivity {

    static Context context;
    Window window;
    FirebaseAuth auth;
    EditText email, password;
    Button register;
    FloatingActionButton returnButton;
    boolean invalidEmail = false;
    RelativeLayout loadingLayout;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!isValidEmail(email.getText().toString())) {
                email.setError("Invalid email format");
                invalidEmail = true;
            } else {
                invalidEmail = false;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        auth = FirebaseAuth.getInstance();
        loadingLayout = findViewById(R.id.loadingLayoutRegister);
        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        returnButton = findViewById(R.id.exitButton);
        register = findViewById(R.id.register);
        context = this;
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(RegisterActivity.this, "right-to-left");

            }
        });
        email.addTextChangedListener(textWatcher);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingLayout.setVisibility(View.VISIBLE);
                if (!invalidEmail) {
                    if (!email.getText().toString().equals("") && !password.getText().toString().equals("")) {
                        auth.fetchSignInMethodsForEmail(email.getText().toString())
                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        boolean inNewUser = task.getResult().getSignInMethods().isEmpty();
                                        if (inNewUser) {
                                            if (password.getText().toString().length() >= 8) {
                                                loadingLayout.setVisibility(View.INVISIBLE);
                                                Intent intent = new Intent(RegisterActivity.this, NextRegisterActivity.class);
                                                intent.putExtra("email", email.getText().toString());
                                                intent.putExtra("password", password.getText().toString());
                                                startActivity(intent);
                                                CustomIntent.customType(RegisterActivity.this, "left-to-right");
                                            } else {
                                                loadingLayout.setVisibility(View.INVISIBLE);
                                                password.setError("Passwords should be 8 chars or more");
                                            }
                                        } else {
                                            loadingLayout.setVisibility(View.INVISIBLE);
                                            email.setError("Email already exists!");
                                        }
                                    }
                                });


                    }
                }
            }
        });


    }

    private boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(RegisterActivity.this, "right-to-left");
    }

}
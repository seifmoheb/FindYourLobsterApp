package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.chaos.view.PinView;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import maes.tech.intentanim.CustomIntent;

public class OtpActivity extends AppCompatActivity {

    static PhoneAuthCredential credential;
    TextView description;
    ImageView back;
    Window window;
    String pinSent, verification_id;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    PhoneAuthProvider authProvider;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String phone_number;
    Button verify;
    PinView pinView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        back = findViewById(R.id.back);
        Bundle bundle = getIntent().getExtras();
        verify = findViewById(R.id.verify_button);
        if (bundle != null) {
            phone_number = bundle.getString("number");
        }
        pinView = findViewById(R.id.pinView);
        description = findViewById(R.id.description_text);
        description.setText("Enter one time password sent to +" + phone_number);
        phoneNumber();
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinSent.equals(pinView.getText().toString())) {
                    currentUser.updatePhoneNumber(credential);
                    Toast.makeText(OtpActivity.this, "Verification Successful", Toast.LENGTH_LONG).show();
                    finish();
                    CustomIntent.customType(OtpActivity.this, "left-to-right");

                } else {
                    Toast.makeText(OtpActivity.this, "Entered pin doesn't match", Toast.LENGTH_LONG).show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

                CustomIntent.customType(OtpActivity.this, "right-to-left");

            }
        });
    }

    private void phoneNumber() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                pinSent = phoneAuthCredential.getSmsCode();
                credential = phoneAuthCredential;
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(OtpActivity.this, "Verification Failed, " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                verification_id = verificationId;

            }

        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+" + phone_number
                ,
                60,
                TimeUnit.SECONDS,
                this, mCallbacks);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(OtpActivity.this, "right-to-left");
    }
}
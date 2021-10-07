package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.findyourlobster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import maes.tech.intentanim.CustomIntent;

public class NextChangePasswordActivity extends AppCompatActivity {

    Window window;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    EditText newPassword, confirmPassword;
    Button update;
    TextView invalid;
    FloatingActionButton exit;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (newPassword.getText().toString().length() == 0) {

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_change_password);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        exit = findViewById(R.id.exitButton);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(NextChangePasswordActivity.this, "right-to-left");

            }
        });
        invalid = findViewById(R.id.invalid);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        update = findViewById(R.id.update);
        if (newPassword.getText() != null && confirmPassword.getText() != null) {
            update.setVisibility(View.VISIBLE);
        } else {

            update.setVisibility(View.GONE);
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    currentUser.updatePassword(confirmPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(NextChangePasswordActivity.this, "Password update succeeded!", Toast.LENGTH_LONG).show();
                                CustomIntent.customType(NextChangePasswordActivity.this, "right-to-left");
                                ((ChangePasswordActivity) ChangePasswordActivity.context).finish();
                                finish();

                            } else {
                                Toast.makeText(NextChangePasswordActivity.this, "Password update failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    invalid.setText("Passwords don't match!");
                }
            }

        });

        newPassword.addTextChangedListener(textWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(NextChangePasswordActivity.this, "right-to-left");
    }
}
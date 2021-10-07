package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.findyourlobster.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class BlockActivity extends AppCompatActivity {

    String emailToBeBlocked;
    String emailUpdated;
    FirebaseAuth auth;
    FirebaseUser user;
    Button confirm, cancel;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        Bundle bundle = getIntent().getExtras();
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        if (bundle != null) {
            emailToBeBlocked = bundle.getString("email");
        }
        root = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user.getEmail().contains(".")) {
            emailUpdated = user.getEmail().replace(".", " ");
        } else {
            emailUpdated = user.getEmail();
        }
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                root.child("Chats").child(emailUpdated).child(emailToBeBlocked).removeValue();
                root.child("Chats").child(emailToBeBlocked).child(emailUpdated).removeValue();
                block();
                Toasty.success(BlockActivity.this, "Success!", Toast.LENGTH_SHORT, true).show();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void block() {
        String blocker_ref = emailUpdated + "/" + emailToBeBlocked;
        String blocked_ref = emailToBeBlocked + "/" + emailUpdated;
        DatabaseReference databaseReference = root.child("Blocks").child(emailUpdated).child(emailToBeBlocked).push();

        Map hashMapA = new HashMap<>();
        hashMapA.put("type", "blocker");
        Map hashMapB = new HashMap<>();
        hashMapB.put("type", "blocked");
        Map hashMap2 = new HashMap<>();
        hashMap2.put(blocker_ref, hashMapB);
        hashMap2.put(blocked_ref, hashMapA);

        root.child("Blocks").updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                root.child("Chats").child(emailUpdated).child(emailToBeBlocked).removeValue();
                root.child("Chats").child(emailToBeBlocked).child(emailUpdated).removeValue();
                ((ChatActivity) ChatActivity.context).finish();
                ChatFragment.BlockedUser = emailToBeBlocked;

            }
        });
    }
}
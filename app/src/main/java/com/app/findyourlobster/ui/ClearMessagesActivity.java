package com.app.findyourlobster.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.MessagesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ClearMessagesActivity extends AppCompatActivity {

    String chatToBeCleared;
    String emailUpdated;
    FirebaseAuth auth;
    FirebaseUser user;
    Button confirm, cancel;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_messages);
        confirm = findViewById(R.id.confirm);
        cancel = findViewById(R.id.cancel);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatToBeCleared = bundle.getString("email");
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
                Map hashMap = new HashMap<>();
                hashMap.put("Cleared", "");
                root.child("Chats").child(emailUpdated).child(chatToBeCleared).updateChildren(hashMap);
                MessagesAdapter.userMessagesList.clear();
                ChatActivity.recyclerView.removeAllViewsInLayout();

                root.child("Chats").child(emailUpdated).child(chatToBeCleared).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (!dataSnapshot.getValue().equals(""))
                                dataSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toasty.success(ClearMessagesActivity.this, "Success!", Toast.LENGTH_SHORT, true).show();
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
}
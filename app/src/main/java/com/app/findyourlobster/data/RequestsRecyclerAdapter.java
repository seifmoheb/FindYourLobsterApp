package com.app.findyourlobster.data;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.findyourlobster.R;
import com.app.findyourlobster.ui.MoreInfoActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RequestsRecyclerAdapter extends SwipeAdapter implements View.OnClickListener {
    ArrayList<String> mDataset;
    Map<Integer, String> usersImages;
    Map<Integer, String> usersNames;
    String emailUpdated;

    int[] colors = new int[]{R.color.color_red, R.color.color_pink, R.color.color_purple, R.color.color_deep_purple, R.color.color_indigo, R.color.color_blue, R.color.color_light_blue, R.color.color_cyan, R.color.color_teal, R.color.color_green, R.color.color_light_green, R.color.color_lime, R.color.color_yellow, R.color.color_amber, R.color.color_orange, R.color.color_deep_orange, R.color.color_brown, R.color.color_grey, R.color.color_blue_grey};

    private Context mContext;
    private RecyclerView mRecyclerView;

    public RequestsRecyclerAdapter(Context context, RecyclerView recyclerView, ArrayList<String> sentList) {
        mContext = context;
        mRecyclerView = recyclerView;
        mDataset = sentList;
        usersNames = new HashMap<>();
        usersImages = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateSwipeViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.request_sample, parent, true);
        return new RequestsRecyclerAdapter.SampleViewHolder(v);
    }

    @Override
    public void onBindSwipeViewHolder(RecyclerView.ViewHolder swipeViewHolder, final int position) {
        final SampleViewHolder sampleViewHolder = (SampleViewHolder) swipeViewHolder;
        //ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        //drawable.getPaint().setColor(mContext.getResources().getColor(colors[((int) (Math.random() * (colors.length - 1)))]));
        ((SampleViewHolder) swipeViewHolder).myRef.child(mDataset.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sampleViewHolder.textView.setText(snapshot.child("userInfo").child("name").child("displayName").getValue().toString());
                usersNames.put(position, snapshot.child("userInfo").child("name").child("displayName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ((SampleViewHolder) swipeViewHolder).myRef2.child(mDataset.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profileImage").child("ProfileImage").exists()) {
                    Glide.with(mContext)
                            .load(snapshot.child("profileImage").child("ProfileImage").getValue().toString())
                            .placeholder(R.color.colorPrimary)
                            .into(sampleViewHolder.avatarView);
                    usersImages.put(position, snapshot.child("profileImage").child("ProfileImage").getValue().toString());
                } else {
                    usersImages.put(position, "null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ((SampleViewHolder) swipeViewHolder).moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoreInfoActivity.class);
                intent.putExtra("email", mDataset.get(position));
                intent.putExtra("imageURL", usersImages.get(position));
                intent.putExtra("username", usersNames.get(position));
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public SwipeConfiguration onCreateSwipeConfiguration(Context context, int position) {
        return new SwipeConfiguration.Builder(context)
                .setLeftBackgroundColorResource(R.color.color_delete)
                .setRightBackgroundColorResource(R.color.color_mark)
                .setDrawableResource(R.drawable.ic_baseline_delete_forever_24)
                .setRightDrawableResource(R.drawable.ic_baseline_done_24)
                .setLeftUndoable(false)
                .setDescriptionTextColorResource(android.R.color.white)
                .setLeftSwipeBehaviour(SwipeConfiguration.SwipeBehaviour.NORMAL_SWIPE)
                .setRightSwipeBehaviour(SwipeConfiguration.SwipeBehaviour.RESTRICTED_SWIPE)
                .build();
    }

    @Override
    public void onSwipe(int position, int direction) {
        if (direction == SWIPE_LEFT) {

            FirebaseAuth auth;
            FirebaseUser currentUser;
            String emailUpdated;
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            if (currentUser.getEmail().contains(".")) {
                emailUpdated = currentUser.getEmail().replace(".", " ");
            } else {
                emailUpdated = currentUser.getEmail();
            }
            DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Requests").child(emailUpdated);
            myRef3.child(mDataset.get(position)).removeValue();
            mDataset.remove(position);
            notifyItemRemoved(position);
        } else {
            FirebaseAuth auth;
            FirebaseUser currentUser;
            String emailUpdated;
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            if (currentUser.getEmail().contains(".")) {
                emailUpdated = currentUser.getEmail().replace(".", " ");
            } else {
                emailUpdated = currentUser.getEmail();
            }
            DatabaseReference myRef3 = FirebaseDatabase.getInstance().getReference("Requests").child(emailUpdated);
            myRef3.child(mDataset.get(position)).removeValue();
            sendMessage(position);
            mDataset.remove(position);
            notifyItemRemoved(position);
            Toast toast = Toast.makeText(mContext, "Request Accepted ", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View view) {
        // We need to get the parent of the parent to actually have the proper view
        int position = mRecyclerView.getChildAdapterPosition((View) view.getParent().getParent());
        /*Intent intent = new Intent(mContext, ChatActivity.class);
        intent.putExtra("email",mDataset.get(position));
        intent.putExtra("imageURL",usersImages.get(position));
        intent.putExtra("username",usersNames.get(position));
        mContext.startActivity(intent);*/
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void sendMessage(int position) {
        try {
            String saveCurrentDate, saveCurrentTime;
            DatabaseReference root = FirebaseDatabase.getInstance().getReference();
            String message = "How u doin";
            String messagesender_ref = "Chats/" + emailUpdated + "/" + mDataset.get(position);
            String messagereceiver_ref = "Chats/" + mDataset.get(position) + "/" + emailUpdated;

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            DatabaseReference databaseReference = root.child("Chats").child(emailUpdated).child(mDataset.get(position)).push();
            String message_push_id = databaseReference.getKey();

            Map hashMap = new HashMap<>();
            hashMap.put("sender", emailUpdated);
            hashMap.put("message", message);
            hashMap.put("to", mDataset.get(position));
            hashMap.put("date", saveCurrentDate);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("type", "text");

            Map hashMap2 = new HashMap<>();
            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);


            root.updateChildren(hashMap2);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contentView;
        ImageView avatarView;
        TextView textView;
        DatabaseReference myRef, myRef2, myRef3;
        FirebaseAuth auth;
        FirebaseUser currentUser;
        FloatingActionButton moreInfo, accept, decline;

        public SampleViewHolder(View view) {
            super(view);
            contentView = (LinearLayout) view.findViewById(R.id.contentView);
            avatarView = view.findViewById(R.id.avatarView);
            textView = (TextView) view.findViewById(R.id.textView);
            moreInfo = view.findViewById(R.id.moreInfo);


            contentView.setOnClickListener(RequestsRecyclerAdapter.this);
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();
            if (currentUser.getEmail().contains(".")) {
                emailUpdated = currentUser.getEmail().replace(".", " ");
            } else {
                emailUpdated = currentUser.getEmail();
            }
            myRef = FirebaseDatabase.getInstance().getReference("AllUsersData");
            myRef2 = FirebaseDatabase.getInstance().getReference("UsersProfileImages");
            myRef3 = FirebaseDatabase.getInstance().getReference("Requests").child(emailUpdated);

        }
    }
}
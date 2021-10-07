package com.app.findyourlobster.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.app.findyourlobster.R;
import com.app.findyourlobster.ui.ChatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.SampleViewHolder> {
    public static Map<String, Date>  mDataset;
    public static Map<String, Date>  temp;

    Map<Integer, String> usersImages;
    Map<Integer, String> usersNames;
    List<String> listOfNames;


    private Context mContext;
    private RecyclerView mRecyclerView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ChatRecyclerAdapter(Context context, RecyclerView recyclerView, Map<String, Date>  sentList) {
        mContext = context;
        mRecyclerView = recyclerView;
        mDataset = new HashMap<>();
        mDataset = sentList;
        Map<String, Date> reverseSortedMap = new HashMap<>();

        usersNames = new HashMap<>();
        usersImages = new HashMap<>();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public SampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sample, parent, false);

        listOfNames = new ArrayList<>(mDataset.keySet());

        return new SampleViewHolder(v);    }

    @Override
    public void onBindViewHolder(@NonNull SampleViewHolder holder, int position) {
        //ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        //drawable.getPaint().setColor(mContext.getResources().getColor(colors[((int) (Math.random() * (colors.length - 1)))]));
        holder.myRef.child(listOfNames.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("userInfo").child("name").child("displayName").exists()) {
                    holder.textView.setText(snapshot.child("userInfo").child("name").child("displayName").getValue().toString());
                    usersNames.put(position, snapshot.child("userInfo").child("name").child("displayName").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.itemView.setBackgroundColor(mRecyclerView.isSelected() ? Color.CYAN : Color.WHITE);
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("email", listOfNames.get(position));
                intent.putExtra("imageURL", usersImages.get(position));
                intent.putExtra("username", usersNames.get(position));
                mContext.startActivity(intent);
            }
        });
        holder.myRef2.child(listOfNames.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("profileImage").child("ProfileImage").exists()) {
                    Glide.with(mContext)
                            .load(snapshot.child("profileImage").child("ProfileImage").getValue().toString())
                            .placeholder(R.color.colorPrimary)
                            .into(holder.avatarView);
                    usersImages.put(position, snapshot.child("profileImage").child("ProfileImage").getValue().toString());
                } else {
                    usersImages.put(position, "null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.myRef3.child(listOfNames.get(position)).limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("message").exists()) {

                    if (snapshot.child("message").getValue().toString().contains("https://firebasestorage.googleapis.com/v0/b/findyourlobster-9234f.appspot.com/o/Files")) {
                        holder.lastMessage.setText("Image");
                    } else {
                        holder.lastMessage.setText(snapshot.child("message").getValue().toString());
                    }
                }
                else if (snapshot.child("Cleared").exists())
                    holder.lastMessage.setText("");

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    public void removeItem(String BlockedUser) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (listOfNames.get(i).equals(BlockedUser)) {
                mDataset.remove(listOfNames.get(i));
                listOfNames.remove(i);
                notifyItemRemoved(i);
            }
        }
    }
    static class TreeMapDemo {
        @NonNull
        public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
            Comparator<K> valueComparator = new Comparator<K>() {
                public int compare(K k1, K k2) {

                     return map.get(k1).compareTo(map.get(k2));

                }
            };
            Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
            sortedByValues.putAll(map);
            return sortedByValues;
        }
    }
    public static class SampleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contentView;
        ImageView avatarView;
        TextView textView, lastMessage;
        DatabaseReference myRef, myRef2, myRef3;
        FirebaseAuth auth;
        FirebaseUser currentUser;
        String emailUpdated;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public SampleViewHolder(View view) {
            super(view);
            contentView = (LinearLayout) view.findViewById(R.id.contentView);
            avatarView = view.findViewById(R.id.avatarView);
            textView = (TextView) view.findViewById(R.id.textView);
            lastMessage = (TextView) view.findViewById(R.id.lastMessage);
            auth = FirebaseAuth.getInstance();
            currentUser = auth.getCurrentUser();

            if (currentUser.getEmail().contains(".")) {
                emailUpdated = currentUser.getEmail().replace(".", " ");
            } else {
                emailUpdated = currentUser.getEmail();
            }
            myRef = FirebaseDatabase.getInstance().getReference("AllUsersData");
            myRef2 = FirebaseDatabase.getInstance().getReference("UsersProfileImages");
            myRef3 = FirebaseDatabase.getInstance().getReference("Chats").child(emailUpdated);

        }

    }

}

package com.app.findyourlobster.ui;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.ChatRecyclerAdapter;
import com.app.findyourlobster.data.DividerItemDecoration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class ChatFragment extends Fragment {

    static String BlockedUser = "";
    RecyclerView recyclerView;
    ArrayList<String> list;
    static Map<String,Date> hash;
    Map<String,Date> sortedHash;
    ChatRecyclerAdapter chatRecyclerAdapter;
    DatabaseReference myRef;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    String emailUpdated;
    LinearLayoutManager linearLayoutManager;
    int count;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration((requireActivity()), DividerItemDecoration.VERTICAL_LIST));
        list = new ArrayList<>();
        hash = new TreeMap<>();
        sortedHash = new TreeMap<>();
        chatRecyclerAdapter = new ChatRecyclerAdapter(getActivity(), recyclerView, hash);
        recyclerView.setAdapter(chatRecyclerAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (currentUser.getEmail().contains(".")) {
            emailUpdated = currentUser.getEmail().replace(".", " ");
        } else {
            emailUpdated = currentUser.getEmail();
        }
        myRef = FirebaseDatabase.getInstance().getReference("Chats").child(emailUpdated);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot Dsnapshot, @Nullable String previousChildName) {
                list.add(Dsnapshot.getKey());
                myRef = FirebaseDatabase.getInstance().getReference("Chats").child(emailUpdated).child(Dsnapshot.getKey());
                myRef.limitToLast(1).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if (snapshot.child("message").exists()) {
                            String time = snapshot.child("time").getValue().toString();
                            String date = snapshot.child("date").getValue().toString();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:a dd-MM-yyyy");
                            Date dateFormatted = null;
                            try {
                                dateFormatted = dateFormat.parse(time + " " + date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            hash.put(Dsnapshot.getKey(),dateFormatted);
                            System.out.println("Before "+hash);
                            sortedHash = TreeMapDemo.sortByValues(hash);
                            System.out.println("After "+sortedHash);
                            hash.putAll(sortedHash);
                            System.out.println("Hash After "+sortedHash);
                            chatRecyclerAdapter = new ChatRecyclerAdapter(getActivity(), recyclerView, sortedHash);
                            recyclerView.setAdapter(chatRecyclerAdapter);
                            chatRecyclerAdapter.notifyDataSetChanged();

                        }

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
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                chatRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //sortByValueJava8Stream(hash);



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!BlockedUser.equals("")) {
            chatRecyclerAdapter.removeItem(BlockedUser);
            BlockedUser = "";

        }
    }
    static class TreeMapDemo {
        public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
            Comparator<K> valueComparator = new Comparator<K>() {
                public int compare(K k1, K k2) {

                    if (map.get(k1) != null && map.get(k2) != null) {
                        int compare = map.get(k1).compareTo(map.get(k2));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                    else
                        return -1;

                }
            };
            Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
            sortedByValues.putAll(map);

            return sortedByValues;
        }
    }
}

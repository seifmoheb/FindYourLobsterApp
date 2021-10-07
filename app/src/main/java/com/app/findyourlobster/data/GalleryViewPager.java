package com.app.findyourlobster.data;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.app.findyourlobster.R;
import com.app.findyourlobster.ui.GalleryActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryViewPager extends PagerAdapter {
    Context context;
    ArrayList<ImageModel> imagesList;
    int countImages = 0;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;
    FirebaseAuth auth;
    String emailUpdated;
    String deleteNode;
    int pos;
    ImageView imageView;
    View view;

    public GalleryViewPager(Context context, ArrayList<ImageModel> imagesList,int pos) {
        this.context = context;
        this.imagesList = imagesList;
        this.pos = pos;

    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int pos) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.gallery_screen, null);
        final FloatingActionButton delete, star, starred;
        delete = view.findViewById(R.id.delete);
        star = view.findViewById(R.id.star);
        auth = FirebaseAuth.getInstance();
        starred = view.findViewById(R.id.starred);
        if (auth.getCurrentUser().getEmail().contains(".")) {
            emailUpdated = auth.getCurrentUser().getEmail().replace(".", " ");
        } else {
            emailUpdated = auth.getCurrentUser().getEmail();
        }

        imageView = view.findViewById(R.id.galleryItem);
        Glide.with(context)
                .load(imagesList.get(pos).getUrl())
                .placeholder(R.color.colorPrimaryDark)
                .into(imageView);
        database = FirebaseDatabase.getInstance();
        myRef2 = database.getReference("AllUsersData").child(emailUpdated).child("starred");
        myRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.getValue().toString().equals("")) {
                    if (snapshot.getValue().toString().equals(imagesList.get(pos).getUrl())) {
                        starred.setVisibility(View.VISIBLE);
                        star.setVisibility(View.INVISIBLE);
                    } else {
                        starred.setVisibility(View.INVISIBLE);
                        star.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (!snapshot.getValue().toString().equals("")) {
                    if (snapshot.getValue().toString().equals(imagesList.get(pos).getUrl())) {
                        starred.setVisibility(View.VISIBLE);
                        star.setVisibility(View.INVISIBLE);
                    } else {
                        starred.setVisibility(View.INVISIBLE);
                        star.setVisibility(View.VISIBLE);
                    }
                }
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

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map map = new HashMap();
                map.put("star", imagesList.get(pos).getUrl());
                myRef2.updateChildren(map);
                star.setVisibility(View.INVISIBLE);
                starred.setVisibility(View.VISIBLE);

            }
        });
        starred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pos != 0) {
                    Map map = new HashMap();
                    map.put("star", imagesList.get(pos).getUrl());
                    myRef2.updateChildren(map);
                } else {
                    Map map = new HashMap();
                    map.put("star", "");
                    myRef2.updateChildren(map);
                }

                starred.setVisibility(View.INVISIBLE);
                star.setVisibility(View.VISIBLE);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(context, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                progressDialog.setMessage("Deleting....");
                progressDialog.setTitle("Gallery");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                final String emailUpdated;
                StorageReference mStorageRef;
                mStorageRef = FirebaseStorage.getInstance().getReference("AllUsersData");
                database = FirebaseDatabase.getInstance();
                if (auth.getCurrentUser().getEmail().contains(".")) {
                    emailUpdated = auth.getCurrentUser().getEmail().replace(".", " ");
                } else {
                    emailUpdated = auth.getCurrentUser().getEmail();
                }
                myRef = database.getReference("AllUsersData").child(emailUpdated).child("images");
                FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                final StorageReference storageRef = mFirebaseStorage.getReferenceFromUrl(imagesList.get(pos).getUrl());

                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        myRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull final DataSnapshot snapshot, @Nullable String previousChildName) {
                                String value = snapshot.child("image").getValue().toString();

                                if (value.equals(imagesList.get(pos).getUrl())) {
                                    deleteNode = snapshot.getRef().getKey();
                                    final DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("AllUsersData").child(emailUpdated).child("images").child(deleteNode);
                                    db_node.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (pos != 0) {
                                                Map map = new HashMap();
                                                map.put("star", imagesList.get(0).getUrl());
                                                myRef2.updateChildren(map);
                                            } else {
                                                if (imagesList.size() == 1) {
                                                    Map map = new HashMap();
                                                    map.put("star", "");
                                                    myRef2.updateChildren(map);
                                                } else {
                                                    Map map = new HashMap();
                                                    map.put("star", imagesList.get(pos + 1).getUrl());
                                                    myRef2.updateChildren(map);
                                                }

                                            }
                                            ((GalleryActivity) context).finish();
                                            Toast.makeText(context, "Gallery updated", Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            deleteNode = snapshot.getRef().getKey();
                                            final DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("AllUsersData").child(emailUpdated).child(deleteNode);
                                            db_node.removeValue();
                                            progressDialog.dismiss();
                                        }
                                    });
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

                });
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}

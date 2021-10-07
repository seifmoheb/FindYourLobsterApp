package com.app.findyourlobster.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.GalleryAllAdapter;
import com.app.findyourlobster.data.ImageModel;
import com.app.findyourlobster.data.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GalleryAll extends AppCompatActivity {
    GalleryAllAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<ImageModel> data = new ArrayList<>();
    int countImages = 0;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;
    FirebaseAuth auth;
    String emailUpdated;
    StorageReference mStorageRef;
    Window window;
    FloatingActionButton exit,add;
    static final SecureRandom secureRandom = new SecureRandom();
    static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    String img;
    StorageReference storageRef;
    Uri resultUri;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_all);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        exit = findViewById(R.id.back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                CustomIntent.customType(GalleryAll.this, "right-to-left");
            }
        });
        add = findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(GalleryAll.this);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (auth.getCurrentUser().getEmail().contains(".")) {
            emailUpdated = auth.getCurrentUser().getEmail().replace(".", " ");
        } else {
            emailUpdated = auth.getCurrentUser().getEmail();
        }
        database = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference("UsersImages");
        myRef = database.getReference("AllUsersData").child(emailUpdated).child("images");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ImageModel imageModel = new ImageModel();
                String value = snapshot.child("image").getValue().toString();
                imageModel.setUrl(value);

                data.add(imageModel);
                mAdapter.notifyDataSetChanged();
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
        mAdapter = new GalleryAllAdapter(GalleryAll.this, data);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {

                        Intent intent = new Intent(GalleryAll.this, GalleryActivity.class);
                        intent.putParcelableArrayListExtra("data", data);
                        intent.putExtra("pos", position);
                        startActivity(intent);
                        CustomIntent.customType(GalleryAll.this, "right-to-left");

                    }
                }));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                uploadFile();
            }
        }
    }
    private static String generateToken() {

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }
    private void uploadFile() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(GalleryAll.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        storageRef = mStorageRef.child(currentUser.getEmail() + "/images/" + generateToken() + ".jpg");
        storageRef.putFile(resultUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Map map = new HashMap();
                                map.put("image", uri.toString());
                                myRef.push().updateChildren(map);
                                progressDialog.dismiss();
                                Toast.makeText(GalleryAll.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(GalleryAll.this, "Image upload failed", Toast.LENGTH_LONG).show();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double p = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();

                progressDialog.setMessage((int) p + " % Uploading....");
                progressDialog.setTitle("Gallery");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        });
    }
}
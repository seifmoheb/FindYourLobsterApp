package com.app.findyourlobster.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.GalleryViewPager;
import com.app.findyourlobster.data.ImageModel;
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
import java.util.List;
import java.util.Map;

import maes.tech.intentanim.CustomIntent;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GalleryActivity extends AppCompatActivity {

    static final SecureRandom secureRandom = new SecureRandom();
    static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    Window window;
    FloatingActionButton next, previous, add, exit;
    Uri resultUri;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String emailUpdated;
    ViewPager viewPager;
    List<String> galleryItems = new ArrayList<>();
    int position;
    GalleryViewPager galleryViewPager;
    Adapter adapter;
    TextView counter;
    int countImages = 0;
    String img;
    StorageReference mStorageRef;
    StorageReference storageRef;
    ArrayList<ImageModel> data = new ArrayList<>();
    int pos;

    private static String generateToken() {

        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        counter = findViewById(R.id.counter);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        viewPager = findViewById(R.id.GalleryViewPager);
        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);
        position = pos;
        counter.setText(position + 1 + "/" + data.size());
        mStorageRef = FirebaseStorage.getInstance().getReference("UsersImages");
        exit = findViewById(R.id.exit);
        if (currentUser.getEmail().contains(".")) {
            emailUpdated = currentUser.getEmail().replace(".", " ");
        } else {
            emailUpdated = currentUser.getEmail();
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("AllUsersData").child(emailUpdated).child("images");

        galleryViewPager = new GalleryViewPager(this, data,pos);
        // Read from the database


        viewPager.setAdapter(galleryViewPager);
        viewPager.setCurrentItem(position);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        add = findViewById(R.id.add);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                CustomIntent.customType(GalleryActivity.this, "right-to-left");

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(GalleryActivity.this);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position < data.size()) {
                    position++;
                    if (position != data.size()) {
                        counter.setText(position + 1 + "/" + data.size());
                    }
                    viewPager.setCurrentItem(position);
                }
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                counter.setText(position + 1 + "/" + data.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position > 0) {
                    position--;
                    counter.setText(position + 1 + "/" + data.size());
                    viewPager.setCurrentItem(position);
                }
            }
        });

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

    private void uploadFile() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(GalleryActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
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
                                Toast.makeText(GalleryActivity.this, "Image uploaded successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(GalleryActivity.this, "Image upload failed", Toast.LENGTH_LONG).show();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomIntent.customType(GalleryActivity.this, "right-to-left");
    }
}
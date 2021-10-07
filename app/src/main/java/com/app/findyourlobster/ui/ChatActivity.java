package com.app.findyourlobster.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.findyourlobster.R;
import com.app.findyourlobster.data.Messages;
import com.app.findyourlobster.data.MessagesAdapter;
import com.app.findyourlobster.data.messagingservice.APIService;
import com.app.findyourlobster.data.messagingservice.Client;
import com.app.findyourlobster.data.messagingservice.Data;
import com.app.findyourlobster.data.messagingservice.MyResponse;
import com.app.findyourlobster.data.messagingservice.NotificationSender;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import maes.tech.intentanim.CustomIntent;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    public static RecyclerView recyclerView;
    public static Context context;
    Window window;
    MessagesAdapter messagesAdapter;
    String messageSenderId, messageReceiverId, saveCurrentDate, saveCurrentTime, displayName;
    TextView receiverName;
    FloatingActionButton send, back, menu;
    EditText textInputEditText;
    ImageView userImage;
    FloatingActionButton sendDoc;
    Uri fileUri;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference myRef, myRef2;
    private ArrayList<Messages> messagesList = new ArrayList<>();
    private DatabaseReference root;
    private View theMenu;
    private View menu1;
    private View menu2;
    private View menu3;
    private View menu4;
    private View menu5;
    private View menu6;
    private View overlay;
    String token,senderDisplayName;



    private boolean menuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        messagesAdapter = new MessagesAdapter(messagesList);
        sendDoc = findViewById(R.id.sendDoc);
        recyclerView = findViewById(R.id.chatRecycler);
        userImage = findViewById(R.id.imageView16);
        receiverName = findViewById(R.id.receiverName);
        send = findViewById(R.id.sendMessage);
        back = findViewById(R.id.backButton);
        menu = findViewById(R.id.menu);
        textInputEditText = findViewById(R.id.textInput);
        root = FirebaseDatabase.getInstance().getReference();
        theMenu = findViewById(R.id.the_menu);
        menu1 = findViewById(R.id.menu1);
        menu2 = findViewById(R.id.menu2);
        menu3 = findViewById(R.id.menu3);
        menu4 = findViewById(R.id.menu4);
        menu5 = findViewById(R.id.menu5);
        menu6 = findViewById(R.id.menu6);
        overlay = findViewById(R.id.overlay);
        Typeface typeface = ResourcesCompat.getFont(ChatActivity.context, R.font.minuscre);
        textInputEditText.setTypeface(typeface);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        try {
            String url;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if (extras == null) {
                    messageReceiverId = "";
                    url = "";
                    displayName = "";
                } else {
                    messageReceiverId = extras.getString("email");
                    url = extras.getString("imageURL");
                    displayName = extras.getString("username");
                }
            } else {
                messageReceiverId = (String) savedInstanceState.getSerializable("email");
                url = (String) savedInstanceState.getSerializable("imageURL");
                displayName = (String) savedInstanceState.getSerializable("username");


            }
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            String emailUpdated = "";
            if (user.getEmail().contains(".")) {
                emailUpdated = user.getEmail().replace(".", " ");
            } else {
                emailUpdated = user.getEmail();
            }
            messageSenderId = emailUpdated;

            if (!url.equals("")) {

                Glide.with(this)
                        .asBitmap()
                        .load(url)
                        .into(userImage);
            }
            myRef = FirebaseDatabase.getInstance().getReference("AllUsersData");
            myRef2 = FirebaseDatabase.getInstance().getReference("UsersProfileImages");
            receiverName.setText(displayName);
            FetchMessages();
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!textInputEditText.getText().toString().equals(""))
                        sendMessage();
                }
            });

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final PopupMenu menuItems = new PopupMenu(ChatActivity.this, v);

                    menuItems.getMenu().add(1, 1, 1, "Clear Messages");
                    menuItems.getMenu().add(1, 2, 2, "Block User");
                    menuItems.getMenu().add(1, 3, 3, "View Profile");
                    menuItems.show();
                    menuItems.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if (menuItem.getItemId() == 1) {
                                Intent intent = new Intent(ChatActivity.this, ClearMessagesActivity.class);
                                intent.putExtra("email", messageReceiverId);
                                //intent.putExtra("imageURL",usersImages.get(position));
                                intent.putExtra("username", displayName);
                                startActivity(intent);
                                CustomIntent.customType(ChatActivity.this, "up-to-down");
                                return true;
                            }
                            if (menuItem.getItemId() == 2) {
                                Intent intent = new Intent(ChatActivity.this, BlockActivity.class);
                                intent.putExtra("email", messageReceiverId);
                                //intent.putExtra("imageURL",usersImages.get(position));
                                intent.putExtra("username", displayName);
                                startActivity(intent);
                                CustomIntent.customType(ChatActivity.this, "up-to-down");

                                return true;
                            }
                            if (menuItem.getItemId() == 3) {
                                Intent intent = new Intent(ChatActivity.this, MoreInfoActivity.class);
                                intent.putExtra("email", messageReceiverId);
                                //intent.putExtra("imageURL",usersImages.get(position));
                                //intent.putExtra("username",usersNames.get(position));
                                startActivity(intent);
                                CustomIntent.customType(ChatActivity.this, "right-to-left");

                                return true;
                            } else
                                return false;
                        }
                    });


                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(messagesAdapter);

            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

            myRef2 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(messageSenderId).child("userInfo").child("name");
            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    senderDisplayName = snapshot.child("displayName").getValue().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            sendDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!menuOpen) {
                        revealMenu();
                    } else {
                        hideMenu();
                    }

                    /*
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, 1);
                */
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void FetchMessages() {
        try {
            root.child("Chats").child(messageSenderId).child(messageReceiverId)
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {

                                if (!dataSnapshot.getValue().equals("")) {
                                    Messages messages = dataSnapshot.getValue(Messages.class);
                                    messagesList.add(messages);
                                    messagesAdapter.notifyItemInserted(messagesAdapter.getItemCount());

                                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                                }
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendMessage() {




        try {
            String message = textInputEditText.getText().toString();
            String messagesender_ref = "Chats/" + messageSenderId + "/" + messageReceiverId;
            String messagereceiver_ref = "Chats/" + messageReceiverId + "/" + messageSenderId;
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss:a");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            DatabaseReference databaseReference = root.child("Chats").child(messageSenderId).child(messageReceiverId).push();
            String message_push_id = databaseReference.getKey();

            Map hashMap = new HashMap<>();
            hashMap.put("sender", messageSenderId);
            hashMap.put("message", message);
            hashMap.put("to", messageReceiverId);
            hashMap.put("date", saveCurrentDate);
            hashMap.put("time", saveCurrentTime);
            hashMap.put("SenderTimeZone",TimeZone.getDefault().getID());
            hashMap.put("type", "text");
            Map hashMap2 = new HashMap<>();
            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);
            root.updateChildren(hashMap2).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    root.child("Chats").child(messageSenderId).child(messageReceiverId).child("Cleared").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            textInputEditText.setText("");
                            myRef2 = FirebaseDatabase.getInstance().getReference("AllUsersData").child(messageReceiverId);
                            myRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    token = snapshot.child("token").getValue().toString();

                                    Log.i("HEREisTheToken",token);
                                    sendNotification(token,senderDisplayName,message);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    });

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Messages";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.logo)
                        .setContentTitle("FindYourLobster")
                        .setContentText(messageBody)
                        //.addPerson(token)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "FindYourLobster",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 , notificationBuilder.build());
    }*/
    public void sendNotification(String usertoken, String title, String message) {

        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);
        APIService apiService;
        Log.i("HEREAGAIN",usertoken);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NotNull Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(ChatActivity.context, "Failed ", Toast.LENGTH_LONG);
                        Log.i("Failed","Failedhere");

                    }else{
                        Log.i("Success","here");

                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Log.i("Failed","Failed");
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(chatActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        */
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Files");
            final String messagesender_ref = "Chats/" + messageSenderId + "/" + messageReceiverId;
            final String messagereceiver_ref = "Chats/" + messageReceiverId + "/" + messageSenderId;
            DatabaseReference databaseReference = root.child("Chats").child(messageSenderId).child(messageReceiverId).push();
            final String message_push_id = databaseReference.getKey();
            final StorageReference filePath = storageReference.child(messageSenderId + messageReceiverId + message_push_id + "." + "pdf");
            filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            Map hashMap = new HashMap<>();
                            hashMap.put("sender", messageSenderId);
                            hashMap.put("message", downloadUri.toString());
                            hashMap.put("to", messageReceiverId);
                            hashMap.put("date", saveCurrentDate);
                            hashMap.put("time", saveCurrentTime);
                            hashMap.put("type", "pdf");
                            Map hashMap2 = new HashMap<>();
                            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
                            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);
                            root.updateChildren(hashMap2);

                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                   /* progressDialog.setMessage((int) p + " % Uploading....");
                    progressDialog.setTitle("PDF");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                */
                }
            });


        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            fileUri = data.getData();
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Files");
            final String messagesender_ref = "Chats/" + messageSenderId + "/" + messageReceiverId;
            final String messagereceiver_ref = "Chats/" + messageReceiverId + "/" + messageSenderId;
            DatabaseReference databaseReference = root.child("Chats").child(messageSenderId).child(messageReceiverId).push();
            final String message_push_id = databaseReference.getKey();
            final StorageReference filePath = storageReference.child(messageSenderId + messageReceiverId + message_push_id + "." + "jpg");

            filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            Map hashMap = new HashMap<>();
                            hashMap.put("sender", messageSenderId);
                            hashMap.put("message", downloadUri.toString());
                            hashMap.put("to", messageReceiverId);
                            hashMap.put("date", saveCurrentDate);
                            hashMap.put("time", saveCurrentTime);
                            hashMap.put("type", "image");
                            Map hashMap2 = new HashMap<>();
                            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
                            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);
                            root.updateChildren(hashMap2);
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                   /* progressDialog.setMessage((int) p + " % Uploading....");
                    progressDialog.setTitle("PDF");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                */
                }
            });
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime = currentTime.format(calForTime.getTime());
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Files");
            final String messagesender_ref = "Chats/" + messageSenderId + "/" + messageReceiverId;
            final String messagereceiver_ref = "Chats/" + messageReceiverId + "/" + messageSenderId;
            DatabaseReference databaseReference = root.child("Chats").child(messageSenderId).child(messageReceiverId).push();
            final String message_push_id = databaseReference.getKey();
            final StorageReference filePath = storageReference.child(messageSenderId + messageReceiverId + message_push_id + "." + "mp4");
            filePath.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            Map hashMap = new HashMap<>();
                            hashMap.put("sender", messageSenderId);
                            hashMap.put("message", downloadUri.toString());
                            hashMap.put("to", messageReceiverId);
                            hashMap.put("date", saveCurrentDate);
                            hashMap.put("time", saveCurrentTime);
                            hashMap.put("type", "video");
                            Map hashMap2 = new HashMap<>();
                            hashMap2.put(messagesender_ref + "/" + message_push_id, hashMap);
                            hashMap2.put(messagereceiver_ref + "/" + message_push_id, hashMap);
                            root.updateChildren(hashMap2);
                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double p = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                   /* progressDialog.setMessage((int) p + " % Uploading....");
                    progressDialog.setTitle("PDF");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                */
                }
            });
        } else if (requestCode == 4 && resultCode == RESULT_OK && data != null && data.getData() != null) {

        } else if (requestCode == 5 && resultCode == RESULT_OK && data != null && data.getData() != null) {

        } else if (requestCode == 6 && resultCode == RESULT_OK && data != null && data.getData() != null) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_languages) {


        }
        return super.onOptionsItemSelected(item);
    }

    public void revealMenu() {
        menuOpen = true;

        theMenu.setVisibility(View.INVISIBLE);
        int cx = theMenu.getRight() - 200;
        int cy = theMenu.getTop();
        int finalRadius = Math.max(theMenu.getWidth(), theMenu.getHeight());
        Animator anim;
        anim = ViewAnimationUtils.createCircularReveal(theMenu, cx, cy, 0, finalRadius);
        anim.setDuration(600);
        theMenu.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        anim.start();


        // Animate The Icons One after the other, I really would like to know if there is any
        // simpler way to do it
        Animation popeup1 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup2 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup3 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup4 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup5 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        Animation popeup6 = AnimationUtils.loadAnimation(this, R.anim.popeup);
        popeup1.setStartOffset(50);
        popeup2.setStartOffset(100);
        popeup3.setStartOffset(150);
        popeup4.setStartOffset(200);
        popeup5.setStartOffset(250);
        popeup6.setStartOffset(300);
        menu1.startAnimation(popeup1);
        menu2.startAnimation(popeup2);
        menu3.startAnimation(popeup3);
        menu4.startAnimation(popeup4);
        menu5.startAnimation(popeup5);
        menu6.startAnimation(popeup6);

    }

    public void hideMenu() {
        menuOpen = false;
        int cx = theMenu.getRight() - 200;
        int cy = theMenu.getTop();
        int initialRadius = theMenu.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(theMenu, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                theMenu.setVisibility(View.INVISIBLE);
                theMenu.setVisibility(View.GONE);
                overlay.setVisibility(View.INVISIBLE);
                overlay.setVisibility(View.GONE);
            }
        });
        anim.start();
    }

    @Override
    public void onBackPressed() {
        if (menuOpen) {
            hideMenu();
        } else {
            finishAfterTransition();
        }
    }

    public void overlayClick(View v) {
        hideMenu();
    }

    public void menuClick(View view) {
        hideMenu();
        if (view.getTag().equals("image")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, 2);
        } else if (view.getTag().equals("document")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        } else if (view.getTag().equals("video")) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, 3);
        } else if (view.getTag() == "audio") {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        } else if (view.getTag() == "location") {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        } else if (view.getTag() == "contact") {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, 1);
        }
    }
}
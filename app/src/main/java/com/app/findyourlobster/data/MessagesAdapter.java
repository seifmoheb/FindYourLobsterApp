package com.app.findyourlobster.data;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.app.findyourlobster.R;
import com.app.findyourlobster.ui.ChatActivity;
import com.app.findyourlobster.ui.ImageDisplayActivity;
import com.app.findyourlobster.ui.VideoDisplayActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import static android.provider.Settings.System.DATE_FORMAT;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    public static ArrayList<Messages> userMessagesList;
    FirebaseAuth auth;
    FirebaseUser user;
    private DatabaseReference databaseReference;

    public MessagesAdapter(ArrayList<Messages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_layout, parent, false);
        return new MessagesViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {


        try {
            String emailUpdated = "";
            if (user.getEmail().contains(".")) {
                emailUpdated = user.getEmail().replace(".", " ");
            } else {
                emailUpdated = user.getEmail();
            }
            final String messageSenderId = emailUpdated;
            final Messages messages = userMessagesList.get(position);
            String type = messages.getType();
            String fromUserId = messages.getSender();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Chats").child(fromUserId);
            holder.senderMessageText.setVisibility(View.INVISIBLE);
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.senderImageLayout.setVisibility(View.GONE);
            holder.receiverMessageVideoFrame.setVisibility(View.GONE);
            holder.senderMessageVideoFrame.setVisibility(View.GONE);

            holder.right.setVisibility(View.INVISIBLE);
            holder.left.setVisibility(View.INVISIBLE);


            if (type.equals("text")) {

                    if (fromUserId.equals(messageSenderId)) {
                        holder.right.setVisibility(View.VISIBLE);
                        holder.senderMessageText.setVisibility(View.VISIBLE);
                        holder.senderMessageText.setBackgroundResource(R.drawable.right_message);
                        holder.senderMessageText.setTextColor(Color.WHITE);
                        holder.senderMessageText.setGravity(Gravity.LEFT);
                        holder.senderMessageText.setText(messages.getMessage());

                        //or to support all versions use
                        Typeface typeface = ResourcesCompat.getFont(ChatActivity.context, R.font.minuscre);
                        holder.senderMessageText.setTypeface(typeface);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:a");
                        LocalDateTime ldt = LocalDateTime.parse(messages.getDate()+" "+messages.getTime(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:a"));
                        ZoneId fromZoneId = ZoneId.of(messages.getTimeZone());
                        ZoneId toZoneId = ZoneId.of(TimeZone.getDefault().getID());
                        ZonedDateTime fromZonedDateTime = ldt.atZone(fromZoneId);
                        ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(toZoneId);
                        String newTimeDate = formatter.format(toZonedDateTime);
                        String[] newTimeDateSplitted = newTimeDate.split(" ");
                        String[] time = newTimeDateSplitted[1].split(":");
                        holder.dateSender.setText(time[0] + ":" + time[1]+" "+time[3]);
                        holder.right.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                holder.date.setText(newTimeDateSplitted[0]);

                                if (holder.date.getVisibility() == View.VISIBLE) {
                                    holder.date.setVisibility(View.GONE);
                                } else {
                                    holder.date.setVisibility(View.VISIBLE);
                                }

                            }
                        });

                    } else {
                        holder.left.setVisibility(View.VISIBLE);
                        holder.receiverMessageText.setVisibility(View.VISIBLE);
                        holder.receiverMessageText.setBackgroundResource(R.drawable.left_message);
                        holder.receiverMessageText.setTextColor(Color.WHITE);
                        holder.receiverMessageText.setGravity(Gravity.LEFT);
                        Typeface typeface = ResourcesCompat.getFont(ChatActivity.context, R.font.minuscre);
                        holder.receiverMessageText.setTypeface(typeface);
                        holder.receiverMessageText.setText(messages.getMessage());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:a");
                        LocalDateTime ldt = LocalDateTime.parse(messages.getDate()+" "+messages.getTime(), DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss:a"));
                        ZoneId fromZoneId = ZoneId.of(messages.getTimeZone());
                        ZoneId toZoneId = ZoneId.of(TimeZone.getDefault().getID());
                        ZonedDateTime fromZonedDateTime = ldt.atZone(fromZoneId);
                        ZonedDateTime toZonedDateTime = fromZonedDateTime.withZoneSameInstant(toZoneId);
                        String newTimeDate = formatter.format(toZonedDateTime);
                        String[] newTimeDateSplitted = newTimeDate.split(" ");
                        String[] time = newTimeDateSplitted[1].split(":");

                        holder.dateReceiver.setText(time[0] + ":" + time[1]+" "+time[3]);
                        holder.left.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                holder.date.setText(newTimeDateSplitted[0]);

                                if (holder.date.getVisibility() == View.VISIBLE) {
                                    holder.date.setVisibility(View.GONE);

                                } else {
                                    holder.date.setVisibility(View.VISIBLE);

                                }
                            }
                        });
                    }

            } else if (type.equals("image")) {
                if (fromUserId.equals(messageSenderId)) {

                        holder.senderImageLayout.setVisibility(View.VISIBLE);
                        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ChatActivity.context);
                        circularProgressDrawable.setStrokeWidth(5f);
                        circularProgressDrawable.setCenterRadius(30f);
                        circularProgressDrawable.start();

                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(circularProgressDrawable);
                        requestOptions.skipMemoryCache(true);
                        requestOptions.fitCenter();

                        final String url = messages.getMessage();
                        Glide.with(ChatActivity.context)
                                .asBitmap()
                                .load(url)
                                .apply(requestOptions)
                                .into(holder.senderMessageImage);

                        String[] time = messages.getTime().split(":");
                        holder.dateImageSender.setText(time[0] + ":" + time[1]);
                        holder.senderImageLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatActivity.context, ImageDisplayActivity.class);
                                intent.putExtra("uri", url);
                                intent.putExtra("date", messages.getDate());
                                intent.putExtra("time", messages.getTime());
                                intent.putExtra("sender", messages.getSender());
                                ChatActivity.context.startActivity(intent);
                            }
                        });

                } else {
                    holder.receiverImageLayout.setVisibility(View.VISIBLE);

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ChatActivity.context);
                    circularProgressDrawable.setStrokeWidth(5f);
                    circularProgressDrawable.setCenterRadius(30f);
                    circularProgressDrawable.start();

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(circularProgressDrawable);
                    requestOptions.skipMemoryCache(true);
                    requestOptions.fitCenter();

                    final String url = messages.getMessage();
                    Glide.with(ChatActivity.context)
                            .asBitmap()
                            .load(url)
                            .apply(requestOptions)
                            .into(holder.receiverMessageImage);

                    String[] time = messages.getTime().split(":");
                    holder.dateImageReceiver.setText(time[0] + ":" + time[1]);
                    holder.receiverImageLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.context, ImageDisplayActivity.class);
                            intent.putExtra("uri",url);
                            intent.putExtra("date",messages.getDate());
                            intent.putExtra("time",messages.getTime());
                            intent.putExtra("sender",messages.getSender());
                            ChatActivity.context.startActivity(intent);
                        }
                    });
                }
            } else if (type.equals("video")) {
                if (fromUserId.equals(messageSenderId)) {
                    final Messages messagesDelivered = userMessagesList.get(position-1);

                    if(!messages.sender.equals(messagesDelivered.sender) && !messages.time.equals(messagesDelivered.time) && !messages.to.equals(messagesDelivered.to) && !messages.message.equals(messagesDelivered.message)) {

                        holder.senderMessageVideoFrame.setVisibility(View.VISIBLE);
                        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ChatActivity.context);
                        circularProgressDrawable.setStrokeWidth(5f);
                        circularProgressDrawable.setCenterRadius(30f);
                        circularProgressDrawable.start();

                        String[] time = messages.getTime().split(":");
                        holder.dateVideoSender.setText(time[0] + ":" + time[1]);

                        final String url = messages.getMessage();
                        final Uri uri = Uri.parse(url);
                        holder.senderMessageVideo.setVideoURI(uri);
                        holder.senderMessageVideo.requestFocus();
                        holder.senderMessageVideo.seekTo(holder.senderMessageVideo.getCurrentPosition() + 3000);
                        holder.play1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(ChatActivity.context, VideoDisplayActivity.class);
                                intent.putExtra("uri", url);
                                intent.putExtra("date", messages.getDate());
                                intent.putExtra("time", messages.getTime());
                                intent.putExtra("sender", messages.getSender());
                                ChatActivity.context.startActivity(intent);
                            }
                        });
                    }
                } else {
                    holder.receiverMessageVideoFrame.setVisibility(View.VISIBLE);

                    CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(ChatActivity.context);
                    circularProgressDrawable.setStrokeWidth(5f);
                    circularProgressDrawable.setCenterRadius(30f);
                    circularProgressDrawable.start();

                    String[] time = messages.getTime().split(":");
                    holder.dateVideoReceiver.setText(time[0] + ":" + time[1]);

                    final String url = messages.getMessage();
                    final Uri uri = Uri.parse(url);

                    holder.receiverMessageVideo.seekTo(holder.receiverMessageVideo.getCurrentPosition() + 3000);
                    holder.play2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatActivity.context, VideoDisplayActivity.class);
                            intent.putExtra("uri",url);
                            intent.putExtra("date",messages.getDate());
                            intent.putExtra("time",messages.getTime());
                            intent.putExtra("sender",messages.getSender());
                            ChatActivity.context.startActivity(intent);                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


    public class MessagesViewHolder extends RecyclerView.ViewHolder {
        public TextView senderMessageText;
        public TextView receiverMessageText;
        public ImageView senderMessageImage;
        public ImageView receiverMessageImage;
        public LinearLayout senderMessageVideoFrame;
        public LinearLayout receiverMessageVideoFrame;
        public VideoView senderMessageVideo;
        public VideoView receiverMessageVideo;
        public TextView dateSender, dateReceiver, date;
        public TextView dateImageSender, dateImageReceiver;
        public TextView dateVideoSender, dateVideoReceiver;

        ImageButton play1, play2;
        LinearLayout senderImageLayout,receiverImageLayout;
        RelativeLayout right, left;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            senderMessageImage = itemView.findViewById(R.id.message_sender_image_view);
            receiverMessageImage = itemView.findViewById(R.id.message_receiver_image_view);
            senderMessageVideo = itemView.findViewById(R.id.message_sender_video_view);
            receiverMessageVideo = itemView.findViewById(R.id.message_receiver_video_view);
            senderMessageVideoFrame = itemView.findViewById(R.id.message_sender_video_view_frame);
            receiverMessageVideoFrame = itemView.findViewById(R.id.message_receiver_video_view_frame);
            senderImageLayout = itemView.findViewById(R.id.senderImageLayout);
            receiverImageLayout = itemView.findViewById(R.id.receiverImageLayout);
            dateSender = itemView.findViewById(R.id.date_sender);
            dateReceiver = itemView.findViewById(R.id.date_receiver);
            dateImageSender = itemView.findViewById(R.id.date_sender_image);
            dateImageReceiver = itemView.findViewById(R.id.date_receiver_image);
            dateVideoSender = itemView.findViewById(R.id.date_sender_video);
            dateVideoReceiver = itemView.findViewById(R.id.date_receiver_video);
            date = itemView.findViewById(R.id.date);
            right = itemView.findViewById(R.id.right);
            left = itemView.findViewById(R.id.left);
            auth = FirebaseAuth.getInstance();
            user = auth.getCurrentUser();
            play1 = itemView.findViewById(R.id.play_button);
            play2 = itemView.findViewById(R.id.play_button2);

        }
    }

}

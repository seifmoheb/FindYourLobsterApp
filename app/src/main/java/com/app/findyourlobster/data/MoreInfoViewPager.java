package com.app.findyourlobster.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.app.findyourlobster.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MoreInfoViewPager extends PagerAdapter {
    Context context;
    List<String> imagesList;
    int countImages = 0;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef2;
    FirebaseAuth auth;
    String emailUpdated;
    String deleteNode;

    public MoreInfoViewPager(Context context, List<String> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.moreinfo_screen, null);
        ImageView imageView = view.findViewById(R.id.galleryItem);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser().getEmail().contains(".")) {
            emailUpdated = auth.getCurrentUser().getEmail().replace(".", " ");
        } else {
            emailUpdated = auth.getCurrentUser().getEmail();
        }
        database = FirebaseDatabase.getInstance();
        myRef2 = database.getReference("AllUsersData").child(emailUpdated).child("starred");

        Glide.with(context)
                .load(imagesList.get(position))
                .placeholder(R.color.colorPrimaryDark)
                .into(imageView);

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

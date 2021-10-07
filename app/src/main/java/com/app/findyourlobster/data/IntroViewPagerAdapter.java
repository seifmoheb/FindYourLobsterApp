package com.app.findyourlobster.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.app.findyourlobster.R;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {
    Context context;
    List<ScreenItem> screenItemList;

    public IntroViewPagerAdapter(int numOfTabs, Context context, List<ScreenItem> screenItemList) {
        this.context = context;
        this.screenItemList = screenItemList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_screen, null);
        ImageView imageView = view.findViewById(R.id.intro_image);
        TextView intro_text = view.findViewById(R.id.intro_title);
        TextView intro_desc = view.findViewById(R.id.intro_description);
        intro_text.setText(screenItemList.get(position).getTitle());
        intro_desc.setText(screenItemList.get(position).getDescription());
        imageView.setImageResource(screenItemList.get(position).getScreenImg());

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return screenItemList.size();
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

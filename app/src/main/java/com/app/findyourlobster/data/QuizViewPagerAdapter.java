package com.app.findyourlobster.data;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.app.findyourlobster.R;

import java.util.List;

public class QuizViewPagerAdapter extends PagerAdapter {
    Context context;
    List<QuizItems> screenItemList;
    int question1 = 0;
    int question2 = 0;
    int question3 = 0;
    int question4 = 0;
    int question5 = 0;
    int question6 = 0;
    int question7 = 0;
    int question8 = 0;
    int question9 = 0;
    int question10 = 0;


    public QuizViewPagerAdapter(int numOfTabs, Context context, List<QuizItems> screenItemList) {
        this.context = context;
        this.screenItemList = screenItemList;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.quiz_screen, null);
        TextView question_text = view.findViewById(R.id.question);
        question_text.setText(screenItemList.get(position).getQuestion());
        final RadioButton choiceOne = view.findViewById(R.id.radioButton1);
        final RadioButton choiceTwo = view.findViewById(R.id.radioButton2);
        final RadioButton choiceThree = view.findViewById(R.id.radioButton3);
        final RadioButton choiceFour = view.findViewById(R.id.radioButton4);
        choiceOne.setText(screenItemList.get(position).getChoiceOne());
        choiceTwo.setText(screenItemList.get(position).getChoiceTwo());
        choiceThree.setText(screenItemList.get(position).getChoiceThree());
        choiceFour.setText(screenItemList.get(position).getChoiceFour());
        TextView tv = (TextView) view.findViewById(R.id.timer);

        Typeface typeface = ResourcesCompat.getFont(context, R.font.digital);
        tv.setTypeface(typeface);
        container.addView(view);
        if (position == 0) {
            if (choiceOne.getText().equals(screenItemList.get(position).getCorrectAnswer())) {
                choiceOne.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                        if (b) {
                            question1 = 1;

                        } else {
                            question1 = 0;
                        }
                    }
                });
            }
            if (choiceTwo.getText().equals(screenItemList.get(position).getCorrectAnswer())) {
                choiceTwo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                        if (b) {
                            question1 = 1;

                        } else {
                            question1 = 0;
                        }
                    }
                });
            }
            if (choiceThree.getText().equals(screenItemList.get(position).getCorrectAnswer())) {
                choiceThree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                        if (b) {
                            question1 = 1;

                        } else {
                            question1 = 0;
                        }
                    }
                });
            }
            if (choiceFour.getText().equals(screenItemList.get(position).getCorrectAnswer())) {
                choiceFour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                        if (b) {
                            question1 = 1;

                        } else {
                            question1 = 0;
                        }
                    }
                });
            }

        }

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

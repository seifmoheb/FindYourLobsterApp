package com.app.findyourlobster.data.internal;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.app.findyourlobster.data.Direction;
import com.app.findyourlobster.data.RewindAnimationSetting;
import com.app.findyourlobster.data.StackFrom;
import com.app.findyourlobster.data.SwipeAnimationSetting;
import com.app.findyourlobster.data.SwipeableMethod;

import java.util.List;

public class CardStackSetting {
    public StackFrom stackFrom = StackFrom.None;
    public int visibleCount = 3;
    public float translationInterval = 8.0f;
    public float scaleInterval = 0.95f; // 0.0f - 1.0f
    public float swipeThreshold = 0.3f; // 0.0f - 1.0f
    public float maxDegree = 20.0f;
    public List<Direction> directions = Direction.HORIZONTAL;
    public boolean canScrollHorizontal = true;
    public boolean canScrollVertical = true;
    public SwipeableMethod swipeableMethod = SwipeableMethod.AutomaticAndManual;
    public SwipeAnimationSetting swipeAnimationSetting = new SwipeAnimationSetting.Builder().build();
    public RewindAnimationSetting rewindAnimationSetting = new RewindAnimationSetting.Builder().build();
    public Interpolator overlayInterpolator = new LinearInterpolator();
}

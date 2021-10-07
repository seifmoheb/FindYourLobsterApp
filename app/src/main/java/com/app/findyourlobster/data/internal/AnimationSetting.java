package com.app.findyourlobster.data.internal;

import android.view.animation.Interpolator;

import com.app.findyourlobster.data.Direction;

public interface AnimationSetting {
    Direction getDirection();

    int getDuration();

    Interpolator getInterpolator();
}

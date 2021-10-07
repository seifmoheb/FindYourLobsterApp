package com.app.findyourlobster.data;

public enum Duration {
    Fast(100),
    Normal(1500),
    Slow(5000);

    public final int duration;

    Duration(int duration) {
        this.duration = duration;
    }

    public static Duration fromVelocity(int velocity) {
        if (velocity < 1000) {
            return Slow;
        } else if (velocity < 5000) {
            return Normal;
        }
        return Fast;
    }
}

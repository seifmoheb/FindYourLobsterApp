package com.app.findyourlobster.data;

import android.graphics.drawable.Drawable;

public class squares {
    String text;


    Drawable image;

    public squares(String text, Drawable image) {
        this.text = text;
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

}

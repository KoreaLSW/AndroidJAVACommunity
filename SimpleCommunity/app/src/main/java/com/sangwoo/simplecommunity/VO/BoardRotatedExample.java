package com.sangwoo.simplecommunity.VO;

import android.graphics.Bitmap;

public class BoardRotatedExample {
    Bitmap picture;

    public BoardRotatedExample(Bitmap picture) {
        this.picture = picture;
    }

    public  Bitmap getPicture() {
        return picture;
    }

    public void setPicture( Bitmap picture) {
        this.picture = picture;
    }
}

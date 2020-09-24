package com.sangwoo.simplecommunity.VO;

import android.graphics.Bitmap;

public class BoardExample {
    Bitmap picture;

    public BoardExample( Bitmap picture) {
        this.picture = picture;
    }

    public  Bitmap getPicture() {
        return picture;
    }

    public void setPicture( Bitmap picture) {
        this.picture = picture;
    }
}

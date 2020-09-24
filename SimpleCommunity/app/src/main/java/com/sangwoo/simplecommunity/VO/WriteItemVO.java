package com.sangwoo.simplecommunity.VO;

import android.graphics.Bitmap;
import android.widget.EditText;

public class WriteItemVO {

    Bitmap imageView;
    EditText editText;

    public WriteItemVO(Bitmap imageView) {
        this.imageView = imageView;
    }

    public WriteItemVO(Bitmap imageView, EditText editText) {
        this.imageView = imageView;
        this.editText = editText;
    }

    public Bitmap getImageView() {
        return imageView;
    }

    public void setImageView(Bitmap imageView) {
        this.imageView = imageView;
    }

    public EditText getEditText() {
        return editText;
    }

    public void setEditText(EditText editText) {
        this.editText = editText;
    }
}

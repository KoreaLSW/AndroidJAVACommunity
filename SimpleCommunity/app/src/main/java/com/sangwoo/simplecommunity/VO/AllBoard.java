package com.sangwoo.simplecommunity.VO;

import android.graphics.Bitmap;

public class AllBoard {

    int idx;
    String user;
    String data;
    String title;
    Bitmap picture;
    String Strpicture;
    String content;

    public AllBoard(String user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public AllBoard(String user, String title, Bitmap picture, String content) {
        this.user = user;
        this.title = title;
        this.picture = picture;
        this.content = content;
    }

    public AllBoard(int idx, String user, String data, String title,  String content) {
        this.idx = idx;
        this.user = user;
        this.data = data;
        this.title = title;
        this.content = content;
    }

    public AllBoard(String user, String data, String title, Bitmap picture, String content) {
        this.user = user;
        this.data = data;
        this.title = title;
        this.picture = picture;
        this.content = content;
    }

    public AllBoard(int idx, String user, String data, String title, Bitmap picture, String content) {
        this.idx = idx;
        this.user = user;
        this.data = data;
        this.title = title;
        this.picture = picture;
        this.content = content;
    }

    public AllBoard(String user, String data, String title, String Strpicture, String content) {
        this.user = user;
        this.data = data;
        this.title = title;
        this.Strpicture = Strpicture;
        this.content = content;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStrpicture() {
        return Strpicture;
    }

    public void setStrpicture(String strpicture) {
        Strpicture = strpicture;
    }
}

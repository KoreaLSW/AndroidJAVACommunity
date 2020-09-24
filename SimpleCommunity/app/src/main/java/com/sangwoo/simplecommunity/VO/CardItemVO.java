package com.sangwoo.simplecommunity.VO;

public class CardItemVO {
    private String title;
    private String user;

    public CardItemVO(String title, String user) {
        this.title = title;
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

package com.sangwoo.simplecommunity.ShootingGame;

public class RankingVO {

    String user;
    String score;
    String data;

    public RankingVO(String user, String score, String data) {
        this.user = user;
        this.score = score;
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

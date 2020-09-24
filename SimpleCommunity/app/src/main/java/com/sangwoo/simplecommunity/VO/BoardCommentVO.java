package com.sangwoo.simplecommunity.VO;

public class BoardCommentVO {

    int idx;
    String user;
    String data;
    String content;
    String seq;
    String lvl;

    public BoardCommentVO(int idx, String user, String data, String content, String seq, String lvl) {
        this.idx = idx;
        this.user = user;
        this.data = data;
        this.content = content;
        this.seq = seq;
        this.lvl = lvl;
    }

    public BoardCommentVO(String user, String data, String content) {
        this.user = user;
        this.data = data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getLvl() {
        return lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }
}

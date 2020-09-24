package com.sangwoo.simplecommunity.ShootingGame;

public class Missile {
    //미사일을 한개씩 생성하여 비행기 위치에 저장
    int x, y;

    public Missile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(){
        y -= 10; // 미사일이 위로 이동
    }

    // 미사일이 화면을 벗어났는지 판단
    public boolean isDead(){
        if(y < 0) {
            return true;
        }else {
            return false;
        }
    }
}

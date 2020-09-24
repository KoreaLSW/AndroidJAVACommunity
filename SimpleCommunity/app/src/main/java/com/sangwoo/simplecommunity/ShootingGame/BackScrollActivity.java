package com.sangwoo.simplecommunity.ShootingGame;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class BackScrollActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_back_scroll);
        setContentView( new GameView(BackScrollActivity.this));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}

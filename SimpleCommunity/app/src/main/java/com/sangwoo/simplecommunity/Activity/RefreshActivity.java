package com.sangwoo.simplecommunity.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.sangwoo.simplecommunity.R;

public class RefreshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh);
        Toast.makeText(this, "123123", Toast.LENGTH_SHORT).show();
    }
}

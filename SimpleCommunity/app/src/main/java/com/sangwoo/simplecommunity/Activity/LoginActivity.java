package com.sangwoo.simplecommunity.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.Request.LoginRequest;
import com.sangwoo.simplecommunity.MainActivity;
import com.sangwoo.simplecommunity.R;

import org.json.JSONObject;

import java.util.UUID;

public class LoginActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_RESULT = 1;
    String deviceId;

    EditText idText, passwordText;
    Button loginButton;
    TextView registerButton;

    private AlertDialog dialog;

    public static String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //android O fix bug orientation
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        deviceId();

        try {
            Intent intent = getIntent();
            String ToastText = intent.getExtras().getString("Toast");
            Toast.makeText(LoginActivity.this, ToastText, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }

        idText = findViewById(R.id.idText);
        passwordText = findViewById(R.id.passwordText);

        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                Response.Listener<String> responseLister = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인에 성공했습니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();

                                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                                SharedPreferences.Editor editor = pref.edit(); // editor에 put하기
                                editor.putString("userID", userID);
                                editor.commit();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.putExtra("userID", userID);
                                LoginActivity.this.startActivity(intent);
                                finish();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요")
                                        .setNegativeButton("다시 시도", null)
                                        .create();
                                dialog.show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseLister);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

            }
        });


    }


    // 로그인화면에서 회원가입 화면으로
    private void register() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 권한 허용시 회원가입 할 수 있음
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            } else {
                // 권한 거부
                Toast.makeText(getApplicationContext(), "권한을 허용해 주세요", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_RESULT);
            }
        }

    }

    public void deviceId() {
        // 핸드폰 고유번호 받기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                final TelephonyManager tm = (TelephonyManager) LoginActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                final String tmDevice, tmSerial, androidId;
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                deviceId = deviceUuid.toString();
                //Log.d("테그확인", deviceId);
            } else {
                Toast.makeText(getApplicationContext(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSIONS_REQUEST_RESULT);
            }
        }
    }
    private class LodingProgressDialog extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog progressDialog; // API 26에서 deprecated
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("로그인 중....");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            userID = idText.getText().toString();
            final String userPassword = passwordText.getText().toString();
            Response.Listener<String> responseLister = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        if(success) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            dialog = builder.setMessage("로그인에 성공했습니다.")
                                    .setPositiveButton("확인", null)
                                    .create();
                            dialog.show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("userID", userID);
                            LoginActivity.this.startActivity(intent);
                            finish();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            dialog = builder.setMessage("계정을 다시 확인하세요")
                                    .setNegativeButton("다시 시도", null)
                                    .create();
                            dialog.show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseLister);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);


            return true;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if (progressDialog != null)
                progressDialog.dismiss();
        }
    }

}

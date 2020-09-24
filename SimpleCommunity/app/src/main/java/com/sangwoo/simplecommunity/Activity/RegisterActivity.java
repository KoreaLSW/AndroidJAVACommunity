package com.sangwoo.simplecommunity.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.R;
import com.sangwoo.simplecommunity.Request.RegisterCheckRequest;
import com.sangwoo.simplecommunity.Request.RegisterRequest;
import com.sangwoo.simplecommunity.Request.ValidateRequest;

import org.json.JSONObject;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_RESULT = 1;
    String deviceId;
    String Delete_confirm = "1";

    private AlertDialog dialog;
    private boolean validate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 핸드폰 고유번호 받기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                final TelephonyManager tm = (TelephonyManager) RegisterActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
                final String tmDevice, tmSerial, androidId;
                tmDevice = "" + tm.getDeviceId();
                tmSerial = "" + tm.getSimSerialNumber();
                androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
                UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
                deviceId = deviceUuid.toString();
                Log.d("테그확인", deviceId);
            } else {

            }
        }

        final EditText idText = findViewById(R.id.idText);
        final EditText passwordText = findViewById(R.id.passwordText);
        final EditText repasswordText = findViewById(R.id.repasswordText);
        final EditText emailText = findViewById(R.id.emailText);

        // 아이디 중복 체크 검사
        final Button validateButton = findViewById(R.id.validateButton);
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idText.getText().toString();
                if (validate) {
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                                idText.setEnabled(false);
                                validate = true;
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                return;
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });


        // 회원가입 버튼
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = idText.getText().toString();
                final String userPassword = passwordText.getText().toString();
                final String reuserPassword = repasswordText.getText().toString();
                final String userEmail = emailText.getText().toString();

                // 중복 체크를 했는지 확인
                if (!validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("먼저 중복 체크를 해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 빈칸 체크
                if (userID.equals("") || userPassword.equals("") || reuserPassword.equals("") || userEmail.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력해주세요.")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                // 비밀번호 체크
                if(!userPassword.equals(reuserPassword)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("비밀번호를 확인해 주세요")
                            .setNegativeButton("확인", null)
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListenerCheck = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean ok = jsonResponse.getBoolean("ok");
                            if (ok) {
                                // 고유번호를 체크한 후 검색되지 않았을때 회원등록을 진행한다.
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");
                                            if (success) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                dialog = builder.setMessage("회원 등록에 성공했습니다.")
                                                        .setPositiveButton("확인", null)
                                                        .create();
                                                dialog.show();
                                                finish();
                                                return;
                                            } else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                                dialog = builder.setMessage("회원 등록에 실패했습니다.")
                                                        .setNegativeButton("확인", null)
                                                        .create();
                                                dialog.show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userEmail, deviceId, Delete_confirm, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                                queue.add(registerRequest);
                                return;

                            } else {
                                // 기기의 고유번호를 체크한 후 고유번호가 있을때 회원가입 불가!!
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("회원가입은 한 기기당 한계정만 가능합니다.")
                                        .setNegativeButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                // 휴대폰 고유번호 체크
                RegisterCheckRequest registerRequest = new RegisterCheckRequest(deviceId, responseListenerCheck);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }


        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}

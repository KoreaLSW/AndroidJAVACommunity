package com.sangwoo.simplecommunity.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.sangwoo.simplecommunity.MainActivity;
import com.sangwoo.simplecommunity.R;
import com.sangwoo.simplecommunity.Request.PasswordChangeRequest;
import com.sangwoo.simplecommunity.Request.WithdrawalRequest;

import org.json.JSONObject;

public class PrivacyActivity extends AppCompatActivity {

    TextView userID;
    Button Passwordchange, Withdrawal;

    String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        init();
        userID.setText(LoginActivity.userID);

        Passwordchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText et = new EditText(PrivacyActivity.this);
                final EditText et2 = new EditText(PrivacyActivity.this);
                LinearLayout container = new LinearLayout(PrivacyActivity.this);
                container.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

                et.setLayoutParams(params);
                et2.setLayoutParams(params);

                et.setHint("비밀번호를 입력해 주세요");
                et2.setHint("비밀번호를 다시 입력해 주세요");

                et.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);
                et2.setInputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD);

                container.addView(et);
                container.addView(et2);

                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(PrivacyActivity.this, R.style.MyAlertDialogStyle);

                alt_bld.setTitle("비밀번호 변경").setMessage("변경할 비밀번호를 입력해 주세요.").setIcon(R.drawable.ic_search_black_64dp).setCancelable(

                        false).setView(container).setPositiveButton("확인",

                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                if (et.getText().toString().equals(et2.getText().toString())) {
                                    userPassword = String.valueOf(et.getText());
                                    Response.Listener<String> responseLister = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean success = jsonResponse.getBoolean("success");
                                                if (success) {
                                                    Toast.makeText(PrivacyActivity.this, "비밀번호 변경 완료 다시 로그인 해주세요!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(PrivacyActivity.this, "비밀번호 변경 실패", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest(LoginActivity.userID, userPassword, responseLister);
                                    RequestQueue queue = Volley.newRequestQueue(PrivacyActivity.this);
                                    queue.add(passwordChangeRequest);

                                    MainActivity mainActivity = (MainActivity) MainActivity.activity;
                                    mainActivity.finish();

                                    Intent intent = new Intent(PrivacyActivity.this, LoginActivity.class);
                                    intent.putExtra("Toast", "비밀번호 변경 완료 다시 로그인 해주세요!");
                                    startActivity(intent);

                                    finish();
                                } else {
                                    Toast.makeText(PrivacyActivity.this, "비밀번호가 다릅니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });

        Withdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout container = new FrameLayout(PrivacyActivity.this);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

                params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);

                final AlertDialog.Builder alt_bld = new AlertDialog.Builder(PrivacyActivity.this, R.style.MyAlertDialogStyle);

                alt_bld.setTitle("회원 탈퇴").setMessage("정말로 회원탈퇴를 하시겠습니까?").setIcon(R.drawable.ic_search_black_64dp).setCancelable(

                        false).setView(container).setPositiveButton("확인",

                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                Response.Listener<String> responseLister = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");
                                            if (success) {
                                                Toast.makeText(PrivacyActivity.this, "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(PrivacyActivity.this, "회원탈퇴 실패", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                WithdrawalRequest withdrawalRequest = new WithdrawalRequest(LoginActivity.userID, responseLister);
                                RequestQueue queue = Volley.newRequestQueue(PrivacyActivity.this);
                                queue.add(withdrawalRequest);

                                MainActivity mainActivity = (MainActivity) MainActivity.activity;
                                mainActivity.finish();

                                Intent intent = new Intent(PrivacyActivity.this, LoginActivity.class);
                                intent.putExtra("Toast", "회원 탈퇴 되었습니다");
                                startActivity(intent);

                                finish();
                            }
                        });

                AlertDialog alert = alt_bld.create();
                alert.show();
            }
        });
    }

    private void init() {
        userID = findViewById(R.id.idText);
        Passwordchange = findViewById(R.id.Passwordchange);
        Withdrawal = findViewById(R.id.Withdrawal);
    }

}

package com.example.group.classhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;

public class NewPasswordActivity extends AppCompatActivity {
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context myContext;
    private Button new_pwd_button;
    private EditText old_password;
    private EditText real_new_password;
    private EditText re_new;
    private String passwordMD5;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println(msg.obj.toString());

            switch (msg.what) {
                case HttpPut.PUT_SUCC:
                    String srcData = "{\"username\":\"" + LogInActivity.usernameString + "\","
                            + "\"password\":\"" + passwordMD5 + "\"}";
                    new HttpPost(srcData.getBytes(), "http://101.200.163.140:3000/api/customers/login",
                            handler, HttpPost.TYPE_LOGIN);
                    break;

                case HttpPut.PUT_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;

                case HttpPost.POST_SUCC:
                    try {
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        LogInActivity.userId = jsonInfo.getString("userId");
                        LogInActivity.access_token = jsonInfo.getString("id");
                        Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_LONG).show();
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("NewPasswordActivity: JSON WRONG!!");
                        alert = null;
                        builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                        alert = builder.setMessage("服务器数据异常")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();
                        alert.show();
                    }
                    break;

                case HttpPost.POST_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        myContext = NewPasswordActivity.this;

        new_pwd_button = (Button) findViewById(R.id.new_pwd_button);
        old_password = (EditText) findViewById(R.id.old_password);
        real_new_password = (EditText) findViewById(R.id.real_new_password);
        re_new = (EditText) findViewById(R.id.re_new);

        old_password.setText("");
        real_new_password.setText("");
        re_new.setText("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert = null;
                builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                alert = builder.setMessage("是否放弃更改密码？")
                        .setNegativeButton("放弃更改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("继续更改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                alert.show();
            }
        });

        new_pwd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (old_password.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写原密码！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (!old_password.getText().toString().equals(LogInActivity.passwordString)) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("原密码错误！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (real_new_password.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写新密码！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (re_new.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("请重复输入新密码！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (!re_new.getText().toString().equals(real_new_password.getText().toString())) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("两次输入的新密码不一致！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }

                try {
                    passwordMD5 = MD5.getMD5(real_new_password.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("加密算法缺失")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }

                alert = null;
                builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                alert = builder.setMessage("您确定要修改密码吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String srcData = "{\"username\":\"";
                                srcData += LogInActivity.usernameString;
                                srcData += "\",\"email\":\"";
                                srcData += LogInActivity.usernameString + "@pku.edu.cn";
                                srcData += "\",\"password\":\"";
                                srcData += passwordMD5;
                                srcData += "\"}";

                                // System.out.println("POST");
                                // System.out.println(srcData);

                                new HttpPut(srcData.getBytes(), "http://101.200.163.140:3000/api/customers/" +
                                        LogInActivity.userId +
                                        "?access_token=" + LogInActivity.access_token,
                                        handler, HttpPut.TYPE_NEWPASSWORD);
                            }
                        })
                        .setNegativeButton("取消修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                        .create();
                alert.show();
            }
        });
    }
}

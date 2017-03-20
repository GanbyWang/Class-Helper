package com.example.group.classhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LogInActivity extends AppCompatActivity {
    private Button login_button;
    private EditText user_name;
    private EditText password;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context myContext;
    public static int priority = 1;
    public static String userId = null;
    public static String access_token = null;
    public static String usernameString = null;
    public static String passwordString = null;

    Intent intent = new Intent();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println(msg.obj.toString());

            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    try {
                        JSONObject jsonInfo = new JSONObject(msg.obj.toString());
                        userId = jsonInfo.getString("userId");
                        access_token = jsonInfo.getString("id");
                        new HttpGet("http://101.200.163.140:3000/api/customers?access_token="+access_token,
                                handler, HttpGet.TYPE_INFO, null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("InfoFragment: JSON WRONG!!");
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
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    String msgString;
                    if (msg.arg1 == 401) {
                        msgString = "用户名或密码错误";
                    } else {
                        msgString = "请检查网络连接";
                    }
                    alert = builder.setMessage(msgString)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    break;

                case HttpGet.GET_SUCC:
                    priority = 1;
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                    intent.setClass(LogInActivity.this, RealMainActivity.class);
                    startActivity(intent);
                    LogInActivity.this.finish();
                    break;

                case HttpGet.GET_FAIL:
                    priority = 0;
                    Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
                    intent.setClass(LogInActivity.this, RealMainActivity.class);
                    startActivity(intent);
                    LogInActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        myContext = LogInActivity.this;

        login_button = (Button) findViewById(R.id.login_button);
        user_name = (EditText) findViewById(R.id.user_name);
        password = (EditText) findViewById(R.id.password);

        user_name.setText("");
        password.setText("");

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_name.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("请输入用户名")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (password.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("请输入密码")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }

                try {
                    usernameString = user_name.getText().toString();
                    passwordString = password.getText().toString();
                    String passwordMD5 = MD5.getMD5(passwordString);
                    // System.out.println(passwordMD5);
                    String srcData = "{\"username\":\"" + usernameString + "\","
                            + "\"password\":\"" + passwordMD5 + "\"}";
                    new HttpPost(srcData.getBytes(), "http://101.200.163.140:3000/api/customers/login",
                            handler, HttpPost.TYPE_LOGIN);
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
                }
            }
        });
    }
}

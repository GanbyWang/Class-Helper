package com.example.group.classhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PostInfoActivity extends AppCompatActivity {
    private Button upload_info;
    private EditText post_info_name;
    private EditText post_info_title;
    private EditText post_info_content;
    private RadioGroup post_info_type;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context myContext;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("PostInfoActivity: " + msg.obj.toString());

            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    Toast.makeText(getApplicationContext(), "发布成功", Toast.LENGTH_LONG).show();
                    finish();
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
        setContentView(R.layout.activity_post_info);

        myContext = PostInfoActivity.this;

        upload_info = (Button) findViewById(R.id.upload_info);
        post_info_name = (EditText) findViewById(R.id.post_info_name);
        post_info_title = (EditText) findViewById(R.id.post_info_title);
        post_info_content = (EditText) findViewById(R.id.post_info_content);
        post_info_type = (RadioGroup) findViewById(R.id.post_info_type);

        post_info_name.setText("");
        post_info_title.setText("");
        post_info_content.setText("");
        post_info_type.clearCheck();

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert = null;
                builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                alert = builder.setMessage("是否放弃此次发布？")
                        .setNegativeButton("放弃发布", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setPositiveButton("继续发布", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                alert.show();
            }
        });

        upload_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_info_name.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写姓名！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (post_info_title.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写通知标题！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (post_info_content.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写通知正文！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (post_info_type.getCheckedRadioButtonId() == -1) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未选择通知类型！")
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
                alert = builder.setMessage("您确定要发布本条通知吗！")
                        .setCancelable(false)
                        .setPositiveButton("确定发布", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String srcData = "{\"head\":\"";
                                srcData += post_info_title.getText().toString();
                                srcData += "\",\"body\":\"";
                                srcData += post_info_content.getText().toString();
                                srcData += "\",\"type\":\"";
                                switch (post_info_type.getCheckedRadioButtonId()) {
                                    case R.id.normal:
                                        srcData += "normal";
                                        break;
                                    case R.id.study:
                                        srcData += "study";
                                        break;
                                    case R.id.publicity:
                                        srcData += "publicity";
                                        break;
                                }
                                srcData += "\",\"date\":\"";
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                                srcData += df.format(new Date(System.currentTimeMillis()));
                                srcData += "\",\"author\":\"";
                                srcData += LogInActivity.usernameString;
                                srcData += "\"}";

                                // System.out.println("POST");
                                // System.out.println(srcData);

                                new HttpPost(srcData.getBytes(), "http://101.200.163.140:3000/api/customers/" + LogInActivity.userId +
                                        "/messages?access_token=" + LogInActivity.access_token,
                                        handler, HttpPost.TYPE_INFO);
                            }
                        })
                        .setNegativeButton("取消发布", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create();
                alert.show();
            }
        });
    }
}
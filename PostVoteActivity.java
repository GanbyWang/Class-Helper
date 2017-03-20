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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PostVoteActivity extends AppCompatActivity {
    private WebView vote_web;
    private Button upload_vote;
    private EditText post_vote_name;
    private EditText post_vote_title;
    private EditText post_vote_content;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context myContext;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println(msg.obj.toString());

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
        setContentView(R.layout.activity_post_vote);

        myContext = PostVoteActivity.this;

        upload_vote = (Button) findViewById(R.id.upload_vote);
        post_vote_name = (EditText) findViewById(R.id.post_vote_name);
        post_vote_title = (EditText) findViewById(R.id.post_vote_title);
        post_vote_content = (EditText) findViewById(R.id.post_vote_content);

        post_vote_name.setText("");
        post_vote_title.setText("");
        post_vote_content.setText("");

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

        vote_web = (WebView) findViewById(R.id.vote_web);
        vote_web.getSettings().setJavaScriptEnabled(true);
        vote_web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        vote_web.loadUrl("http://www.sojump.com/");

        upload_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_vote_name.getText().toString().equals("")) {
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
                if (post_vote_title.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写投票标题！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (post_vote_content.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写投票链接！")
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
                alert = builder.setMessage("您确定要发布此次投票吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定发布", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String srcData = "{\"head\":\"";
                                srcData += post_vote_title.getText().toString();
                                srcData += "\",\"link\":\"";
                                srcData += post_vote_content.getText().toString();
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
                                        "/votes?access_token=" + LogInActivity.access_token,
                                        handler, HttpPost.TYPE_VOTE);
                            }
                        })
                        .setNegativeButton("取消发布", new DialogInterface.OnClickListener() {
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
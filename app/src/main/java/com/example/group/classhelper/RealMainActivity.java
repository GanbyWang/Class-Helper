package com.example.group.classhelper;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RealMainActivity extends AppCompatActivity {
    private Fragment infoFragment, cloudFragment, voteFragment, accountFragment;

    private RadioButton infoButton;
    private RadioButton cloudButton;
    private RadioButton voteButton;
    private RadioButton accountButton;

    private FragmentManager fragmentManager;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;

    private int i = 0;
    static Uri uri = null;
    static String title = null;

    DownloadManager downloadManager;
    Map<Long, String> downloadFileMap = new HashMap<>();
    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    // static final int MY_PERMISSIONS_REQUEST_NETWORK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_main);

        fragmentManager = getSupportFragmentManager();

        infoButton = (RadioButton) findViewById(R.id.real_info_button);
        cloudButton = (RadioButton) findViewById(R.id.real_cloud_button);
        voteButton = (RadioButton) findViewById(R.id.real_vote_button);
        accountButton = (RadioButton) findViewById(R.id.real_account_button);

        infoFragment = new InfoFragment();
        voteFragment = new VoteFragment();
        cloudFragment = new CloudFragment();
        accountFragment = new AccountFragment();

        changeFragment(infoFragment, null, "infoFragment");
        changeRadioButtonImage(R.id.real_info_button);

        infoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                infoFragment = new InfoFragment();
                changeFragment(infoFragment, null, "infoFragment");
                changeRadioButtonImage(v.getId());
            }
        });

        cloudButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cloudFragment = new CloudFragment();
                changeFragment(cloudFragment, null, "cloudFragment");
                changeRadioButtonImage(v.getId());
            }
        });

        voteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                voteFragment = new VoteFragment();
                changeFragment(voteFragment, null, "voteFragment");
                changeRadioButtonImage(v.getId());
            }
        });

        accountButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                accountFragment = new AccountFragment();
                changeFragment(accountFragment, null, "accountFragment");
                changeRadioButtonImage(v.getId());
            }
        });

        // Prepare for download request
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

        // Prepare for download-finish signal
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (!downloadFileMap.containsKey(reference)) {
                    return;
                }
                String title = downloadFileMap.get(reference);
                Toast.makeText(getApplicationContext(), title + "下载已完成", Toast.LENGTH_LONG).show();
                downloadFileMap.remove(reference);
            }
        };
        registerReceiver(receiver, filter);
    }

    public void changeFragment(Fragment fragment, Bundle bundle, String tag) {
        for (int i = 0, count = fragmentManager.getBackStackEntryCount(); i < count; i++) {
            fragmentManager.popBackStack();
        }
        FragmentTransaction fg = fragmentManager.beginTransaction();
        fragment.setArguments(bundle);
        fg.add(R.id.fragment_root, fragment, tag);
        fg.addToBackStack(tag);
        fg.commit();
    }

    public void changeRadioButtonImage(int btids) {
        int[] unact_image = {R.drawable.unactivated_info, R.drawable.unactivated_cloud, R.drawable.unactivated_vote, R.drawable.unactivated_account};
        int[] act_image = {R.drawable.activated_info, R.drawable.activated_cloud, R.drawable.activated_vote, R.drawable.activated_account};
        int[] button = {R.id.real_info_button, R.id.real_cloud_button, R.id.real_vote_button, R.id.real_account_button};
        int[] parent = {R.id.info_tab, R.id.cloud_tab, R.id.vote_tab, R.id.account_tab};

        switch (btids) {
            case R.id.real_info_button:
                changeImage(unact_image, act_image, button, 0, parent);
                break;
            case R.id.real_cloud_button:
                changeImage(unact_image, act_image, button, 1, parent);
                break;
            case R.id.real_vote_button:
                changeImage(unact_image, act_image, button, 2, parent);
                break;
            case R.id.real_account_button:
                changeImage(unact_image, act_image, button, 3, parent);
                break;
        }
    }

    public void changeImage(int[] image1, int[] image2, int[] rabtid, int index, int[] parent) {
        for (int i = 0; i < image1.length; i++) {
            if (i != index) {
                ((RadioButton) findViewById(rabtid[i]))
                        .setCompoundDrawablesWithIntrinsicBounds(0, image1[i],
                                0, 0);
                ((RadioButton) findViewById(rabtid[i]))
                        .setTextColor(Color.rgb(3, 101, 100));
                ((LinearLayout) findViewById(parent[i])).setBackgroundColor(Color.rgb(225, 233, 220));
            } else {
                ((RadioButton) findViewById(rabtid[i]))
                        .setCompoundDrawablesWithIntrinsicBounds(0, image2[i],
                                0, 0);
                ((RadioButton) findViewById(rabtid[i]))
                        .setTextColor(Color.rgb(255, 255, 255));
                ((LinearLayout) findViewById(parent[i])).setBackgroundColor(Color.rgb(3, 101, 100));
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    long reference = downloadManager.enqueue(request);
                    downloadFileMap.put(reference, title);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(this);
                    alert = builder.setMessage("请提供访问权限，否则无法下载文件！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                }
                break;
            }
        }
    }

    public void getWritePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            long reference = downloadManager.enqueue(request);
            downloadFileMap.put(reference, title);
        }
    }
}

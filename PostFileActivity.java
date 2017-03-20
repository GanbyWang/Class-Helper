package com.example.group.classhelper;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PostFileActivity extends AppCompatActivity {
    private ImageButton file_button;
    private TextView route;
    private Button upload_button;
    private EditText post_file_name;
    private EditText post_file_title;
    private RadioGroup post_file_type;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Context myContext;
    private Uri postUri;

    static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println(msg.obj.toString());

            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_LONG).show();
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
        setContentView(R.layout.activity_post_file);

        myContext = PostFileActivity.this;

        post_file_name = (EditText) findViewById(R.id.post_file_name);
        post_file_title = (EditText) findViewById(R.id.post_file_title);
        post_file_type = (RadioGroup) findViewById(R.id.post_file_type);

        post_file_name.setText("");
        post_file_title.setText("");
        post_file_type.clearCheck();

        file_button = (ImageButton) findViewById(R.id.file_button);
        route = (TextView) findViewById(R.id.route);
        upload_button = (Button) findViewById(R.id.upload_button);
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

        file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    startActivityForResult(intent, 1);
                } catch (android.content.ActivityNotFoundException ex) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未安装文件管理应用！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                }
            }
        });


        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_file_name.getText().toString().equals("")) {
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
                if (post_file_title.getText().toString().equals("")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未填写文件名！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (post_file_title.getText().toString().contains(" ")) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("文件名不能含有空格！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
                if (post_file_type.getCheckedRadioButtonId() == -1) {
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未选择文件类型！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }

                try {
                    File file = new File(route.getText().toString());
                    if (file == null) {
                        alert = null;
                        builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                        alert = builder.setMessage("您未选择上传的文件！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();
                        alert.show();
                        return;
                    }

                    // Get InputStream
                    FileInputStream inputStream = new FileInputStream(file);
                    // Switch to BufferedReader
                    // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    // Store feedback in resultData
                    byte[] buf = new byte[(int) file.length()];
                    inputStream.read(buf);
                    inputStream.close();
                    // System.out.println("file: " + (int) file.length());
                    // System.out.println("buff: " + buf.length);

                    // Get file suffix
                    int lastIndex = route.getText().toString().lastIndexOf(".");
                    String suffix = route.getText().toString().substring(lastIndex);
                    // Get date
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_");
                    df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    // Get type
                    String type = "";
                    switch (post_file_type.getCheckedRadioButtonId()) {
                        case R.id.study:
                            type = "Study";
                            break;
                        case R.id.others:
                            type = "Other";
                            break;
                    }

                    String urlTitle = post_file_title.getText().toString();
                    byte[] former = ("--WebKitFormBoundary7MA4YWxkTrZu0gW\r\n" +
                            "Content-Disposition: form-data; name=\"vfsdfdsf\"; " +
                            "filename=\"" +
                            df.format(new Date(System.currentTimeMillis())) +
                            type + "_" +
                            LogInActivity.usernameString + "_" +
                            urlTitle + suffix + "\"\r\n" +
                            "Content-Type: application/msword\r\n\r\n")
                            .getBytes();
                    byte[] latter = "\r\n--WebKitFormBoundary7MA4YWxkTrZu0gW--".getBytes();
                    final byte[] srcData = new byte[former.length + buf.length + latter.length];
                    System.arraycopy(former, 0, srcData, 0, former.length);
                    System.arraycopy(buf, 0, srcData, former.length, buf.length);
                    System.arraycopy(latter, 0, srcData, former.length + buf.length, latter.length);

                    final byte[] final_srcData = srcData;
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您确定要上传该文件吗？")
                            .setCancelable(false)
                            .setPositiveButton("确定上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new HttpPost(final_srcData, "http://101.200.163.140:3000/api/containers/container1/upload" +
                                            "?access_token=" + LogInActivity.access_token,
                                            handler, HttpPost.TYPE_FILE);
                                }
                            })
                            .setNegativeButton("取消上传", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).create();
                    alert.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("您未选择上传的文件！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                    return;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            postUri = data.getData();
            getPermission();
        }
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    String path = getPath(myContext, postUri);
                    route.setText(path);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alert = null;
                    builder = new android.support.v7.app.AlertDialog.Builder(myContext);
                    alert = builder.setMessage("请提供访问权限，否则无法上传文件！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).create();
                    alert.show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            String path = getPath(myContext, postUri);
            route.setText(path);
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}

package com.example.group.classhelper;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

class HttpPost {
    // Set result
    static final int POST_SUCC = 2;
    static final int POST_FAIL = 3;

    // Set type
    static final int TYPE_INFO = 0;
    static final int TYPE_VOTE = 1;
    static final int TYPE_FILE = 2;
    static final int TYPE_LOGIN = 3;
    static final int TYPE_LOGOUT = 4;

    // Set timeout
    private final int READ_TIMEOUT = 3000;
    private final int CONNECT_TIMEOUT = 3000;
    private final int FILE_READ_TIMEOUT = 100000;
    private final int FILE_CONNECT_TIMEOUT = 100000;

    HttpPost(final byte[] srcData, final String urlString, final Handler handler, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Not a file
                    if (type != TYPE_FILE) {
                        // URL
                        URL url = new URL(urlString);
                        // Create HttpURLConnection
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        // Set Request Method
                        connection.setRequestMethod("POST");
                        // Set Connect Timeout
                        connection.setConnectTimeout(CONNECT_TIMEOUT);
                        // Set Read Timeout
                        connection.setReadTimeout(READ_TIMEOUT);
                        // Set I/O options
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setUseCaches(false);
                        // Set RequestProperty
                        // connection.setRequestProperty("Connection", "Keep-Alive");
                        connection.setRequestProperty("Charset", "UTF-8");
                        connection.setRequestProperty("Content-Type", "application/json");

                        // Get OutputStream
                        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                        // Write to OutputStream
                        outputStream.write(srcData);
                        outputStream.flush();
                        outputStream.close();

                        // System.out.println(connection.getResponseCode());
                        // Http success
                        if (connection.getResponseCode() >= HttpURLConnection.HTTP_OK &&
                                connection.getResponseCode() < 300) {
                            // Get InputStream
                            InputStream inputStream = connection.getInputStream();
                            // Switch to
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            // Store feedback from the Net
                            String line;
                            String resultData = "";
                            while (((line = bufferedReader.readLine()) != null)) {
                                resultData += line;
                            }
                            // Close stream
                            inputStream.close();
                            // Close connection
                            connection.disconnect();

                            // Set Message (return to handler)
                            Message mg = Message.obtain();
                            mg.what = POST_SUCC;
                            mg.obj = resultData;
                            handler.sendMessage(mg);
                        }
                        // Http not success
                        else {
                            // Get InputStream
                            InputStream inputStream = connection.getErrorStream();
                            // Switch to
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                            // Store feedback from the Net
                            String line;
                            String resultData = "";
                            while (((line = bufferedReader.readLine()) != null)) {
                                resultData += line;
                            }
                            // Close stream
                            inputStream.close();

                            // Set Message (return to handler)
                            Message mg = Message.obtain();
                            mg.what = POST_FAIL;
                            mg.obj = resultData;
                            mg.arg1 = connection.getResponseCode();
                            // Close connection
                            connection.disconnect();
                            handler.sendMessage(mg);
                        }
                    }
                    // Is a file
                    else {
                        OkHttpClient client = new OkHttpClient.Builder()
                                .connectTimeout(FILE_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                                .readTimeout(FILE_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                                .build();

                        MediaType mediaType = MediaType.parse("multipart/form-data; boundary=WebKitFormBoundary7MA4YWxkTrZu0gW");
                        RequestBody body = RequestBody.create(mediaType, srcData);
                        Request request = new Request.Builder()
                                .url(urlString)
                                .post(body)
                                .addHeader("content-type", "multipart/form-data; boundary=WebKitFormBoundary7MA4YWxkTrZu0gW")
                                .addHeader("cache-control", "no-cache")
                                .build();
                        Response response = client.newCall(request).execute();

                        if (response.isSuccessful()) {
                            // Set Message (return to handler)
                            Message mg = Message.obtain();
                            mg.what = POST_SUCC;
                            mg.obj = response.body();
                            handler.sendMessage(mg);
                        } else {
                            // Set Message (return to handler)
                            Message mg = Message.obtain();
                            mg.what = POST_FAIL;
                            mg.obj = response.body();
                            handler.sendMessage(mg);
                        }
                    }
                } catch (Exception e) {
                    // Set Message (return to handler)
                    Message mg = Message.obtain();
                    mg.what = POST_FAIL;
                    mg.obj = "请检查网络连接";
                    mg.arg1 = 0;
                    handler.sendMessage(mg);
                    // Debug
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

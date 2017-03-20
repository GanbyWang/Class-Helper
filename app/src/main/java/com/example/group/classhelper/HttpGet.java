package com.example.group.classhelper;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpGet {
    // Set result
    public static final int GET_SUCC = 0;
    public static final int GET_FAIL = 1;

    // Set type
    static final int TYPE_INFO = 0;
    static final int TYPE_VOTE = 1;
    static final int TYPE_FILE = 2;

    // Set timeout
    final int READ_TIMEOUT = 3000;
    final int CONNECT_TIMEOUT = 3000;

    public HttpGet(final String urlString, final Handler handler, final int type, final String param) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL
                    URL url = new URL(urlString);
                    // Create HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // Set Request Method
                    connection.setRequestMethod("GET");
                    // Set Connect Timeout
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    // Set Read Timeout
                    connection.setReadTimeout(READ_TIMEOUT);
                    // Set I/O options
                    // connection.setDoInput(true);
                    // connection.setDoOutput(true);
                    // connection.setUseCaches(false);
                    // Set Request Property
                    // connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Charset", "UTF-8");
                    connection.setRequestProperty("Content-Type", "application/json");

                    // System.out.println(connection.getResponseCode());
                    // Http Success
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
                        mg.what = GET_SUCC;
                        mg.obj = resultData;
                        mg.arg1 = type;
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
                        // Close connection
                        connection.disconnect();

                        // Set Message (return to handler)
                        Message mg = Message.obtain();
                        mg.what = GET_FAIL;
                        mg.obj = resultData;
                        mg.arg1 = type;
                        handler.sendMessage(mg);
                    }
                } catch (Exception e) {
                    // Set Message (return to handler)
                    Message mg = Message.obtain();
                    mg.what = GET_FAIL;
                    mg.obj = "请检查网络连接";
                    mg.arg1 = type;
                    handler.sendMessage(mg);
                    // Debug
                    e.printStackTrace();
                }
            }
        }).start();
    }
}


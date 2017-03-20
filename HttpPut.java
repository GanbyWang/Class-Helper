package com.example.group.classhelper;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class HttpPut {
    // Set result
    static final int PUT_SUCC = 4;
    static final int PUT_FAIL = 5;

    // Set type
    static final int TYPE_NEWPASSWORD = 0;

    // Set timeout
    private final int READ_TIMEOUT = 3000;
    private final int CONNECT_TIMEOUT = 3000;

    HttpPut(final byte[] srcData, final String urlString, final Handler handler, final int type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // URL
                    URL url = new URL(urlString);
                    // Create HttpURLConnection
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // Set Request Method
                    connection.setRequestMethod("PUT");
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
                        mg.what = PUT_SUCC;
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
                        // Close connection
                        connection.disconnect();

                        // Set Message (return to handler)
                        Message mg = Message.obtain();
                        mg.what = PUT_FAIL;
                        mg.obj = resultData;
                        handler.sendMessage(mg);
                    }

                } catch (Exception e) {
                    // Set Message (return to handler)
                    Message mg = Message.obtain();
                    mg.what = PUT_FAIL;
                    mg.obj = "请检查网络连接";
                    handler.sendMessage(mg);
                    // Debug
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

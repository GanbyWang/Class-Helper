package com.example.group.classhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyInfo extends AppCompatActivity {
    private ListView info_list;
    private List<Map<String, Object>> myData = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("MyInfo: " + msg.obj.toString());

            switch (msg.what) {
                case HttpGet.GET_SUCC:
                    try {
                        // Get jsons
                        JSONArray jsons = new JSONArray(msg.obj.toString());

                        // Get infolist
                        myData.clear();
                        for (int i = 0; i < jsons.length(); i++) {
                            JSONObject jsonInfo = jsons.getJSONObject(i);
                            Map<String, Object> map = new HashMap<>();
                            map.put("title", jsonInfo.getString("head"));
                            map.put("time", jsonInfo.getString("date"));
                            map.put("body", jsonInfo.getString("body"));
                            map.put("id", jsonInfo.getString("id"));
                            map.put("type", jsonInfo.getString("type"));
                            map.put("author", jsonInfo.getString("author"));
                            myData.add(map);
                        }
                        info_list = (ListView) findViewById(R.id.info_list);
                        MyAdapter adapter = new MyAdapter(MyInfo.this);
                        info_list.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("MyInfo: JSON WRONG!!");
                        Toast.makeText(getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpGet.GET_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;

                case HttpDelete.DELETE_SUCC:
                    Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_LONG).show();
                    new HttpGet("http://101.200.163.140:3000/api/customers/" + LogInActivity.userId +
                            "/messages?access_token=" + LogInActivity.access_token +
                            "&filter[order]=date%20DESC",
                            handler, HttpGet.TYPE_INFO, null);
                    break;

                case HttpDelete.DELETE_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        new HttpGet("http://101.200.163.140:3000/api/customers/" + LogInActivity.userId +
                "/messages?access_token=" + LogInActivity.access_token +
                "&filter[order]=date%20DESC",
                handler, HttpGet.TYPE_INFO, null);
    }

    public final class ViewHolder {
        public TextView title;
        public TextView time;
        public ImageButton link;
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater myInflater;

        public MyAdapter(Context context) {
            this.myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return myData.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = myInflater.inflate(R.layout.deletelist_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.link = (ImageButton) convertView.findViewById(R.id.link);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText((String) myData.get(position).get("title"));
            holder.time.setText((String) myData.get(position).get("time"));
            holder.link.setTag(position);

            holder.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteInfo(position);
                }
            });

            return convertView;
        }

        public void deleteInfo(int position) {
            new HttpDelete("http://101.200.163.140:3000/api/customers/" + LogInActivity.userId +
                    "/messages/" + myData.get(position).get("id").toString() +
                    "?access_token=" + LogInActivity.access_token,
                    handler, HttpDelete.TYPE_INFO, null);
        }
    }
}

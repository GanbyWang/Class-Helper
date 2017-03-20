package com.example.group.classhelper;

import android.content.Context;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MyFile extends AppCompatActivity {
    private ListView study_file_list;
    private ListView other_file_list;
    private List<String> studyFile = new ArrayList<>();
    private List<String> otherFile = new ArrayList<>();
    studyFileAdapter studyAdapter = null;
    otherFileAdapter otherAdapter = null;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("MyFile: " + msg.obj.toString());

            switch (msg.what) {
                case HttpGet.GET_SUCC:
                    try {
                        // Get jsons
                        JSONArray jsons = new JSONArray(msg.obj.toString());

                        // Get infolist
                        studyFile.clear();
                        otherFile.clear();
                        for (int i = 0; i < jsons.length(); i++) {
                            JSONObject jsonInfo = jsons.getJSONObject(i);
                            String title = jsonInfo.getString("name");
                            // Not belong to this user
                            if (title.indexOf(LogInActivity.usernameString) != 26) {
                                continue;
                            }
                            // Study file
                            if (title.indexOf("Study") == 20) {
                                studyFile.add(title);
                            }
                            // Other file
                            else if (title.indexOf("Other") == 20) {
                                otherFile.add(title);
                            }
                        }
                        study_file_list = (ListView) findViewById(R.id.study_file_list);
                        other_file_list = (ListView) findViewById(R.id.other_file_list);
                        studyAdapter = new studyFileAdapter(MyFile.this);
                        otherAdapter = new otherFileAdapter(MyFile.this);
                        study_file_list.setAdapter(studyAdapter);
                        other_file_list.setAdapter(otherAdapter);
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
                    new HttpGet("http://101.200.163.140:3000/api/containers/container1/files" +
                            "?access_token=" + LogInActivity.access_token,
                            handler, HttpGet.TYPE_FILE, null);
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
        setContentView(R.layout.activity_my_file);

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        new HttpGet("http://101.200.163.140:3000/api/containers/container1/files" +
                "?access_token=" + LogInActivity.access_token,
                handler, HttpGet.TYPE_FILE, null);
    }

    public final class ViewHolder {
        public TextView title;
        public TextView time;
        public ImageButton link;
    }

    public class studyFileAdapter extends BaseAdapter {
        private LayoutInflater myInflater;

        public studyFileAdapter(Context context) {
            this.myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return studyFile.size();
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
                holder.link = (ImageButton) convertView.findViewById(R.id.link);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (studyFile.get(position).length() > 26) {
                int index = studyFile.get(position).indexOf("_", 26);
                holder.title.setText(studyFile.get(position).substring(index + 1));
            } else {
                holder.title.setText(studyFile.get(position));
            }
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
            String fileName = studyFile.get(position);
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (Exception e) {
            }
            new HttpDelete("http://101.200.163.140:3000/api/containers/container1/files/" +
                    fileName +
                    "?access_token=" + LogInActivity.access_token,
                    handler, HttpDelete.TYPE_FILE, null);
        }
    }

    public class otherFileAdapter extends BaseAdapter {
        private LayoutInflater myInflater;

        public otherFileAdapter(Context context) {
            this.myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return otherFile.size();
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
                holder.link = (ImageButton) convertView.findViewById(R.id.link);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (otherFile.get(position).length() > 26) {
                int index = otherFile.get(position).indexOf("_", 26);
                holder.title.setText(otherFile.get(position).substring(index + 1));
            } else {
                holder.title.setText(otherFile.get(position));
            }
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
            String fileName = otherFile.get(position);
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (Exception e) {
            }
            new HttpDelete("http://101.200.163.140:3000/api/containers/container1/files/" +
                    fileName +
                    "?access_token=" + LogInActivity.access_token,
                    handler, HttpDelete.TYPE_FILE, null);
        }
    }
}

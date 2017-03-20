package com.example.group.classhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

import static android.R.color.white;

public class NormalInfoActivity extends AppCompatActivity {
    private ListView info_list;
    private List<Map<String, Object>> tmpData;
    private List<Map<String, Object>> myData;
    private Info[] infoList = new Info[10];
    private MyAdapter adapter = null;
    private boolean if_refresh = false;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("NormalInfoActivity: " + msg.obj.toString());

            switch (msg.what) {
                case HttpGet.GET_SUCC:
                    try {
                        JSONArray jsons = new JSONArray(msg.obj.toString());
                        for (int i = 0; i < 10; i++) {
                            if (i >= jsons.length()) {
                                infoList[i] = null;
                                break;
                            }
                            JSONObject jsonInfo = jsons.getJSONObject(i);
                            infoList[i] = new Info();
                            infoList[i].setId(jsonInfo.getString("id"));
                            infoList[i].setHead(jsonInfo.getString("head"));
                            infoList[i].setBody(jsonInfo.getString("body"));
                            infoList[i].setType(jsonInfo.getString("type"));
                            infoList[i].setDate(jsonInfo.getString("date"));
                            infoList[i].setAuthor(jsonInfo.getString("author"));
                        }
                        info_list = (ListView) findViewById(R.id.info_list);
                        tmpData = getData();
                        if (myData == null || if_refresh == true) {
                            myData = tmpData;
                        } else {
                            myData.addAll(tmpData);
                        }
                        if (adapter == null) {
                            adapter = new MyAdapter(NormalInfoActivity.this);
                            info_list.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        info_list.setOnScrollListener(new AbsListView.OnScrollListener() {
                            //当滑动状态发生改变的时候执行
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                switch (scrollState) {
                                    //当不滚动的时候
                                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                                        //判断是否是最底部
                                        if (view.getLastVisiblePosition() == (view.getCount()) - 1) {
                                            String last_time = (String) myData.get(myData.size() - 1).get("time");
                                            if_refresh = false;
                                            String oldUri="http://101.200.163.140:3000/api/messages" +
                                                    "?access_token=" + LogInActivity.access_token +
                                                    "&filter[where][type]=normal" +
                                                    "&filter[where][date][lt]=" + last_time +
                                                    "&filter[order]=date%20DESC" +
                                                    "&filter[limit]=10";
                                            String newUri="";
                                            for(int i=0; i<oldUri.length(); i++)
                                            {
                                                if(oldUri.charAt(i)!=' '){
                                                    newUri+=oldUri.charAt(i);
                                                }
                                                else{
                                                    newUri+="%20";
                                                }
                                            }
                                            new HttpGet(newUri,
                                                    handler, HttpGet.TYPE_INFO, null);
                                        }
                                        break;
                                }
                            }

                            //正在滑动的时候执行
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("NormalInfoActivity: JSON WRONG!!");
                        Toast.makeText(getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;
                case HttpGet.GET_FAIL:
                    Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        final SwipeRefreshLayout swipe_list = (SwipeRefreshLayout) findViewById(R.id.swipe_list);
        swipe_list.setColorSchemeResources(R.color.my_green, R.color.my_gray);
        swipe_list.setProgressBackgroundColor(white);
        swipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if_refresh = true;
                new HttpGet("http://101.200.163.140:3000/api/messages" +
                        "?access_token=" + LogInActivity.access_token +
                        "&filter[where][type]=normal" +
                        "&filter[order]=date%20DESC" +
                        "&filter[limit]=10",
                        handler, HttpGet.TYPE_INFO, null);
                if (swipe_list.isRefreshing()) {
                    swipe_list.setRefreshing(false);
                }
            }
        });

        new HttpGet("http://101.200.163.140:3000/api/messages" +
                "?access_token=" + LogInActivity.access_token +
                "&filter[where][type]=normal" +
                "&filter[order]=date%20DESC" +
                "&filter[limit]=10",
                handler, HttpGet.TYPE_INFO, null);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < 10; i++) {
            if (infoList[i] == null) break;
            map = new HashMap<String, Object>();
            map.put("title", infoList[i].getHead());
            map.put("time", infoList[i].getDate());
            map.put("body", infoList[i].getBody());
            map.put("id", infoList[i].getId());
            map.put("type", infoList[i].getType());
            map.put("author", infoList[i].getAuthor());

            list.add(map);
        }
        return list;
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
                convertView = myInflater.inflate(R.layout.listview_item, null);
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
                    showInfo(position);
                }
            });

            return convertView;
        }

        public void showInfo(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("title", (String) myData.get(position).get("title"));
            bundle.putString("author", (String) myData.get(position).get("author"));
            bundle.putString("time", (String) myData.get(position).get("time"));
            bundle.putString("content", (String) myData.get(position).get("body"));
            Intent intent = new Intent(NormalInfoActivity.this, DetailedInfoActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
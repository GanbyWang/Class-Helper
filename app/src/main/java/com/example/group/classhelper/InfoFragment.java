package com.example.group.classhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class InfoFragment extends Fragment {
    private DrawerLayout myDrawer;
    private ActionBarDrawerToggle myToggle;
    private Toolbar myToolbar;
    private Button post_button;
    public View view;
    private ListView info_list;
    private List<Map<String, Object>> allData = null;
    private List<Map<String, Object>> myData;
    private Info[] infoList = new Info[10];
    private MyAdapter adapter = null;
    private boolean if_refresh = true;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("InfoFragment: "+msg.obj.toString());

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
                        info_list = (ListView) view.findViewById(R.id.info_list);
                        myData = getData();
                        if (allData == null || if_refresh == true) {
                            allData = myData;
                        } else {
                            allData.addAll(myData);
                        }
                        if (adapter == null) {
                            adapter = new MyAdapter(getActivity());
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
                                            String last_time = (String) allData.get(allData.size() - 1).get("time");
                                            if_refresh = false;
                                            String oldUri="http://101.200.163.140:3000/api/messages" +
                                                    "?access_token=" + LogInActivity.access_token +
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
                        System.out.println("InfoFragment: JSON WRONG!!");
                        Toast.makeText(getActivity().getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;
                case HttpGet.GET_FAIL:
                    Toast.makeText(getActivity().getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public InfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "Android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_info, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initViews();

        final SwipeRefreshLayout swipe_list = (SwipeRefreshLayout) view.findViewById(R.id.swipe_list);
        swipe_list.setColorSchemeResources(R.color.my_green, R.color.my_gray);
        swipe_list.setProgressBackgroundColor(white);
        swipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if_refresh = true;
                new HttpGet("http://101.200.163.140:3000/api/messages" +
                        "?access_token=" + LogInActivity.access_token +
                        "&filter[order]=date%20DESC" +
                        "&filter[limit]=10",
                        handler, HttpGet.TYPE_INFO, null);
                if (swipe_list.isRefreshing()) {
                    swipe_list.setRefreshing(false);
                }
            }
        });

        post_button = (Button) view.findViewById(R.id.post_info_button);
        if (LogInActivity.priority == 1) {
            post_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostInfoActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            post_button.setVisibility(View.INVISIBLE);
        }

        TextView normal = (TextView) view.findViewById(R.id.normal);
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NormalInfoActivity.class);
                startActivity(intent);
            }
        });

        TextView study = (TextView) view.findViewById(R.id.study);
        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StudyInfoActivity.class);
                startActivity(intent);
            }
        });

        TextView pub = (TextView) view.findViewById(R.id.publication);
        pub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PubInfoActivity.class);
                startActivity(intent);
            }
        });

        new HttpGet("http://101.200.163.140:3000/api/messages" +
                "?access_token=" + LogInActivity.access_token +
                "&filter[order]=date%20DESC" +
                "&filter[limit]=10",
                handler, HttpGet.TYPE_INFO, null);
    }

    private void initViews() {
        myToolbar = (Toolbar) view.findViewById(R.id.header);
        myToolbar.setNavigationIcon(R.drawable.slide);
        ((AppCompatActivity) getActivity()).setSupportActionBar(myToolbar);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myDrawer = (DrawerLayout) getView().findViewById(R.id.drawer);
        myToggle = new ActionBarDrawerToggle(getActivity(), myDrawer, myToolbar, R.string.drawer_open, R.string.drawer_close);
        myToggle.syncState();
        myDrawer.setDrawerListener(myToggle);
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
        protected Context context;

        public MyAdapter(Context context) {
            this.context = context;
            this.myInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return allData.size();
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

            holder.title.setText((String) allData.get(position).get("title"));
            holder.time.setText((String) allData.get(position).get("time"));
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
            bundle.putString("title", (String) allData.get(position).get("title"));
            bundle.putString("author", (String) allData.get(position).get("author"));
            bundle.putString("time", (String) allData.get(position).get("time"));
            bundle.putString("content", (String) allData.get(position).get("body"));
            Intent intent = new Intent(getActivity(), DetailedInfoActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}

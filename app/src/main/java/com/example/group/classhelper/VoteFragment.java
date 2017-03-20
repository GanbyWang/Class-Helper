package com.example.group.classhelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class VoteFragment extends Fragment {
    public View view;
    private Button post_vote_button;
    private ListView info_list;
    private List<Map<String, Object>> tmpData;
    private List<Map<String, Object>> myData = null;
    private Vote[] voteList = new Vote[10];
    private MyAdapter adapter = null;
    private boolean if_refresh = true;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("VoteFragment: "+msg.obj.toString());

            switch (msg.what) {
                case HttpGet.GET_SUCC:
                    try {
                        JSONArray jsons = new JSONArray(msg.obj.toString());
                        for (int i = 0; i < 10; i++) {
                            if (i >= jsons.length()) {
                                voteList[i] = null;
                                break;
                            }
                            JSONObject jsonInfo = jsons.getJSONObject(i);
                            voteList[i] = new Vote();
                            voteList[i].setId(jsonInfo.getString("id"));
                            voteList[i].setHead(jsonInfo.getString("head"));
                            voteList[i].setLink(jsonInfo.getString("link"));
                            voteList[i].setDate(jsonInfo.getString("date"));
                            voteList[i].setAuthor(jsonInfo.getString("author"));
                        }
                        info_list = (ListView) view.findViewById(R.id.info_list);
                        tmpData = getData();
                        if (myData == null || if_refresh == true) {
                            myData = tmpData;
                        } else {
                            myData.addAll(tmpData);
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
                                            String last_time = (String) myData.get(myData.size() - 1).get("time");
                                            if_refresh = false;
                                            String oldUri="http://101.200.163.140:3000/api/votes" +
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
                                                    handler, HttpGet.TYPE_VOTE, null);
                                            // Toast.makeText(getActivity().getApplicationContext(),
                                            // "可以上拉加载了！", Toast.LENGTH_LONG).show();
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
                        System.out.println("VoteFragment: JSON WRONG!!");
                        Toast.makeText(getActivity().getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpGet.GET_FAIL:
                    Toast.makeText(getActivity().getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public VoteFragment() {
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
        view = inflater.inflate(R.layout.fragment_vote, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final SwipeRefreshLayout swipe_list = (SwipeRefreshLayout) view.findViewById(R.id.swipe_list);
        swipe_list.setColorSchemeResources(R.color.my_green, R.color.my_gray);
        swipe_list.setProgressBackgroundColor(white);
        swipe_list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if_refresh = true;
                new HttpGet("http://101.200.163.140:3000/api/votes" +
                        "?access_token=" + LogInActivity.access_token +
                        "&filter[order]=date%20DESC" +
                        "&filter[limit]=10",
                        handler, HttpGet.TYPE_VOTE, null);
                if (swipe_list.isRefreshing()) {
                    swipe_list.setRefreshing(false);
                }
            }
        });

        post_vote_button = (Button) view.findViewById(R.id.post_vote_button);
        if (LogInActivity.priority == 1) {
            post_vote_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostVoteActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            post_vote_button.setVisibility(View.INVISIBLE);
        }

        new HttpGet("http://101.200.163.140:3000/api/votes" +
                "?access_token=" + LogInActivity.access_token +
                "&filter[order]=date%20DESC" +
                "&filter[limit]=10",
                handler, HttpGet.TYPE_VOTE, null);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        for (int i = 0; i < 10; i++) {
            if (voteList[i] == null) break;
            map = new HashMap<String, Object>();
            map.put("title", voteList[i].getHead());
            map.put("time", voteList[i].getDate());
            map.put("author", voteList[i].getAuthor());
            map.put("id", voteList[i].getId());
            map.put("link", voteList[i].getLink());
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
            bundle.putString("url", (String) myData.get(position).get("link"));
            Intent intent = new Intent(getActivity(), DetailedVoteActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}

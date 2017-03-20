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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.color.white;

public class CloudFragment extends Fragment {
    Button post_file_button;
    View view;
    private ListView study_file_list;
    private ListView other_file_list;
    private List<Map<String, Object>> studyFile = new ArrayList<>();
    private List<Map<String, Object>> otherFile = new ArrayList<>();
    private List<String> fileList = new ArrayList<>();
    studyFileAdapter studyAdapter = null;
    otherFileAdapter otherAdapter = null;
    Context myContext;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println("CloudFragment: "+msg.obj.toString());

            switch (msg.what) {
                case HttpGet.GET_SUCC:
                    try {
                        // Get jsons
                        JSONArray jsons = new JSONArray(msg.obj.toString());

                        // Get fileList
                        fileList.clear();
                        for (int i = 0; i < jsons.length(); i++) {
                            JSONObject jsonInfo = jsons.getJSONObject(i);
                            fileList.add(jsonInfo.getString("name"));
                        }
                        getData();

                        // Study file
                        study_file_list = (ListView) view.findViewById(R.id.study_file);
                        if (studyAdapter == null) {
                            studyAdapter = new studyFileAdapter(getActivity());
                            study_file_list.setAdapter(studyAdapter);
                        } else {
                            studyAdapter.notifyDataSetChanged();
                        }

                        // Other file
                        other_file_list = (ListView) view.findViewById(R.id.other_file);
                        if (otherAdapter == null) {
                            otherAdapter = new otherFileAdapter(getActivity());
                            other_file_list.setAdapter(otherAdapter);
                        } else {
                            otherAdapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("CloudFragment: JSON WRONG!!");
                        Toast.makeText(getActivity().getApplicationContext(), "服务器数据异常", Toast.LENGTH_LONG).show();
                    }
                    break;

                case HttpGet.GET_FAIL:
                    Toast.makeText(getActivity().getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public CloudFragment() {
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
        view = inflater.inflate(R.layout.fragment_cloud, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        myContext = getContext();

        post_file_button = (Button) view.findViewById(R.id.post_file_button);
        post_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostFileActivity.class);
                startActivity(intent);
            }
        });

        final SwipeRefreshLayout study_swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_study_list);
        study_swipe.setColorSchemeResources(R.color.my_green, R.color.my_gray);
        study_swipe.setProgressBackgroundColor(white);
        study_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new HttpGet("http://101.200.163.140:3000/api/containers/container1/files" +
                        "?access_token=" + LogInActivity.access_token,
                        handler, HttpGet.TYPE_FILE, null);
                if (study_swipe.isRefreshing()) {
                    study_swipe.setRefreshing(false);
                }
            }
        });

        final SwipeRefreshLayout other_swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_other_list);
        other_swipe.setColorSchemeResources(R.color.my_green, R.color.my_gray);
        other_swipe.setProgressBackgroundColor(white);
        other_swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new HttpGet("http://101.200.163.140:3000/api/containers/container1/files" +
                        "?access_token=" + LogInActivity.access_token,
                        handler, HttpGet.TYPE_FILE, null);
                if (other_swipe.isRefreshing()) {
                    other_swipe.setRefreshing(false);
                }
            }
        });

        new HttpGet("http://101.200.163.140:3000/api/containers/container1/files" +
                "?access_token=" + LogInActivity.access_token,
                handler, HttpGet.TYPE_FILE, null);
    }

    private void getData() {
        studyFile.clear();
        otherFile.clear();
        Map<String, Object> map;
        for (int i = 0; i < fileList.size(); i++) {
            map = new HashMap<>();
            String title = fileList.get(i);
            if (title.length() <= 26) {
                continue;
            }

            map.put("title", title);
            // Study file
            if (title.indexOf("Study_") == 20) {
                studyFile.add(map);
            }
            // Other file
            else if (title.indexOf("Other_") == 20) {
                otherFile.add(map);
            }
        }
    }

    public final class ViewHolder {
        public TextView title;
        public ImageButton link;
    }

    public class studyFileAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        protected Context context;

        public studyFileAdapter(Context context) {
            this.context = context;
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
                convertView = myInflater.inflate(R.layout.downloadlist_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.link = (ImageButton) convertView.findViewById(R.id.link);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int index = studyFile.get(position).get("title").toString().indexOf("_", 26);
            String tmp = studyFile.get(position).get("title").toString().substring(index + 1);
            holder.title.setText(tmp);
            holder.link.setTag(position);

            holder.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download_file(position);
                }
            });

            return convertView;
        }

        public void download_file(int position) {
            String fileName = studyFile.get(position).get("title").toString();
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (Exception e) {
            }

            String oldUriString = "http://101.200.163.140:3000/api/containers/container1/download/" + fileName +
                    "?access_token=" + LogInActivity.access_token;
            RealMainActivity.uri = Uri.parse(oldUriString);

            int index = studyFile.get(position).get("title").toString().indexOf("_", 26);
            String tmp = studyFile.get(position).get("title").toString().substring(index + 1);
            RealMainActivity.title = tmp;

            ((RealMainActivity) getActivity()).getWritePermission();
        }
    }

    public class otherFileAdapter extends BaseAdapter {
        private LayoutInflater myInflater;
        protected Context context;

        public otherFileAdapter(Context context) {
            this.context = context;
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
            ViewHolder holder;
            holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = myInflater.inflate(R.layout.downloadlist_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.link = (ImageButton) convertView.findViewById(R.id.link);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            int index = otherFile.get(position).get("title").toString().indexOf("_", 26);
            String tmp = otherFile.get(position).get("title").toString().substring(index + 1);
            holder.title.setText(tmp);
            holder.link.setTag(position);

            holder.link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    download_file(position);
                }
            });

            return convertView;
        }

        public void download_file(int position) {
            String fileName = otherFile.get(position).get("title").toString();
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (Exception e) {
            }

            String oldUriString = "http://101.200.163.140:3000/api/containers/container1/download/" + fileName +
                    "?access_token=" + LogInActivity.access_token;
            RealMainActivity.uri = Uri.parse(oldUriString);

            int index = otherFile.get(position).get("title").toString().indexOf("_", 26);
            String tmp = otherFile.get(position).get("title").toString().substring(index + 1);
            RealMainActivity.title = tmp;

            ((RealMainActivity) getActivity()).getWritePermission();
        }
    }
}

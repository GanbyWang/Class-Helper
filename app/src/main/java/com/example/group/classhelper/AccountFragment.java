package com.example.group.classhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AccountFragment extends Fragment {
    public View view;
    private Context myContext;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // System.out.println(msg.obj.toString());
            
            switch (msg.what) {
                case HttpPost.POST_SUCC:
                    Toast.makeText(myContext.getApplicationContext(), "注销成功", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(), LogInActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    break;

                case HttpPost.POST_FAIL:
                    Toast.makeText(myContext.getApplicationContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    public AccountFragment() {
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
        view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        myContext = getActivity();

        ImageButton new_pwd = (ImageButton) view.findViewById(R.id.new_pwd);
        new_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewPasswordActivity.class);
                startActivity(intent);
            }
        });

        ImageButton my_file = (ImageButton) view.findViewById(R.id.my_file);
        my_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyFile.class);
                startActivity(intent);
            }
        });

        ImageButton my_info = (ImageButton) view.findViewById(R.id.my_info);
        if (LogInActivity.priority == 1) {
            my_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MyInfo.class);
                    startActivity(intent);
                }
            });
        } else {
            LinearLayout info_block = (LinearLayout) view.findViewById(R.id.info_block);
            info_block.setVisibility(View.INVISIBLE);

            LinearLayout vote_block = (LinearLayout) view.findViewById(R.id.vote_block);
            vote_block.setVisibility(View.INVISIBLE);

            View line = (View) view.findViewById(R.id.line2);
            line.setVisibility(View.INVISIBLE);

            line = (View) view.findViewById(R.id.line3);
            line.setVisibility(View.INVISIBLE);
        }

        ImageButton my_vote = (ImageButton) view.findViewById(R.id.my_vote);
        if (LogInActivity.priority == 1) {
            my_vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MyVote.class);
                    startActivity(intent);
                }
            });
        }

        Button logout = (Button) view.findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpPost("".getBytes(), "http://101.200.163.140:3000/api/customers/logout" +
                        "?access_token=" + LogInActivity.access_token,
                        handler, HttpPost.TYPE_LOGOUT);
            }
        });
    }
}
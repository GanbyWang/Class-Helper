package com.example.group.classhelper;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
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
import android.widget.TextView;
import android.widget.Toast;

public class AccountFragment extends Fragment {
    public View view;
    private Context myContext;
    private TextView class_name;

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

        // get the class name text view
        class_name = (TextView) view.findViewById(R.id.class_name);

        // set the listener on set the password
        ImageButton new_pwd = (ImageButton) view.findViewById(R.id.new_pwd);
        new_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewPasswordActivity.class);
                startActivity(intent);
            }
        });

        // set the listener on managing posting files
        ImageButton my_file = (ImageButton) view.findViewById(R.id.my_file);
        my_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyFile.class);
                startActivity(intent);
            }
        });

        // set the listener on logout button
        Button logout = (Button) view.findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpPost("".getBytes(), "http://101.200.163.140:3000/api/customers/logout" +
                        "?access_token=" + LogInActivity.access_token,
                        handler, HttpPost.TYPE_LOGOUT);
            }
        });

        // if the user is a priority user
        if (LogInActivity.priority == 1) {
            // set the class name text view
            class_name.setText("您是" + LogInActivity.className + "的管理员");

            // hide the join class and create class to a priority user
            LinearLayout joinClassBlock = (LinearLayout) view.findViewById(R.id.join_class_block);
            joinClassBlock.setVisibility(View.GONE);
            LinearLayout createClassBlock = (LinearLayout) view.findViewById(R.id.create_class_block);
            createClassBlock.setVisibility(View.GONE);

            // hide the lines
            View line = (View) view.findViewById(R.id.line1);
            line.setVisibility(View.GONE);
            line = (View) view.findViewById(R.id.line2);
            line.setVisibility(View.GONE);

            // set listeners on managing info, file and request buttons
            ImageButton my_info = (ImageButton) view.findViewById(R.id.my_info);
            ImageButton my_vote = (ImageButton) view.findViewById(R.id.my_vote);
            ImageButton my_request = (ImageButton) view.findViewById(R.id.my_request);

            my_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MyInfo.class);
                    startActivity(intent);
                }
            });

            my_vote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MyVote.class);
                    startActivity(intent);
                }
            });

            my_request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MyRequestActivity.class);
                    startActivity(intent);
                }
            });

        } else {
            // hide the post, info and request block to a normal user
            LinearLayout info_block = (LinearLayout) view.findViewById(R.id.info_block);
            info_block.setVisibility(View.GONE);
            LinearLayout vote_block = (LinearLayout) view.findViewById(R.id.vote_block);
            vote_block.setVisibility(View.GONE);
            LinearLayout request_block = (LinearLayout) view.findViewById(R.id.request_block);
            request_block.setVisibility(View.GONE);

            // hide the lines
            View line = (View) view.findViewById(R.id.line4);
            line.setVisibility(View.GONE);
            line = (View) view.findViewById(R.id.line5);
            line.setVisibility(View.GONE);
            line = (View) view.findViewById(R.id.line6);
            line.setVisibility(View.GONE);

            // if the user has joined some class
            if(!LogInActivity.className.equals(null)) {
                // set the class name text view
                class_name.setText("您的班级是" + LogInActivity.className);

                // hide the join and create block
                LinearLayout joinClassBlock = (LinearLayout) view.findViewById(R.id.join_class_block);
                joinClassBlock.setVisibility(View.GONE);
                LinearLayout createClassBlock = (LinearLayout) view.findViewById(R.id.create_class_block);
                createClassBlock.setVisibility(View.GONE);

                // hide the lines
                line = (View) view.findViewById(R.id.line1);
                line.setVisibility(View.GONE);
                line = (View) view.findViewById(R.id.line2);
                line.setVisibility(View.GONE);

            } else {
                // set the class name text view
                class_name.setText("您还未加入任何班级");

                // set listener on join class button
                ImageButton joinClassButton = (ImageButton) view.findViewById(R.id.join_class);
                joinClassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: show a list of current classes, user can choose one to join
                        // using dialog window


                    }
                });

                // set listener on create class button
                ImageButton createClassButton = (ImageButton) view.findViewById(R.id.create_class);
                createClassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // jump to create class activity
                        Intent intent = new Intent(getActivity(), CreateClassActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
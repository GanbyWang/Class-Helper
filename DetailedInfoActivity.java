package com.example.group.classhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailedInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_info);

        Bundle bundle=this.getIntent().getExtras();
        String title=bundle.getString("title");
        String author=bundle.getString("author");
        String time=bundle.getString("time");
        String content=bundle.getString("content");

        TextView info_title=(TextView) findViewById(R.id.info_title);
        TextView info_name=(TextView) findViewById(R.id.info_name);
        TextView info_time=(TextView) findViewById(R.id.info_time);
        TextView info_content=(TextView) findViewById(R.id.info_content);

        info_title.setText(title);
        info_name.setText(author);
        info_time.setText(time);
        info_content.setText(content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.header);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                finish();
            }
        });
    }
}

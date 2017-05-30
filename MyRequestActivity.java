package com.example.group.classhelper;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MyRequestActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_request);

        // get the tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // set the back icon
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        // finish when press the back icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }
}

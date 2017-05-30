package com.example.group.classhelper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateClassActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AlertDialog alert = null;
    private AlertDialog.Builder builder = null;
    private Button createButton;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        // set the context
        myContext = CreateClassActivity.this;

        // get the tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // set the back icon
        toolbar.setNavigationIcon(R.drawable.arrow_left_d);
        // finish when press the back icon
        // use a dialog to question the user
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert = null;
                builder = new android.support.v7.app.AlertDialog.Builder(CreateClassActivity.this);
                alert = builder.setMessage("您确定要退出创建班级吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // finish the activity
                                finish();
                            }
                        })
                        .setNegativeButton("继续创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        }).create();
                alert.show();
            }
        });

        // get the create button
        createButton = (Button) findViewById(R.id.create_button);
        // set listener on the create button
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView college = (TextView) findViewById(R.id.college);
                TextView major = (TextView) findViewById(R.id.major);
                TextView classNumber = (TextView) findViewById(R.id.class_number);

                // check whether the user has put in everything
                if(college.getText().toString().equals("")) {
                    Toast.makeText(myContext.getApplicationContext(), "您未填写学院", Toast.LENGTH_LONG).show();
                    return;
                }
                if(major.getText().toString().equals("")) {
                    Toast.makeText(myContext.getApplicationContext(), "您未填写专业", Toast.LENGTH_LONG).show();
                    return;
                }
                if(classNumber.getText().toString().equals("")){
                    Toast.makeText(myContext.getApplicationContext(), "您未填写班号", Toast.LENGTH_LONG).show();
                    return;
                }

                // TODO: send HTTP request to create a class
            }
        });
    }
}

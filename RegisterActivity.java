package com.example.group.classhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button registerButton;
    private TextView userName, password, repeatPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // get the text views
        userName = (TextView) findViewById(R.id.user_name);
        password = (TextView) findViewById(R.id.password);
        repeatPwd = (TextView) findViewById(R.id.repeat_pwd);

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

        // get the register button
        registerButton = (Button) findViewById(R.id.register_button);

        // set listener on the register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_name = userName.getText().toString();
                String pass_word = password.getText().toString();
                String repeat_pwd = repeatPwd.getText().toString();

                // alert if the password doesn't equal to the repeat password
                if(!pass_word.equals(repeat_pwd))
                    Toast.makeText(getApplicationContext(), "密码和重复密码不匹配！", Toast.LENGTH_LONG).show();
                else{
                    // TODO: send HTTP request here
                }
            }
        });
    }
}

package com.example.myproreha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText phone = findViewById(R.id.phone);
        final EditText password = findViewById(R.id.password);
        final Button loginBtn = findViewById(R.id.loginBtn):
        final TextView registerNow = findViewById(R.id.registerNowBtn);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String phoneTxt = phone.getText().toString();
                final  String passwordTxt = password.getText().toString();

                if (phoneTxt.isEmpty()|| passwordTxt.isEmpty()){

                    Toast.makeText(Login.this, "Pleas enter your mobile or password", Toast.LENGTH_SHORT).show();

                }else{


                }
            }
        });


    }
}
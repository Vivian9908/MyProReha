package com.example.myproreha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MyProfile extends AppCompatActivity {

    TextView profileName, profileMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        profileName= findViewById(R.id.profileName);
        profileMail=findViewById(R.id.profileMail);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            profileName.setText(bundle.getString("Title"));
            profileMail.setText(bundle.getString("Date"));

        }





    }
}
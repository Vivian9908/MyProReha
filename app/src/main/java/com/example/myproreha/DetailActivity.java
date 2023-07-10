package com.example.myproreha;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    TextView detailTitle, detailDate, detailDuration, detailNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailTitle = findViewById(R.id.detailTitle);
        detailDate = findViewById(R.id.detailDate);
        detailDuration = findViewById(R.id.detailDuration);
        detailNotes = findViewById(R.id.detailNotes);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            detailTitle.setText(bundle.getString("Title"));
            detailDate.setText(bundle.getString("Date"));
            detailDuration.setText(bundle.getString("Duration"));
            detailNotes.setText(bundle.getString("Notes"));
        }

    }
}
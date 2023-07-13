package com.example.myproreha;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;


public class DetailActivity extends AppCompatActivity {

    TextView detailTitle, detailDate, detailDuration, detailNotes;
    FloatingActionButton deleteBtn, editBtn;

    String key = "";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        detailTitle = findViewById(R.id.detailTitle);
        detailDate = findViewById(R.id.detailDate);
        detailDuration = findViewById(R.id.detailDuration);
        detailNotes = findViewById(R.id.detailNotes);
        deleteBtn = findViewById(R.id.deleteBtn);
        editBtn = findViewById(R.id.editBtn);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            detailTitle.setText(bundle.getString("Title"));
            detailDate.setText(bundle.getString("Date"));
            detailDuration.setText(bundle.getString("Duration") + " Minuten");
            detailNotes.setText(bundle.getString("Notes"));
            key = bundle.getString("Key");
        }

        //Datensatz löschen
        deleteBtn.setOnClickListener(v -> {
            final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + GlobalVariables.currentUser + "/therapies");

            reference.child(key).removeValue().addOnSuccessListener(unused -> {


                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference fileReference = storage.getReference("users/" + GlobalVariables.currentUser + "/therapies");

                fileReference.delete().addOnSuccessListener(unused1 -> {
                    // Deletion from Firebase Storage is successful
                    Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        });

        //Datensatz ändern
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, UpdateActivity.class)
                    .putExtra("Title", detailTitle.getText().toString())
                    .putExtra("Date", detailDate.getText().toString())
                    .putExtra("Duration", detailDuration.getText().toString())
                    .putExtra("Notes", detailNotes.getText().toString())
                    .putExtra("Key", key);
            startActivity(intent);
        });

    }
}
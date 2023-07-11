package com.example.myproreha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {

    TextView detailTitle, detailDate, detailDuration, detailNotes;
    FloatingActionButton deleteBtn, editBtn;

    String key = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detailTitle = findViewById(R.id.detailTitle);
        detailDate = findViewById(R.id.detailDate);
        detailDuration = findViewById(R.id.detailDuration);
        detailNotes = findViewById(R.id.detailNotes);
        deleteBtn = findViewById(R.id.deleteBtn);
        editBtn = findViewById(R.id.editBtn);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            detailTitle.setText(bundle.getString("Title"));
            detailDate.setText(bundle.getString("Date"));
            detailDuration.setText(bundle.getString("Duration"));
            detailNotes.setText(bundle.getString("Notes"));
            key = bundle.getString("Key");
        }


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MeineStundenzettel");

                reference.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Deletion from the database is successful
                        // Now, delete the file from Firebase Storage

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference().child("MeineStundenzettel");

                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Deletion from Firebase Storage is successful
                                Toast.makeText(DetailActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(DetailActivity.this, UpdateActivity.class)
                .putExtra("Title", detailTitle.getText().toString())
                .putExtra("Date", detailDate.getText().toString())
                .putExtra("Duration", detailDuration.getText().toString())
                .putExtra("Notes", detailNotes.getText().toString())
                .putExtra("Key", key);
                startActivity(intent);
            }
        });

    }
}
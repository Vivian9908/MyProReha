package com.example.myproreha;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateActivity extends AppCompatActivity {

    Button updateButton;
    EditText updateTitle, updateDuration, updateNotes;
    TextView updateDate;
    String title, date, duration, notes;
    String key;
    Uri uri;

    DatabaseReference databaseReference;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updateButton = findViewById(R.id.updateBtn);
        updateTitle = findViewById(R.id.update_title);
        updateDate = findViewById(R.id.update_date);
        updateDuration = findViewById(R.id.update_duration);
        updateNotes = findViewById(R.id.update_notes);


        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {

                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                        } else {
                            Toast.makeText(UpdateActivity.this, "Brot", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            updateTitle.setText(bundle.getString("Title"));
            updateDate.setText(bundle.getString("Date"));
            updateDuration.setText(bundle.getString("Duration"));
            updateNotes.setText(bundle.getString("Notes"));

        }
        databaseReference = FirebaseDatabase.getInstance().getReference("users/016093213131/MeineStundenzettel").child(key);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateData();
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void updateData() {
        title = updateTitle.getText().toString().trim();
        date = updateDate.getText().toString().trim();
        duration = updateDuration.getText().toString();
        notes = updateNotes.getText().toString();
        DataClass dataClass = new DataClass(title, date, duration, notes);
        databaseReference.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference reference = storage.getReference().child("users/016093213131/MeineStundenzettel");
                    reference.delete();

                    /*storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            // Deletion from Firebase Storage is successful
                            Toast.makeText(UpdateActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }
                    });*/
                    Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

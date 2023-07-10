package com.example.myproreha;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://myproreha-default-rtdb.firebaseio.com");

    DatabaseReference parentNodeRef = databaseReference.child("users/016093213131/Meine Stundenzettel" );
    DatabaseReference newChildRef = parentNodeRef.push();

    Button saveButton;
    EditText uploadTherapie, uploadDuration, uploadNotes;
    TextView uploadDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadDate = findViewById(R.id.date_input);
        uploadTherapie = findViewById(R.id.therapy_input);
        uploadDuration = findViewById(R.id.duration_input);
        uploadNotes = findViewById(R.id.notes_input);
        saveButton = findViewById(R.id.add_Button);

           /* saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();

            }
        });
    }

        public void uploadData(){


        newChildRef.setValue("Stundenzettel");

        String date = uploadDate.getText().toString();
        String duration = uploadDuration.getText().toString();
        String therapy = uploadTherapie.getText().toString();
        String notes = uploadNotes.getText().toString();
        DataClass dataClass = new DataClass(date, therapy, duration, notes);

        //We are changing the child from title to currentDate,
        // because we will be updating title as well and it may affect child value.
        //String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        newChildRef
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UploadActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }); */
    }


}
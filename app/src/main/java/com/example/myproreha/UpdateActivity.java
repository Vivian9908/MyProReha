package com.example.myproreha;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class UpdateActivity extends AppCompatActivity {

    Button updateButton;
    EditText updateDuration, updateNotes;
    TextView updateDate;
    String therapy, date, duration, notes;
    String key;
    Uri uri;

    DatabaseReference databaseReference;

    Spinner therapySpinner;

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://myproreha-default-rtdb.firebaseio.com");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadTherapyData();

        updateButton = findViewById(R.id.updateBtn);
        therapySpinner = findViewById(R.id.therapy_spinner2);
        updateDate = findViewById(R.id.update_date);
        updateDuration = findViewById(R.id.update_duration);
        updateNotes = findViewById(R.id.update_notes);


        updateDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Select date");
                builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
                builder.setTheme(R.style.ThemeOverlay_App_DatePicker);
                MaterialDatePicker<Long> datePicker = builder.build();


                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar selectedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        selectedDate.setTimeInMillis(selection);

                        int selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH);
                        int selectedMonth = selectedDate.get(Calendar.MONTH);
                        int selectedYear = selectedDate.get(Calendar.YEAR);

                        updateDate.setText(selectedDay + "." + (selectedMonth + 1) + "." + selectedYear);
                    }
                });

                datePicker.show(getSupportFragmentManager(), "DatePicker");
            }
        });


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
            // Erst das Key-Attribut aus dem Bundle abrufen
            key = bundle.getString("Key");
            therapy = bundle.getString("Therapy"); // Get the therapy value from the bundle

            // Setze die Therapieauswahl im Spinner
            int therapyPosition = -1;
            if (therapy != null) {
                ArrayAdapter<String> adapter = (ArrayAdapter<String>) therapySpinner.getAdapter();
                therapyPosition = adapter.getPosition(therapy);
            }
            if (therapyPosition != -1) {
                therapySpinner.setSelection(therapyPosition);
            }

            updateDate.setText(bundle.getString("Date"));
            updateDuration.setText(bundle.getString("Duration"));
            updateNotes.setText(bundle.getString("Notes"));
        }

        databaseRef = FirebaseDatabase.getInstance().getReference("users/"+ GlobalVariables.currentUser +"/therapies").child(key);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateData();

            }
        });
    }

    public void updateData() {

        therapy = therapySpinner.getSelectedItem().toString();
        date = updateDate.getText().toString().trim();
        duration = updateDuration.getText().toString();
        notes = updateNotes.getText().toString();

        // Check if any of the fields are empty
        if (therapy.isEmpty() || date.isEmpty() || duration.isEmpty() || notes.isEmpty()) {
            Toast.makeText(UpdateActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return; // Return without updating the data
        }

        DataClass2 dataClass = new DataClass2(date, therapy, duration, notes);

        databaseRef.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference reference = storage.getReference().child("users/" + GlobalVariables.currentUser + "/therapies");
                    reference.delete();

                    Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadTherapyData() {
        DatabaseReference therapyRef = databaseRef.child("therapy");

        therapyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> therapyList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String therapyName = snapshot.getKey();
                    therapyList.add(therapyName);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(UpdateActivity.this,
                        android.R.layout.simple_spinner_item, therapyList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                therapySpinner.setAdapter(adapter);

                // Set initial selection
                if (therapy != null && adapter != null) {
                    int initialPosition = adapter.getPosition(therapy);
                    therapySpinner.setSelection(initialPosition);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UpdateActivity", "Failed to load therapy data: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to go back without saving changes?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }






}


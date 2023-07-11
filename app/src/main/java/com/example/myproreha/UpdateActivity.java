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
    EditText updateTitle, updateDuration, updateNotes;
    TextView updateDate;
    String title, date, duration, notes;
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

        //loadTherapyData();

        updateButton = findViewById(R.id.updateBtn);
        //therapySpinner = findViewById(R.id.therapySpinner);
        updateTitle= findViewById(R.id.update_title);
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

           /* String therapyValue = bundle.getString("Title");
            int therapyIndex = getTherapyIndex(therapyValue);
            therapySpinner.setSelection(therapyIndex);*/
            updateTitle.setText(bundle.getString("Title"));
            updateDate.setText(bundle.getString("Date"));
            updateDuration.setText(bundle.getString("Duration"));
            updateNotes.setText(bundle.getString("Notes"));
            key = bundle.getString("Key");


        }
        databaseRef = FirebaseDatabase.getInstance().getReference("users/"+ GlobalVariables.currentUser +"/therapies").child(key);

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


        //String therapy = therapySpinner.getSelectedItem().toString();
        title= updateTitle.getText().toString();
        date = updateDate.getText().toString().trim();
        duration = updateDuration.getText().toString();
        notes = updateNotes.getText().toString();
        DataClass2 dataClass = new DataClass2(date, title, duration, notes);
        databaseRef.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference reference = storage.getReference().child("users/"+GlobalVariables.currentUser+"/therapies");
                    reference.delete();

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

    /*private void loadTherapyData() {
        DatabaseReference therapyRef = databaseRef.child("Therapy");

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

                // Optional: Set initial selection
                int initialPosition = adapter.getPosition("Wähle eine Therapie");
                therapySpinner.setSelection(initialPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UpdateActivity", "Failed to load therapy data: " + databaseError.getMessage());
            }
        });
    }

    private int getTherapyIndex(String therapyValue) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) therapySpinner.getAdapter();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            if (adapter.getItem(i).equals(therapyValue)) {
                return i;
            }
        }
        return 0; // Default index if therapyValue is not found
    }*/


}


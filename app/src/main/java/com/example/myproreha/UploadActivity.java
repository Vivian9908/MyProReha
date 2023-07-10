package com.example.myproreha;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UploadActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://myproreha-default-rtdb.firebaseio.com");

    DatabaseReference parentNodeRef = databaseReference.child("users/016093213131/MeineStundenzettel" );
    DatabaseReference newChildRef = parentNodeRef.push();

    Button saveButton;
    EditText  uploadTherapie, uploadDuration, uploadNotes;
    TextView uploadDate;
    
    Spinner spinner;





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

        
        //setzen aktuelles Datum
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        uploadDate.setText(currentDate);
        
        
        //Implememtierung des DatePickers
        uploadDate.setOnClickListener(new View.OnClickListener() {
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

                        uploadDate.setText(selectedDay + "." + (selectedMonth + 1) + "." + selectedYear);
                    }
                });

                datePicker.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tableRef = database.getReference("deineTabelle");

        spinner = findViewById(R.id.spinner);

        tableRef.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                               List<String> items = new ArrayList<>();

                                               for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                   // Annahme, dass die Daten in einem String-Format vorliegen
                                                   String item = snapshot.getValue(String.class);
                                                   items.add(item);
                                               }

                                               ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_spinner_item, items);
                                               adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                               spinner.setAdapter(adapter);
                                           }
                                       });
        
        


        saveButton.setOnClickListener(new View.OnClickListener() {
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
        DataClass dataClass = new DataClass(date,therapy, duration, notes);


        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

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
                });
    }
    
   

}
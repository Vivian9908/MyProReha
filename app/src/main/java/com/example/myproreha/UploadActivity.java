package com.example.myproreha;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.firebase.database.DatabaseError;
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

    DatabaseReference parentNodeRef = databaseReference.child("users/" + GlobalVariables.currentUser + "/therapies");
    DatabaseReference newChildRef = parentNodeRef.push();

    Button saveButton;
    EditText uploadDuration, uploadNotes;
    TextView uploadDate;
    Spinner therapySpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadDate = findViewById(R.id.date_input);
        uploadDuration = findViewById(R.id.duration_input);
        uploadNotes = findViewById(R.id.notes_input);
        saveButton = findViewById(R.id.add_Button);
        therapySpinner = findViewById(R.id.therapy_spinner);

        loadTherapyData();


        //setzen aktuelles Datum
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        uploadDate.setText(currentDate);
        uploadDate.setTextColor(getResources().getColor(R.color.green1));


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
                        uploadDate.setTextColor(getResources().getColor(R.color.black));
                    }
                });

                datePicker.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadData();

            }
        });
    }

    public void uploadData() {


        String date = uploadDate.getText().toString();
        String duration = uploadDuration.getText().toString();
        String therapy = therapySpinner.getSelectedItem().toString();
        String notes = uploadNotes.getText().toString();


        if (date.isEmpty() || duration.isEmpty() || therapy.isEmpty() || notes.isEmpty() || therapy.equals("Wähle eine Therapie")) {
            Toast.makeText(UploadActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        newChildRef.child("Therapies").setValue("Stundenzettel");
        DataClass2 dataClass = new DataClass2(date, therapy, duration, notes);

        newChildRef.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
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

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    private void loadTherapyData() {
        DatabaseReference therapyRef = databaseReference.child("therapy");

        therapyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> therapyList = new ArrayList<>();
                therapyList.add("Wähle eine Therapie");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String therapyName = snapshot.getKey();
                    therapyList.add(therapyName);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UploadActivity.this,
                        android.R.layout.simple_spinner_item, therapyList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view;
                        if (position == 0) {
                            textView.setTextColor(getResources().getColor(R.color.green1));
                            Drawable icDrop = getResources().getDrawable(R.drawable.ic_drop);
                            icDrop.setBounds(0, 0, icDrop.getIntrinsicWidth(), icDrop.getIntrinsicHeight());
                            textView.setCompoundDrawables(null, null, icDrop, null);
                            textView.setCompoundDrawablePadding(8);
                        } else {
                            textView.setTextColor(getResources().getColor(android.R.color.black));
                        }
                        return view;
                    }
                };

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                therapySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UploadActivity", "Failed to load therapy data: " + databaseError.getMessage());
            }
        });
    }
}
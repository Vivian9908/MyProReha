package com.example.myproreha;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class UpdateActivity extends AppCompatActivity {

    Button updateButton;
    EditText updateDuration, updateNotes;
    TextView updateDate;
    String therapy, date, duration, notes;
    String key;

    Spinner therapySpinner;

    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://myproreha-default-rtdb.firebaseio.com");


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        updateButton = findViewById(R.id.updateBtn);
        therapySpinner = findViewById(R.id.therapy_spinner2);
        updateDate = findViewById(R.id.update_date);
        updateDuration = findViewById(R.id.update_duration);
        updateNotes = findViewById(R.id.update_notes);
        updateDate.setTextColor(getResources().getColor(R.color.black));

        loadTherapyData();

        updateDate.setOnClickListener(v -> {

            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Select date");
            builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
            builder.setTheme(R.style.ThemeOverlay_App_DatePicker);
            MaterialDatePicker<Long> datePicker = builder.build();


            datePicker.addOnPositiveButtonClickListener(selection -> {
                Calendar selectedDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                selectedDate.setTimeInMillis(selection);

                int selectedDay = selectedDate.get(Calendar.DAY_OF_MONTH);
                int selectedMonth = selectedDate.get(Calendar.MONTH);
                int selectedYear = selectedDate.get(Calendar.YEAR);

                updateDate.setText(selectedDay + "." + (selectedMonth + 1) + "." + selectedYear);
            });

            datePicker.show(getSupportFragmentManager(), "DatePicker");
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            key = bundle.getString("Key");
            therapy = bundle.getString("Title");
            updateDate.setText(bundle.getString("Date"));
            updateDuration.setText(bundle.getString("Duration"));
            updateNotes.setText(bundle.getString("Notes"));
        }


        databaseRef = FirebaseDatabase.getInstance().getReference("users/" + GlobalVariables.currentUser + "/therapies").child(key);

        updateButton.setOnClickListener(v -> updateData());
    }

    public void updateData() {

        therapy = therapySpinner.getSelectedItem().toString();
        date = updateDate.getText().toString().trim();
        duration = updateDuration.getText().toString();
        notes = updateNotes.getText().toString();

        // Check if any of the fields are empty
        if (therapy.isEmpty() || date.isEmpty() || duration.isEmpty() || notes.isEmpty() || therapy.equals("Wähle eine Therapie")) {
            Toast.makeText(UpdateActivity.this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
            return; // Return without updating the data
        }

        DataClass2 dataClass = new DataClass2(date, therapy, duration, notes);

        databaseRef.setValue(dataClass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("users/" + GlobalVariables.currentUser + "/therapies");
                reference.delete();

                Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(UpdateActivity.this, Objects.requireNonNull(e.getMessage()), Toast.LENGTH_SHORT).show());
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    private void loadTherapyData() {
        DatabaseReference therapyRef = databaseRef.child("therapy");

        therapyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> therapyList = new ArrayList<>();
                therapyList.add("Wähle eine Therapie");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String therapyName = snapshot.getKey();
                    therapyList.add(therapyName);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(UpdateActivity.this,
                        android.R.layout.simple_spinner_item, therapyList) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView textView = (TextView) view;
                        if (position == 0) {
                            textView.setTextColor(getResources().getColor(R.color.green1));
                            @SuppressLint("UseCompatLoadingForDrawables") Drawable icDrop = getResources().getDrawable(R.drawable.ic_drop);
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

                // Set initial selection
                if (therapy != null) {
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
        builder.setMessage("Bist du sicher das du ohne Änderungen zurückgehen möchtest?");
        builder.setPositiveButton("Ja", (dialog, which) -> UpdateActivity.super.onBackPressed());
        builder.setNegativeButton("Nein", null);
        builder.show();
    }


}


package com.example.myproreha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class YourProfile extends AppCompatActivity {

    DatabaseReference databaseReference;
    FloatingActionButton fab;
    EditText fullname;
    EditText email;
    EditText phone;
    EditText password;
    EditText conPassword;
    Button changeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://myproreha-default-rtdb.firebaseio.com/users/123/fullname");

        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        conPassword = findViewById(R.id.conPassword);
        changeBtn = findViewById(R.id.changeBtn);

        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get Data from EditText into String Variable
                final String fullnameTxt = fullname.getText().toString();
                final String emailTxt = email.getText().toString();
                final String phoneTxt = phone.getText().toString();
                final String passwordTxt = password.getText().toString();
                final String conPasswordTxt = conPassword.getText().toString();

                //check if user fill out the fields before sending
                if (fullnameTxt.isEmpty() || emailTxt.isEmpty() || phoneTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(YourProfile.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                }
                //check if passwords are matching
                //if not how text
                else if (!passwordTxt.equals(conPasswordTxt)) {
                    Toast.makeText(YourProfile.this, "Passwords are not matching", Toast.LENGTH_SHORT).show();

                } else {

                    databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //check if phone is not registered before
                            if (snapshot.hasChild(phoneTxt)) {
                                Toast.makeText(YourProfile.this, "phone is already registered", Toast.LENGTH_SHORT).show();

                            } else {
                                //sending Data to realtimedatabase
                                //we are using the phone number as unique ID

                                databaseReference.child("users").child(phoneTxt).child("fullname").setValue(fullnameTxt);
                                databaseReference.child("users").child(phoneTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(phoneTxt).child("password").setValue(passwordTxt);


                                Toast.makeText(YourProfile.this, "user info changed successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }

            }
        });


        // Daten aus der Datenbank abrufen
        /*databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Hier haben Sie Zugriff auf die Daten aus der Datenbank
                String data = dataSnapshot.getValue(String.class);
                fullname.setText(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Behandeln Sie den Fehler, falls erforderlich
            }
        });*/

    }
}

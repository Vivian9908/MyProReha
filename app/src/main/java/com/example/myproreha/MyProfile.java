package com.example.myproreha;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class MyProfile extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView profileName, profileMail;
    FloatingActionButton deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + GlobalVariables.currentUser);

        profileName = findViewById(R.id.profileName);
        profileMail = findViewById(R.id.profileMail);
        deleteBtn = findViewById(R.id.deleteBtn);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String dataFullname = dataSnapshot.child("/dataFullname").getValue(String.class);
                    profileName.setText(dataFullname);
                    String dataEmail = dataSnapshot.child("/dataEmail").getValue(String.class);
                    profileMail.setText(dataEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle Fehler beim Datenbankzugriff
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/"+ GlobalVariables.currentUser);

                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference fileReference = storage.getReference("users/"+ GlobalVariables.currentUser);

                        fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Deletion from Firebase Storage is successful
                                Toast.makeText(MyProfile.this, "Deleted", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}

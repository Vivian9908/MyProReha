package com.example.myproreha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MyProfile extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView profileName, profileMail, sum;
    FloatingActionButton deleteBtn;
    private ActivityResultLauncher<Intent> launcher;



    private ImageView profilePic;
    public Uri imageUri;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Verarbeiten Sie das Ergebnis hier
            }
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + GlobalVariables.currentUser);

        profileName = findViewById(R.id.profileName);
        profileMail = findViewById(R.id.profileMail);
        deleteBtn = findViewById(R.id.deleteBtn);
        sum = findViewById(R.id.sum);
        profilePic = findViewById(R.id.imageView2);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePicture();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sumDuration = 0; // Initialize sumDuration variable
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String dataDuration = itemSnapshot.child( "/therapies").child("/dataDuration").getValue(String.class);
                    Log.e("UpdateActivity", "Failed to load therapy data" + dataDuration);
                   int duration = Integer.valueOf(dataDuration); // Convert dataDuration to int
                    sumDuration += duration; // Add duration to sumDuration
                }
                sum.setText(String.valueOf(sumDuration)); // Set the sumDuration as text in the sum TextView
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

    private void choosePicture() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();

        }
    }

    private void uploadPicture() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setView(R.layout.progress_layout);

final String randomKey = UUID.randomUUID().toString();
StorageReference riversRef = storageReference.child("images/" + randomKey);

riversRef.putFile(imageUri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar.make(findViewById(android.R.id.content), "Image uploaded", Snackbar.LENGTH_LONG).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MyProfile.this, "Failed Image Upload", Toast.LENGTH_SHORT).show();
            }
        });

    }

}

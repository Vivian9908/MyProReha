package com.example.myproreha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

        databaseReference.child("therapies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sumDuration = 0; // Initialize sumDuration variable
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String dataDuration = itemSnapshot.child("dataDuration").getValue(String.class);
                    Log.e("UpdateActivity", "Failed to load therapy data" + dataDuration);
                    int duration = Integer.valueOf(dataDuration); // Convert dataDuration to int
                    sumDuration += duration; // Add duration to sumDuration
                }
                sum.setText(String.valueOf(sumDuration) + " Minuten"); // Set the sumDuration as text in the sum TextView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle Fehler beim Datenbankzugriff
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
                builder.setTitle("Bestätigung");
                builder.setMessage("Möchten Sie das Konto wirklich löschen?");

                builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + GlobalVariables.currentUser);

                        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference fileReference = storage.getReference("users/" + GlobalVariables.currentUser);

                                fileReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Deletion from Firebase Storage is successful
                                        Toast.makeText(MyProfile.this, "Gelöscht", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                });

                builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Abbrechen, wenn der Benutzer "Nein" wählt
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }





    private void uploadPicture(Uri imageUri) {
        // Erstelle einen Dateinamen für das Bild basierend auf dem aktuellen Zeitstempel
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setView(R.layout.progress_layout);

        final String randomKey = UUID.randomUUID().toString();
        // Erhalte eine Referenz auf den Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Erstelle eine Referenz auf den Speicherort, an dem das Bild hochgeladen werden soll
        StorageReference imageRef = storageRef.child("images/" + randomKey);

        // Lade das Bild in den Firebase Storage hoch
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Der Upload war erfolgreich
                    // Erhalte die herunterladbare URL des Bildes
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        // Hier kannst du die URL des Bildes weiterverarbeiten (z. B. speichern oder anzeigen)
                        // ...

                        // Beispiel: Zeige die herunterladbare URL in einem Toast an
                        Toast.makeText(getApplicationContext(), "Bild hochgeladen: " + imageUrl, Toast.LENGTH_SHORT).show();
                    });
                })
                .addOnFailureListener(e -> {
                    // Ein Fehler ist aufgetreten
                    // Zeige eine Fehlermeldung an
                    Toast.makeText(getApplicationContext(), "Fehler beim Hochladen des Bildes.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture(imageUri);

        }
    }

    private void choosePicture() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }


}

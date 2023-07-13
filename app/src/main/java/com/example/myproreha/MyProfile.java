package com.example.myproreha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.UUID;

public class MyProfile extends AppCompatActivity {

    DatabaseReference databaseReference;
    TextView profileName, profileMail, time;
    FloatingActionButton deleteBtn;
    private ActivityResultLauncher<Intent> launcher;


    private ImageView profilePic;
    public Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();


        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + GlobalVariables.currentUser);


        profileName = findViewById(R.id.profileName);
        profileMail = findViewById(R.id.profileMail);
        deleteBtn = findViewById(R.id.deleteBtn);
        time = findViewById(R.id.sum);
        profilePic = findViewById(R.id.imageView2);

        profilePic.setOnClickListener(v -> choosePicture());

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
            }
        });

        databaseReference.child("therapies").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int sumDuration = 0;
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    String dataDuration = itemSnapshot.child("dataDuration").getValue(String.class);
                    Log.e("UpdateActivity", "Failed to load therapy data" + dataDuration);
                    assert dataDuration != null;
                    int duration = Integer.parseInt(dataDuration);
                    sumDuration += duration;
                }
                int hours = sumDuration / 60;
                int minutes = sumDuration % 60;
                String timeString = hours + " Stunden " + minutes + " Minuten";
                time.setText(timeString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
            builder.setTitle("Bestätigung");
            builder.setMessage("Möchten Sie das Konto wirklich löschen?");

            builder.setPositiveButton("Ja", (dialog, which) -> {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/" + GlobalVariables.currentUser);

                reference.removeValue().addOnSuccessListener(unused -> {

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        currentUser.delete().addOnSuccessListener(unused1 -> {

                            FirebaseStorage firestorage = FirebaseStorage.getInstance();
                            StorageReference fileReference = firestorage.getReference("users/" + GlobalVariables.currentUser);

                            fileReference.delete().addOnSuccessListener(unused2 -> {

                                Toast.makeText(MyProfile.this, "Konto gelöscht", Toast.LENGTH_SHORT).show();

                            });
                        }).addOnFailureListener(e -> {

                            Toast.makeText(MyProfile.this, "Fehler beim Löschen des Kontos", Toast.LENGTH_SHORT).show();
                        });
                    } else {

                        Toast.makeText(MyProfile.this, "Nicht angemeldet", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(new Intent(getApplicationContext(), Login.class));
                });
            });

            builder.setNegativeButton("Nein", (dialog, which) -> {
                dialog.dismiss();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


    private void uploadPicture(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfile.this);
        builder.setView(R.layout.progress_layout);

        final String randomKey = UUID.randomUUID().toString();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        StorageReference imageRef = storageRef.child("images/" + randomKey);

        // Lädt Bild in den Firebase Storage hoch
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    Toast.makeText(getApplicationContext(), "Bild hochgeladen: " + imageUrl, Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Fehler beim Hochladen des Bildes.", Toast.LENGTH_SHORT).show());
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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intent);

    }


}

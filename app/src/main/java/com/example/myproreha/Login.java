package com.example.myproreha;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mail;
    private EditText password;
    private FirebaseUser user1;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        Button btn = findViewById(R.id.loginBtn);
        TextView textRegister = findViewById(R.id.registerNowBtn);

        btn.setOnClickListener(v -> login());

        textRegister.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));


    }

    private void login() {
        String user = mail.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (user.isEmpty()) {
            mail.setError("Email bitte angeben!");
        }
        if (pass.isEmpty()) {
            password.setError("Passwort bitte angeben!y");
        } else {
            mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user1 = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("users");

                    GlobalVariables.currentUser = user1.getUid();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    Toast.makeText(Login.this, "Login erfolgreich", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(Login.this, "Login fehlgeschlagen: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();

                }

            });
        }
    }
}
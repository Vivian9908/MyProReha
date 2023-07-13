package com.example.myproreha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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


public class Register extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private EditText fullname, email;
    private EditText password;
    private FirebaseUser user;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button registerBtn = findViewById(R.id.registerBtn);
        TextView loginNow = findViewById(R.id.loginNow);

        registerBtn.setOnClickListener(v -> SignUp());

        loginNow.setOnClickListener(v -> startActivity(new Intent(Register.this, Login.class)));



    }

    private void SignUp() {
        String fullnam = fullname.getText().toString();
        String emai = email.getText().toString();
        String pass = password.getText().toString();

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");


        if(fullnam.isEmpty()){
            fullname.setError("Name darf nicht fehlen!");
        }
        if(emai.isEmpty()){
            email.setError("Email darf nicht fehlen!");
        }
        if(pass.isEmpty()){
            password.setError("Passwort darf nicht fehlen!");
        }
        else{
            mAuth.createUserWithEmailAndPassword(emai, pass).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "Erfolgreich registriert",Toast.LENGTH_SHORT ).show();
                    startActivity(new Intent(Register.this,MainActivity.class));
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    reference = FirebaseDatabase.getInstance().getReference("users");

                    GlobalVariables.currentUser = user.getUid();
                    Log.d("currentUser", "user: "+ GlobalVariables.currentUser);




                    DataClass helperClass = new DataClass(fullnam, emai, pass );
                    reference.child(GlobalVariables.currentUser).setValue(helperClass);
                }
                else{
                    Toast.makeText(Register.this, "Registrierung fehlgeschlagen "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



    }


}


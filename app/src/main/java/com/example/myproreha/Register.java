package com.example.myproreha;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.UidVerifier;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity{
    private FirebaseAuth mAuth;
    private EditText fullname, email;
    private EditText password;
    private Button registerBtn;
    private TextView loginNow;
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
        registerBtn= findViewById(R.id.registerBtn);
        loginNow = findViewById(R.id.loginNow);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();

            }

        });

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });



    }

    private void Register() {
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
            mAuth.createUserWithEmailAndPassword(emai, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "User registeres succesfully",Toast.LENGTH_SHORT ).show();
                        startActivity(new Intent(Register.this,MainActivity.class));
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance().getReference("users");

                        GlobalVariables.currentUser = user.getUid().toString();
                        Log.d("currentUser", "user: "+ GlobalVariables.currentUser);




                        DataClass helperClass = new DataClass(fullnam, emai, pass );
                        reference.child(GlobalVariables.currentUser).setValue(helperClass);
                    }
                    else{
                        Toast.makeText(Register.this, "Registration Failed"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



    }


}


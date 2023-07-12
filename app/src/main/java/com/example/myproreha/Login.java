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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mail;
    private EditText password;
    private Button btn;
    private TextView textRegister;
    private FirebaseUser user1;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mail = findViewById(R.id.mail);
        password = findViewById(R.id.password);
        btn = findViewById(R.id.loginBtn);
        textRegister = findViewById(R.id.registerNowBtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });


    }

    private void login() {
        String user = mail.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(user.isEmpty()){
            mail.setError("Username can not be empty");
        }
        if(pass.isEmpty()){
            password.setError("Password can not be empty");
        }
        else{
            mAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        user1 = FirebaseAuth.getInstance().getCurrentUser();
                        reference = FirebaseDatabase.getInstance().getReference("users");

                        GlobalVariables.currentUser = user1.getUid().toString();



                        //UserHelperClass helperClass = new UserHelperClass(user,pass);
                        //Log.d("hallo","helperClass:"+ helperClass);
                        //reference.child(GlobalVariables.currentUser).setValue(helperClass);

                        startActivity(new Intent(Login.this, MainActivity.class));
                        Toast.makeText(Login.this, "Login sucess", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        Toast.makeText(Login.this, "Login Failed"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }

                }
            });
        }
    }


}
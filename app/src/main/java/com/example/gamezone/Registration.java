package com.example.gamezone;


import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText et_fullName, et_password, et_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        et_fullName = (EditText) findViewById(R.id.FullName);
        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.Password);

    }

    public void registerDB(View view) {
        String name = et_fullName.getText().toString();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        if (name.isEmpty()) {
            et_fullName.setError("Full Name is required");
            et_fullName.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            et_email.setError("Email is required");
            et_email.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) { //you can also check the num. of chars.
            et_password.setError("Password is required and must be > 6");
            et_password.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            et_email.setError("Please provide valid Email");
            et_email.requestFocus();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Player user = new Player(name, email);
                    FirebaseDatabase.getInstance().getReference("users").child(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "User has been registered!", Toast.LENGTH_LONG).show();
                                        //redirect to the main page
                                    } else {
                                        Toast.makeText(getApplicationContext(), "FAILED: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
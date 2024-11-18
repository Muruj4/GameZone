package com.example.gamezone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText et_fullName, et_password, et_email;
    private DatabaseReference databaseReference;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("players");

        et_fullName = findViewById(R.id.FullName);
        et_email = findViewById(R.id.email);
        et_password = findViewById(R.id.Password);
    }

    public void registerDB(View view) {
        String name = et_fullName.getText().toString().trim();
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
        if (password.isEmpty() || password.length() < 6) {
            et_password.setError("Password is required and must be > 6");
            et_password.requestFocus();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String playerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Player player = new Player(playerId, name, email, null, null, null,null,null,null);
                    sendDatatoFirebase(playerId, player);
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendDatatoFirebase(String playerId, Player player) {
        databaseReference.child(playerId).setValue(player).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    RadioButton rbPlayer = findViewById(R.id.rb_player);
                    if (rbPlayer.isChecked()) {
                        Toast.makeText(getApplicationContext(), "Player has been registered!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Registration.this, Registrationpart2.class);
                        intent.putExtra("playerId", playerId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select the player role!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to save user data.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

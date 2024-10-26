package com.example.gamezone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class Registrationpart2 extends AppCompatActivity {
    Uri imageUri;
    ImageButton pickImageButton;
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference databaseReference;
    String playerId; // To hold the playerId from the previous activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationpart2);

        pickImageButton = findViewById(R.id.pickimg);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Get the playerId passed from the Registration activity
        playerId = getIntent().getStringExtra("playerId");
        databaseReference = FirebaseDatabase.getInstance().getReference("players").child(playerId); // Use the same playerId
    }

    public void opengallarie(View view) {
        Intent intentimg = new Intent(Intent.ACTION_GET_CONTENT);
        intentimg.setType("image/*");
        startActivityForResult(intentimg, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap decodeStream = BitmapFactory.decodeStream(inputStream);
                pickImageButton.setImageBitmap(decodeStream);

                // Upload image to Firebase Storage
                uploadImageToFirebase();
            } catch (Exception ex) {
                Log.e("ex", ex.getMessage());
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // Create a reference for the image in Firebase Storage
            StorageReference imageRef = storageRef.child("player_images/" + System.currentTimeMillis() + ".jpg");

            // Upload the image
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the image URL after uploading
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    // Save player data to the database
                                    saveToDatabase(downloadUrl.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("URL Error", e.getMessage());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Upload Error", e.getMessage());
                        }
                    });
        }
    }

    private void saveToDatabase(String imageUrl) {
        String skillLevel = ((Spinner) findViewById(R.id.spinner_skill_level)).getSelectedItem().toString();
        String preferredGame = ((Spinner) findViewById(R.id.spinner_preferred_games)).getSelectedItem().toString();

        // Update the player's existing data in the database
        databaseReference.child("skillLevel").setValue(skillLevel);
        databaseReference.child("preferredGame").setValue(preferredGame);
        databaseReference.child("imageUrl").setValue(imageUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Data saved successfully
                Toast.makeText(Registrationpart2.this, "Player profile updated successfully!", Toast.LENGTH_SHORT).show();
                Log.i("Database", "Player profile updated successfully!");
                // Remove finish(); to stay on the same activity
                // Optionally, you can clear the fields or reset UI elements if needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Database Error", e.getMessage());
            }
        });
    }
}

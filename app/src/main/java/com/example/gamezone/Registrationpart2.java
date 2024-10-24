package com.example.gamezone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Spinner;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrationpart2);

        pickImageButton = findViewById(R.id.pickimg);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
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

                // رفع الصورة إلى Firebase Storage
                uploadImageToFirebase();

            } catch (Exception ex) {
                Log.e("ex", ex.getMessage());
            }
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            // إنشاء مرجع للصورة داخل Firebase Storage
            StorageReference imageRef = storageRef.child("player_images/" + System.currentTimeMillis() + ".jpg");

            // رفع الصورة
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // الحصول على رابط الصورة بعد رفعها
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    // حفظ بيانات اللاعب في قاعدة البيانات
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
        databaseReference = FirebaseDatabase.getInstance().getReference("players");

        String playerId = databaseReference.push().getKey();
        Log.d("PlayerID", "Generated PlayerID: " + playerId);
        String skillLevel = ((Spinner) findViewById(R.id.spinner_skill_level)).getSelectedItem().toString();
        String preferredGame = ((Spinner) findViewById(R.id.spinner_preferred_games)).getSelectedItem().toString();

        // إنشاء كائن لاعب يحتوي على جميع المعلومات بما في ذلك رابط الصورة
        Player player = new Player(playerId, skillLevel, preferredGame, imageUrl); // استخدام رابط الصورة المرفوعة

        // تخزين بيانات اللاعب في قاعدة البيانات
        databaseReference.child(playerId).setValue(player).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // البيانات تم تخزينها بنجاح
                Log.i("Database", "Player profile created successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Database Error", e.getMessage());
            }
        });
    }
}

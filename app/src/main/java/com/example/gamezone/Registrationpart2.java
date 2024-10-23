package com.example.gamezone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
//import com.google.firebase.database.core.view.View;

import java.io.InputStream;

public class Registrationpart2 extends AppCompatActivity {
    ImageButton pickImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 setContentView(R.layout.activity_registrationpart2);
        pickImageButton = findViewById(R.id.pickimg);

    }
    public void opengallarie(View view){

        Intent intentimg = new Intent(Intent.ACTION_GET_CONTENT);
        intentimg.setType("image/*");
     startActivityForResult(intentimg,100);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_OK &&requestCode==100){
            Uri url = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(url);
                Bitmap decodeStream= BitmapFactory.decodeStream(inputStream);
                pickImageButton.setImageBitmap(decodeStream);
            }catch (Exception ex){
                Log.e("ex",ex.getMessage());
            }
        }

    }

}
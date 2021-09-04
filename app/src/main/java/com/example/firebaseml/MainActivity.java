package com.example.firebaseml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button txtButton;
    ImageView imageView;
    TextView textView;
    TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtButton = findViewById(R.id.button);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.textView);

        txtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(MainActivity.this)
                        .crop()
                        .galleryOnly()      //Crop image(Optional), Check Customization for more option
                        .start(100);    //100:request code for accessing purpose

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData(); //original path location
        imageView.setImageURI(uri); //setting img

        //taken from G dev android ref page:https://developers.google.com/ml-kit/vision/text-recognition/android?authuser=0#java
        InputImage image = null;//initialization

        try {
            image = InputImage.fromFilePath(this, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (requestCode == 100) {

            //code taken from G - Dev page
            final Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override

                                public void onSuccess(Text visionText) {
                                    StringBuilder resultText = new StringBuilder();
                                    for (Text.TextBlock block : visionText.getTextBlocks()) {
                                        resultText.append(block.getText().toString());
                                        resultText.append("\n");
                                    }
                                    textView.setText(resultText.toString());
                                    //Toast.makeText(MainActivity.this,"Text"+visionText.toString() ,Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            textView.setText(e.toString());
                                            Toast.makeText(MainActivity.this, "Error" + e, Toast.LENGTH_LONG).show();
                                        }
                                    });
        }
    }
}
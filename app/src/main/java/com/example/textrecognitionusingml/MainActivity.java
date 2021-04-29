package com.example.textrecognitionusingml;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private Button button_capture, button_detect;
    private TextView text_view_detect;
    private ImageView image_view;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        clickButtons();

    }

    private void initViews()
    {
        button_capture = findViewById(R.id.btn_capture_img);
        button_detect = findViewById(R.id.btn_detect_txt);
        text_view_detect = findViewById(R.id.txt_view_dislpay);
        image_view = findViewById(R.id.img_view);
    }

    private void clickButtons()
    {
        button_capture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dispatchTakePictureIntent();
                button_capture.setVisibility(View.INVISIBLE);
                button_detect.setVisibility(View.VISIBLE);
            }
        });

        button_detect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                detectTextFromImage();
                button_detect.setVisibility(View.INVISIBLE);
                button_capture.setVisibility(View.VISIBLE);
            }
        });
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            image_view.setImageBitmap(imageBitmap);
        }
    }

    private void detectTextFromImage()
    {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>()
        {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText)
            {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText)
    {
        List<FirebaseVisionText.Block> blocks = firebaseVisionText.getBlocks();

        if (blocks.size() == 0)
        {
            text_view_detect.setText("فين النص اللي في الصورة ؟");
        }

        else
        {
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks())
            {
                String text = block.getText();
                text_view_detect.setText(text);
            }
        }
    }
}

package com.haram.facetag;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class AnalyzeActivity extends AppCompatActivity {

    private FirebaseVisionImage image;
    private FirebaseVisionFaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        image = FirebaseVisionImage.fromBitmap(
                BitmapFactory.decodeFile(getIntent().getStringExtra("photoPath")));
        faceDetectionInitialize();
        faceDetection();
    }

    private void faceDetection() {
        detector.detectInImage(image).addOnSuccessListener(
                new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> faces) {
                        Toast.makeText(AnalyzeActivity.this, "성공!",Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void faceDetectionInitialize() {
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .build();
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }
}

package com.haram.facetag;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class IntroActivity extends AppCompatActivity {

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        CircleImageView profile = findViewById(R.id.intro_profile_imv);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoto();
            }
        });
    }

    // 권한 체크하고 카메라 불러오기
    private void addPhoto() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    Constants.REQUEST_PERMISSION_CAMERA);
        } else {
            pickPhoto();
        }
    }

    private void pickPhoto() {
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePhotoIntent.resolveActivity(getPackageManager())!=null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(photoFile!=null) {
                    Uri photoUri = FileProvider.getUriForFile(this, Constants.FILE_PROVIDER, photoFile);
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePhotoIntent, Constants.REQUEST_PERMISSION_CAPTURE);
                }
            }
        }
    }

    private File createImageFile() throws IOException {

        String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
        String photoName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(photoName, ".jpg", storageDir);

        photoPath = photoFile.getAbsolutePath();
        return photoFile;
    }

    // 사용자가 사진을 찍는 것에 대한 행동 후 체크
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == Constants.REQUEST_PERMISSION_CAPTURE) {
            // 기본 이미지 안보이게 하고
            findViewById(R.id.intro_default_imv).setVisibility(View.INVISIBLE);
            // 찍은 이미지로 대체
            CircleImageView profileImage = findViewById(R.id.intro_profile_imv);
            profileImage.setImageBitmap(BitmapFactory.decodeFile(photoPath));
        }
    }

    // 사용자가 권한여부에 대한 행동 후 체크
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == Constants.REQUEST_PERMISSION_CAMERA && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
            pickPhoto();
        } else {
            Toast.makeText(this, getString(R.string.intro_permission_message), Toast.LENGTH_SHORT).show();
        }
    }
}

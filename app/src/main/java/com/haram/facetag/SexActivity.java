package com.haram.facetag;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SexActivity extends AppCompatActivity {

    private FrameLayout male, female;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex);

        male = findViewById(R.id.sex_male);
        female = findViewById(R.id.sex_female);
        progressBar = findViewById(R.id.sex_progress);

        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(Color.rgb(60,179,113), PorterDuff.Mode.MULTIPLY);

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                selectSex(getString(R.string.sex_field_male));
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                selectSex(getString(R.string.sex_field_female));
            }
        });
    }

    private void selectSex(String sex) {
        male.setEnabled(false);
        female.setEnabled(false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(getString(R.string.sex_collection_user)).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update(getString(R.string.sex_field_sex),sex)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startActivity(new Intent(SexActivity.this, IntroActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        male.setEnabled(true);
                        female.setEnabled(true);
                        Toast.makeText(SexActivity.this, getString(R.string.sex_invalid_input), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

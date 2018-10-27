package com.haram.facetag;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText editText_email,editText_password;
    private String email, password;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_email = findViewById(R.id.main_edittext_email);
        editText_password = findViewById(R.id.main_edittext_password);
        auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.main_progress);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setIndeterminate(true);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(Color.rgb(60,179,113), PorterDuff.Mode.MULTIPLY);


        Button button_register = findViewById(R.id.main_button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editText_email.getText().toString().trim();
                password = editText_password.getText().toString().trim();

                if(isCompleted()){
                    progressBar.setVisibility(View.VISIBLE);
                    registerUserByFirebase();
                }
            }
        });

        Button button_login = findViewById(R.id.main_button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editText_email.getText().toString().trim();
                password = editText_password.getText().toString().trim();

                if(isCompleted()){
                    progressBar.setVisibility(View.VISIBLE);
                    loginByFirebase(false);
                }
            }
        });
    }

    // Firebase 인증으로 로그인
    private void loginByFirebase(final boolean isFirst) {
        editText_email.setEnabled(false);
        editText_password.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, isFirst?SexActivity.class:AnalyzeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    editText_email.setEnabled(true);
                    editText_password.setEnabled(true);
                    try {
                        throw task.getException();
                    } catch(Exception e){
                        progressBar.setVisibility(View.INVISIBLE);
                        editText_email.setEnabled(true);
                        editText_password.setEnabled(true);
                        Toast.makeText(MainActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    // Firebase 인증으로 유저 등록
    private void registerUserByFirebase() {
        editText_email.setEnabled(false);
        editText_password.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            inputUserID();
                        }
                        else {
                            progressBar.setVisibility(View.INVISIBLE);
                            editText_email.setEnabled(true);
                            editText_password.setEnabled(true);
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) {
                                editText_email.setError(getString(R.string.user_collision));
                                editText_email.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                editText_email.setError(getString(R.string.invalid_email));
                                editText_email.requestFocus();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    // Cloud Firestore에 사용자 ID값 넣기
    private void inputUserID() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, String> gender = new HashMap<>();
        gender.put("sex","");
        db.collection("User").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(gender)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, getString(R.string.register_complete), Toast.LENGTH_SHORT)
                                .show();
                        loginByFirebase(true);
                    }
                });
    }

    // 비어있는 부분이 없는지 확인
    private boolean isCompleted(){
        if(TextUtils.isEmpty(email)){
            editText_email.setError(getString(R.string.empty_email));
            editText_email.requestFocus();
            return false;
        } else if(TextUtils.isEmpty(password)){
            editText_password.setError(getString(R.string.empty_password));
            editText_password.requestFocus();
            return false;
        } else if(password.length()<6){
            editText_password.setError(getString(R.string.invalid_password));
            editText_password.requestFocus();
            return false;
        }
        return true;
    }
}

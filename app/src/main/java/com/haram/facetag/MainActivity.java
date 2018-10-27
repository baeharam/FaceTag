package com.haram.facetag;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class MainActivity extends AppCompatActivity {

    private EditText editText_email,editText_password;
    private String email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_email = findViewById(R.id.main_edittext_email);
        editText_password = findViewById(R.id.main_edittext_password);
        auth = FirebaseAuth.getInstance();

        Button button_register = findViewById(R.id.main_button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editText_email.getText().toString().trim();
                password = editText_password.getText().toString().trim();

                if(isCompleted()){
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
                    loginByFirebase();
                }
            }
        });
    }

    private void loginByFirebase() {
        editText_email.setEnabled(false);
        editText_password.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this, GenderActivity.class));
                    finish();
                } else {
                    editText_email.setEnabled(true);
                    editText_password.setEnabled(true);
                    try {
                        throw task.getException();
                    } catch(Exception e){
                        Toast.makeText(MainActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                    }
                }
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

    // Firebase 인증으로 유저 등록
    private void registerUserByFirebase() {
        editText_email.setEnabled(false);
        editText_password.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, getString(R.string.register_complete), Toast.LENGTH_SHORT)
                                    .show();
                            loginByFirebase();
                        }
                        else {
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
}

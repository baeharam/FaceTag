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

public class RegisterActivity extends AppCompatActivity {

    private String email, password;
    private Button register;
    private EditText editText_email, editText_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editText_email = findViewById(R.id.register_edittext_email);
        editText_password = findViewById(R.id.register_edittext_password);
        register = findViewById(R.id.register_button_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editText_email.getText().toString().trim();
                password = editText_password.getText().toString().trim();
                if(isCompleted()){
                    registerUser();
                }
            }
        });

    }

    // 비어있는 부분이 없는지 확인
    private boolean isCompleted(){
        if(TextUtils.isEmpty(email)){
            editText_email.setError("이메일을 입력하세요.");
            return false;
        } else if(TextUtils.isEmpty(password)){
            editText_password.setError("비밀번호를 입력하세요.");
            return false;
        } else if(password.length()<6){
            editText_password.setError("비밀번호는 6자리 이상입니다.");
            return false;
        }
        return true;
    }

    // Firebase 인증으로 유저 등록
    private void registerUser() {
        register.setEnabled(false);
        editText_email.setEnabled(false);
        editText_password.setEnabled(false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                        else {
                            register.setEnabled(true);
                            editText_email.setEnabled(true);
                            editText_password.setEnabled(true);
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthUserCollisionException e) {
                                editText_email.setError("이미 존재하는 사용자입니다.");
                                editText_email.requestFocus();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                editText_email.setError("이메일이 잘못된 형식입니다.");
                                editText_email.requestFocus();
                            } catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
}

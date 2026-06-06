package com.majorshare.core;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.majorshare.core.controller.AuthManager;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        authManager = AuthManager.getInstance();

        EditText etUserId = findViewById(R.id.etUserId);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnMoveToRegister = findViewById(R.id.btnMoveToRegister);

        btnLogin.setOnClickListener(v -> {
            String userId = etUserId.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Pattern.matches("^[a-zA-Z0-9]{4,12}$", userId)) {
                Toast.makeText(MainActivity.this, "아이디는 영문과 숫자 4~12자리로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean loginResult = authManager.login(MainActivity.this, userId, password);

            if (loginResult) {
                Toast.makeText(MainActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, MainWindowActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "로그인 실패: 정보를 확인해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnMoveToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
package com.majorshare.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.majorshare.core.controller.AuthManager;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Android 13 이상에서 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        authManager = AuthManager.getInstance();
        authManager.init(this);

        if (authManager.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, MainWindowActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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

            AuthManager.LoginResult result = authManager.login(MainActivity.this, userId, password);

            switch (result) {
                case SUCCESS:
                    Toast.makeText(MainActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, MainWindowActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case SUSPENDED:
                    Toast.makeText(MainActivity.this, "정지된 계정입니다. 관리자에게 문의하세요.", Toast.LENGTH_LONG).show();
                    break;
                case BANNED:
                    Toast.makeText(MainActivity.this, "영구 정지된 계정입니다. 접속이 불가능합니다.", Toast.LENGTH_LONG).show();
                    break;
                case WRONG_PASSWORD:
                    Toast.makeText(MainActivity.this, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case USER_NOT_FOUND:
                    Toast.makeText(MainActivity.this, "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        btnMoveToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
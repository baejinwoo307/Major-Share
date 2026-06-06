package com.majorshare.core;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.majorshare.core.controller.AuthManager;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authManager = AuthManager.getInstance();

        EditText etRegisterUserId = findViewById(R.id.etRegisterUserId);
        EditText etRegisterPassword = findViewById(R.id.etRegisterPassword);
        EditText etRegisterName = findViewById(R.id.etRegisterName);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String userId = etRegisterUserId.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();
            String name = etRegisterName.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Pattern.matches("^[a-zA-Z0-9]{4,12}$", userId)) {
                Toast.makeText(RegisterActivity.this, "아이디는 영문과 숫자 4~12자리로 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(RegisterActivity.this, "비밀번호는 6자리 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Pattern.matches("^[가-힣a-zA-Z]+$", name)) {
                Toast.makeText(RegisterActivity.this, "이름은 한글 또는 영문만 가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isRegistered = authManager.register(RegisterActivity.this, userId, password, name);

            if (isRegistered) {
                Toast.makeText(RegisterActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
package com.majorshare.core;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.majorshare.core.controller.AuthManager;
import com.majorshare.core.controller.ItemRepository;
import com.majorshare.core.domain.Item;
import com.majorshare.core.domain.User;

public class ItemRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_register);

        EditText etRegisterTitle = findViewById(R.id.etRegisterTitle);
        Spinner spinnerRegisterCategory = findViewById(R.id.spinnerRegisterCategory);
        RadioGroup rgTransactionType = findViewById(R.id.rgTransactionType);
        RadioButton rbSell = findViewById(R.id.rbSell);
        EditText etRegisterPrice = findViewById(R.id.etRegisterPrice);
        Button btnSubmitItem = findViewById(R.id.btnSubmitItem);

        String[] categories = {"전공 서적", "교양 서적", "기타 물품"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegisterCategory.setAdapter(adapter);

        btnSubmitItem.setOnClickListener(v -> {
            String title = etRegisterTitle.getText().toString().trim();
            String priceStr = etRegisterPrice.getText().toString().trim();

            if (title.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(ItemRegisterActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            String category = spinnerRegisterCategory.getSelectedItem().toString();
            String type = rbSell.isChecked() ? "판매" : "대여";

            User currentUser = AuthManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(ItemRegisterActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Item newItem = new Item(title, category, type, price, 0, currentUser);
            ItemRepository.getInstance().addItem(ItemRegisterActivity.this, newItem);

            Toast.makeText(ItemRegisterActivity.this, "물품이 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
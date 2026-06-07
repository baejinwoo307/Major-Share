package com.majorshare.core;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.majorshare.core.controller.AuthManager;
import com.majorshare.core.controller.ItemRepository;
import com.majorshare.core.domain.Item;
import com.majorshare.core.domain.User;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ItemRegisterActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 101;
    private ImageView ivItemPreview;
    private String selectedImageBase64 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_register);

        EditText etRegisterTitle = findViewById(R.id.etRegisterTitle);
        Spinner spinnerRegisterCategory = findViewById(R.id.spinnerRegisterCategory);
        RadioGroup rgTransactionType = findViewById(R.id.rgTransactionType);
        RadioButton rbSell = findViewById(R.id.rbSell);
        RadioButton rbRent = findViewById(R.id.rbRent);
        EditText etRegisterPrice = findViewById(R.id.etRegisterPrice);
        EditText etMaxRentDays = findViewById(R.id.etMaxRentDays);
        TextView tvRentalEndDate = findViewById(R.id.tvRentalEndDate);
        ivItemPreview = findViewById(R.id.ivItemPreview);
        Button btnSelectImage = findViewById(R.id.btnSelectImage);
        Button btnSubmitItem = findViewById(R.id.btnSubmitItem);

        String[] categories = {"전공 서적", "교양 서적", "기타 물품"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRegisterCategory.setAdapter(adapter);

        tvRentalEndDate.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String selectedDate = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvRentalEndDate.setText(selectedDate);
            }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        rgTransactionType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbRent) {
                tvRentalEndDate.setVisibility(View.VISIBLE);
                etMaxRentDays.setVisibility(View.VISIBLE);
            } else {
                tvRentalEndDate.setVisibility(View.GONE);
                tvRentalEndDate.setText("");
                etMaxRentDays.setVisibility(View.GONE);
            }
        });

        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "이미지 선택"), PICK_IMAGE_REQUEST);
        });

        btnSubmitItem.setOnClickListener(v -> {
            String title = etRegisterTitle.getText().toString().trim();
            String priceStr = etRegisterPrice.getText().toString().trim();

            if (title.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(ItemRegisterActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            String category = spinnerRegisterCategory.getSelectedItem().toString();
            boolean isRent = rbRent.isChecked();
            String type = isRent ? "대여" : "매매";

            String rentalEndDate = "";
            int maxRentDays = 7; // Default
            if (isRent) {
                rentalEndDate = tvRentalEndDate.getText().toString().trim();
                String maxDaysStr = etMaxRentDays.getText().toString().trim();
                if (rentalEndDate.isEmpty() || maxDaysStr.isEmpty()) {
                    Toast.makeText(ItemRegisterActivity.this, "대여 정보를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                maxRentDays = Integer.parseInt(maxDaysStr);
            }

            User currentUser = AuthManager.getInstance().getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(ItemRegisterActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // [설계서 보완] 물품 등록 전 계정 상태 실시간 최종 검증 (Sequence Diagram #3)
            AuthManager.getInstance().checkUserStatus(this);
            if (AuthManager.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "계정 상태가 변경되어 물품을 등록할 수 없습니다.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            Item newItem = new Item(title, category, type, price, rentalEndDate, currentUser);
            newItem.setMaxRentDays(maxRentDays);
            if (selectedImageBase64 != null) {
                newItem.setImageBase64(selectedImageBase64);
            }
            ItemRepository.getInstance().addItem(ItemRegisterActivity.this, newItem);

            Toast.makeText(ItemRegisterActivity.this, "물품이 성공적으로 등록되었습니다!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                ivItemPreview.setImageURI(imageUri);
                ivItemPreview.setVisibility(View.VISIBLE);
                selectedImageBase64 = uriToBase64(imageUri);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지를 로드하는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String uriToBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap == null) return null;

            int maxSize = 500;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxSize || height > maxSize) {
                float ratio = (float) width / (float) height;
                if (ratio > 1) {
                    width = maxSize;
                    height = (int) (maxSize / ratio);
                } else {
                    height = maxSize;
                    width = (int) (maxSize * ratio);
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
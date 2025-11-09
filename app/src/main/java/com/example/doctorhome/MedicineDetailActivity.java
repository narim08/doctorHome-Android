package com.example.doctorhome;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doctorhome.database.DatabaseHelper;
import com.example.doctorhome.model.Medicine;
import com.google.android.material.textfield.TextInputEditText;

public class MedicineDetailActivity extends AppCompatActivity { //상세, 수정, 삭제 화면

    private TextInputEditText etName, etExpiryDate, etQuantity, etUsage, etImageUrl;
    private DatabaseHelper dbHelper;
    private Medicine medicine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etQuantity = findViewById(R.id.etQuantity);
        etUsage = findViewById(R.id.etUsage);
        etImageUrl = findViewById(R.id.etImageUrl);
        Button btnUpdate = findViewById(R.id.btnUpdate);
        Button btnDelete = findViewById(R.id.btnDelete);

        //전달받은 약 정보 가져오기
        medicine = (Medicine) getIntent().getSerializableExtra("medicine");

        if (medicine != null) { //화면에 약 정보 표시
            etName.setText(medicine.getName());
            etExpiryDate.setText(medicine.getExpiryDate());
            etQuantity.setText(String.valueOf(medicine.getQuantity()));
            etUsage.setText(medicine.getMedicineUsage());
            etImageUrl.setText(medicine.getImageUrl());
        }

        //수정 버튼
        btnUpdate.setOnClickListener(v -> updateMedicine());

        //삭제 버튼
        btnDelete.setOnClickListener(v -> showDeleteDialog());
    }

    private void updateMedicine() {
        String name = etName.getText().toString().trim();
        String expiryDate = etExpiryDate.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String usage = etUsage.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "약 이름을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (expiryDate.isEmpty()) {
            Toast.makeText(this, "유통기한을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = 0;
        if (!quantityStr.isEmpty()) {
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "올바른 개수를 입력하세요", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //약 정보 업데이트
        medicine.setName(name);
        medicine.setExpiryDate(expiryDate);
        medicine.setQuantity(quantity);
        medicine.setMedicineUsage(usage);
        medicine.setImageUrl(imageUrl);

        int result = dbHelper.updateMedicine(medicine); //DB 업데이트

        if (result > 0) {
            Toast.makeText(this, "수정되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "수정 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteDialog() { //삭제
        new AlertDialog.Builder(this)
                .setTitle("삭제 확인")
                .setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    dbHelper.deleteMedicine(medicine.getId());
                    Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("취소", null)
                .show();
    }
}
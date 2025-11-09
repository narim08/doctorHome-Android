package com.example.doctorhome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.doctorhome.database.DatabaseHelper;
import com.example.doctorhome.model.Medicine;
import com.google.android.material.textfield.TextInputEditText;

public class AddMedicineActivity extends AppCompatActivity { //약 추가 화면

    private TextInputEditText etName, etExpiryDate, etQuantity, etUsage, etImageUrl;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etName);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etQuantity = findViewById(R.id.etQuantity);
        etUsage = findViewById(R.id.etUsage);
        etImageUrl = findViewById(R.id.etImageUrl);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> saveMedicine()); //저장 버튼
    }

    private void saveMedicine() {
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

        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setExpiryDate(expiryDate);
        medicine.setQuantity(quantity);
        medicine.setMedicineUsage(usage);
        medicine.setImageUrl(imageUrl);

        long result = dbHelper.addMedicine(medicine); //DB에 저장

        if (result > 0) {
            Toast.makeText(this, "약이 등록되었습니다", Toast.LENGTH_SHORT).show();
            finish(); // 현재 화면 종료하고 메인으로 돌아감
        } else {
            Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show();
        }
    }
}
package com.example.doctorhome;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorhome.adapter.MedicineAdapter;
import com.example.doctorhome.database.DatabaseHelper;
import com.example.doctorhome.model.Medicine;
import android.widget.Button;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMedicines;
    private MedicineAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvEmpty;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        //뷰 연결
        rvMedicines = findViewById(R.id.rvMedicines);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabAlarm = findViewById(R.id.fabAlarm);
        Button btnAIDiagnosis = findViewById(R.id.btnAIDiagnosis);
        Button btnFindHospital = findViewById(R.id.btnFindHospital);

        //갤러리 형식 -> 2열 그리드
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvMedicines.setLayoutManager(layoutManager);

        loadMedicines();

        //약 추가 버튼
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            startActivity(intent);
        });

        //알림 설정 버튼
        fabAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AlarmSettingActivity.class);
            startActivity(intent);
        });

        //AI 진단 버튼
        btnAIDiagnosis.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AIDiagnosisActivity.class);
            startActivity(intent);
        });

        //병원 찾기 버튼
        btnFindHospital.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HospitalFinderActivity.class);
            startActivity(intent);
        });

        //검색 기능
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchMedicines(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMedicines();
    }

    private void loadMedicines() {
        List<Medicine> medicineList = dbHelper.getAllMedicines();

        if (medicineList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvMedicines.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvMedicines.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new MedicineAdapter(this, medicineList);
                rvMedicines.setAdapter(adapter);
            } else {
                adapter.updateData(medicineList);
            }
        }
    }

    private void searchMedicines(String query) {
        if (query.isEmpty()) {
            loadMedicines();
        } else {
            List<Medicine> searchResults = dbHelper.searchMedicinesByName(query);
            if (adapter != null) {
                adapter.updateData(searchResults);
            }

            if (searchResults.isEmpty()) {
                tvEmpty.setText("검색 결과가 없습니다");
                tvEmpty.setVisibility(View.VISIBLE);
                rvMedicines.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rvMedicines.setVisibility(View.VISIBLE);
            }
        }
    }
}
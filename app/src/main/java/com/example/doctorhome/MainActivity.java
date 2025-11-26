package com.example.doctorhome;

import android.content.Intent;
import android.net.Uri;
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
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.util.Locale;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private RecyclerView rvMedicines;
    private MedicineAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView tvEmpty;
    private EditText etSearch;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 뷰 연결
        rvMedicines = findViewById(R.id.rvMedicines);
        tvEmpty = findViewById(R.id.tvEmpty);
        etSearch = findViewById(R.id.etSearch);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        FloatingActionButton fabAlarm = findViewById(R.id.fabAlarm);
        FloatingActionButton fabCalendar = findViewById(R.id.fabCalendar);
        FloatingActionButton fabCall = findViewById(R.id.fabCall);
        FloatingActionButton fabMessage = findViewById(R.id.fabMessage);
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

        //캘린더 버튼
        fabCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        });

        //전화 걸기 버튼
        fabCall.setOnClickListener(v -> {
            Uri uri = Uri.parse("tel:119");
            Intent intent = new Intent(Intent.ACTION_DIAL, uri); //암시적 인텐트
            startActivity(intent);
        });

        //메시지 보내기 버튼
        fabMessage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                sendEmergencySMS();
            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendEmergencySMS();
            } else {//권한 거부되면 주소 없이 메시지 보냄
                Toast.makeText(this, "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                sendEmergencySMSWithoutLocation();
            }
        }
    }

    private void sendEmergencySMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        String address = getAddressFromLocation(location);
                        String message = "응급 상황입니다. 주소는 " + address + "입니다.";

                        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                        smsIntent.setData(Uri.parse("smsto:119"));
                        smsIntent.putExtra("sms_body", message);
                        startActivity(smsIntent);
                    } else {
                        Toast.makeText(this, "현재 위치를 가져올 수 없습니다", Toast.LENGTH_SHORT).show();
                        sendEmergencySMSWithoutLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "위치 정보를 가져오는데 실패했습니다", Toast.LENGTH_SHORT).show();
                    sendEmergencySMSWithoutLocation();
                });
    }

    private void sendEmergencySMSWithoutLocation() {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:119"));
        smsIntent.putExtra("sms_body", "응급 상황입니다.");
        startActivity(smsIntent);
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );

            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //주소 변환 실패 시 좌표 반환
        return "위도: " + location.getLatitude() + ", 경도: " + location.getLongitude();
    }
}
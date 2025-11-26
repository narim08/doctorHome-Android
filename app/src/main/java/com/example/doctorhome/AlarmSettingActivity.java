package com.example.doctorhome;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorhome.adapter.AlarmAdapter;
import com.example.doctorhome.database.DatabaseHelper;
import com.example.doctorhome.model.MedicineAlarm;
import com.example.doctorhome.util.AlarmManagerHelper;
import java.util.List;
import java.util.Locale;

public class AlarmSettingActivity extends AppCompatActivity implements AlarmAdapter.OnAlarmClickListener {

    private EditText etMedicineName, etUsage;
    private TextView tvSelectedTime, tvEmptyAlarm;
    private Button btnSelectTime, btnSetAlarm;
    private RecyclerView rvAlarms;
    private AlarmAdapter adapter;
    private DatabaseHelper dbHelper;

    private String selectedTime = "";
    private MedicineAlarm editingAlarm = null;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this, "알림 권한이 허용되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "알림 권한이 거부되었습니다. 설정에서 권한을 허용해주세요", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);
        checkNotificationPermission();

        etMedicineName = findViewById(R.id.etMedicineName);
        etUsage = findViewById(R.id.etUsage);
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        tvEmptyAlarm = findViewById(R.id.tvEmptyAlarm);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        btnSetAlarm = findViewById(R.id.btnSetAlarm);
        rvAlarms = findViewById(R.id.rvAlarms);

        dbHelper = new DatabaseHelper(this);

        rvAlarms.setLayoutManager(new LinearLayoutManager(this));
        loadAlarms();

        btnSelectTime.setOnClickListener(v -> showTimePickerDialog());

        btnSetAlarm.setOnClickListener(v -> saveAlarm());
    }

    private void checkNotificationPermission() {
        //Android 13 (API 33) 이상에서만 런타임 권한 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void showTimePickerDialog() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = calendar.get(java.util.Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    tvSelectedTime.setText(selectedTime);
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void saveAlarm() {
        String medicineName = etMedicineName.getText().toString().trim();
        String usage = etUsage.getText().toString().trim();

        if (medicineName.isEmpty()) {
            Toast.makeText(this, "약 이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (usage.isEmpty()) {
            Toast.makeText(this, "복용법을 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "시간을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        MedicineAlarm alarm;
        if (editingAlarm != null) { //수정 모드
            alarm = editingAlarm;
            alarm.setMedicineName(medicineName);
            alarm.setUsage(usage);
            alarm.setTime(selectedTime);

            dbHelper.updateAlarm(alarm);
            AlarmManagerHelper.cancelAlarm(this, alarm.getId());
            AlarmManagerHelper.setAlarm(this, alarm);

            Toast.makeText(this, "알림이 수정되었습니다", Toast.LENGTH_SHORT).show();
            editingAlarm = null;
            btnSetAlarm.setText("알림 받기");
        } else { //추가 모드
            alarm = new MedicineAlarm();
            alarm.setMedicineName(medicineName);
            alarm.setUsage(usage);
            alarm.setTime(selectedTime);
            alarm.setActive(true);

            long id = dbHelper.addAlarm(alarm);
            alarm.setId(id);

            AlarmManagerHelper.setAlarm(this, alarm);

            Toast.makeText(this, "알림이 설정되었습니다", Toast.LENGTH_SHORT).show();
        }

        clearInputFields();
        loadAlarms();
    }

    private void clearInputFields() {
        etMedicineName.setText("");
        etUsage.setText("");
        tvSelectedTime.setText("시간 선택");
        selectedTime = "";
    }

    private void loadAlarms() {
        List<MedicineAlarm> alarmList = dbHelper.getAllAlarms();

        if (alarmList.isEmpty()) {
            tvEmptyAlarm.setVisibility(View.VISIBLE);
            rvAlarms.setVisibility(View.GONE);
        } else {
            tvEmptyAlarm.setVisibility(View.GONE);
            rvAlarms.setVisibility(View.VISIBLE);

            if (adapter == null) {
                adapter = new AlarmAdapter(this, alarmList, this);
                rvAlarms.setAdapter(adapter);
            } else {
                adapter.updateData(alarmList);
            }
        }
    }

    @Override
    public void onDeleteClick(MedicineAlarm alarm) {
        new AlertDialog.Builder(this)
                .setTitle("알림 삭제")
                .setMessage(alarm.getMedicineName() + " 알림을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    AlarmManagerHelper.cancelAlarm(this, alarm.getId());
                    dbHelper.deleteAlarm(alarm.getId());
                    Toast.makeText(this, "알림이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                    loadAlarms();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    @Override
    public void onItemClick(MedicineAlarm alarm) {
        editingAlarm = alarm;
        etMedicineName.setText(alarm.getMedicineName());
        etUsage.setText(alarm.getUsage());
        tvSelectedTime.setText(alarm.getTime());
        selectedTime = alarm.getTime();
        btnSetAlarm.setText("알림 수정");

        Toast.makeText(this, "수정 모드입니다", Toast.LENGTH_SHORT).show();
    }
}
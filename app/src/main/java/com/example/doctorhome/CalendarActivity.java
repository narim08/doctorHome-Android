package com.example.doctorhome;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorhome.adapter.ScheduleAdapter;
import com.example.doctorhome.database.DatabaseHelper;
import com.example.doctorhome.decorator.CompletedDecorator;
import com.example.doctorhome.decorator.ScheduledDecorator;
import com.example.doctorhome.decorator.TodayDecorator;
import com.example.doctorhome.model.MedicineIntake;
import com.example.doctorhome.model.MedicineSchedule;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private EditText etMedicineName;
    private CheckBox cbEveryday, cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday;
    private Button btnStartDate, btnEndDate, btnAddSchedule;
    private RecyclerView rvSchedules;
    private MaterialCalendarView calendarView;
    private DatabaseHelper dbHelper;
    private ScheduleAdapter scheduleAdapter;
    private List<MedicineSchedule> scheduleList;

    private ScheduledDecorator scheduledDecorator;
    private CompletedDecorator completedDecorator;

    private String startDate = "";
    private String endDate = "";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        dbHelper = new DatabaseHelper(this);

        //뷰 연결
        etMedicineName = findViewById(R.id.etMedicineName);
        cbEveryday = findViewById(R.id.cbEveryday);
        cbMonday = findViewById(R.id.cbMonday);
        cbTuesday = findViewById(R.id.cbTuesday);
        cbWednesday = findViewById(R.id.cbWednesday);
        cbThursday = findViewById(R.id.cbThursday);
        cbFriday = findViewById(R.id.cbFriday);
        cbSaturday = findViewById(R.id.cbSaturday);
        cbSunday = findViewById(R.id.cbSunday);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        btnAddSchedule = findViewById(R.id.btnAddSchedule);
        rvSchedules = findViewById(R.id.rvSchedules);
        calendarView = findViewById(R.id.calendarView);

        //오늘 날짜로 시작일 초기화
        startDate = dateFormat.format(new Date());
        btnStartDate.setText(startDate);

        rvSchedules.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadSchedules();
        setupCalendar();

        //매일 체크박스 클릭 시 다른 요일 체크박스 비활성화
        cbEveryday.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cbMonday.setChecked(false);
                cbTuesday.setChecked(false);
                cbWednesday.setChecked(false);
                cbThursday.setChecked(false);
                cbFriday.setChecked(false);
                cbSaturday.setChecked(false);
                cbSunday.setChecked(false);

                cbMonday.setEnabled(false);
                cbTuesday.setEnabled(false);
                cbWednesday.setEnabled(false);
                cbThursday.setEnabled(false);
                cbFriday.setEnabled(false);
                cbSaturday.setEnabled(false);
                cbSunday.setEnabled(false);
            } else {
                cbMonday.setEnabled(true);
                cbTuesday.setEnabled(true);
                cbWednesday.setEnabled(true);
                cbThursday.setEnabled(true);
                cbFriday.setEnabled(true);
                cbSaturday.setEnabled(true);
                cbSunday.setEnabled(true);
            }
        });

        btnStartDate.setOnClickListener(v -> showDatePicker(true));
        btnEndDate.setOnClickListener(v -> showDatePicker(false));
        btnAddSchedule.setOnClickListener(v -> addSchedule());

        //캘린더 날짜 선택
        calendarView.setOnDateChangedListener((widget, date, selected) -> {
            String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                    date.getYear(), date.getMonth(), date.getDay());
            showIntakeDialog(selectedDate);
        });
    }

    private void setupCalendar() {
        //데코레이터 초기화
        scheduledDecorator = new ScheduledDecorator(new HashSet<>());
        completedDecorator = new CompletedDecorator(new HashSet<>());

        calendarView.addDecorator(scheduledDecorator);
        calendarView.addDecorator(completedDecorator);
        calendarView.addDecorator(new TodayDecorator());

        updateCalendarDecorators();
    }

    private void updateCalendarDecorators() {
        HashSet<CalendarDay> scheduledDates = new HashSet<>();
        HashSet<CalendarDay> completedDates = new HashSet<>();

        List<MedicineSchedule> schedules = dbHelper.getAllSchedules();

        try {
            for (MedicineSchedule schedule : schedules) {
                Date start = dateFormat.parse(schedule.getStartDate());
                Date end = dateFormat.parse(schedule.getEndDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(start);

                while (!calendar.getTime().after(end)) {
                    String currentDate = dateFormat.format(calendar.getTime());
                    List<MedicineIntake> intakes = dbHelper.getIntakesByDate(currentDate);

                    if (!intakes.isEmpty()) {
                        CalendarDay calendarDay = CalendarDay.from(
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1, //Calendar.MONTH는 0-11이므로 +1
                                calendar.get(Calendar.DAY_OF_MONTH)
                        );

                        //모든 목표 일정이 완료되었는지 확인
                        boolean allCompleted = true;
                        for (MedicineIntake intake : intakes) {
                            if (!intake.isCompleted()) {
                                allCompleted = false;
                                break;
                            }
                        }

                        if (allCompleted) {
                            completedDates.add(calendarDay);
                        } else {
                            scheduledDates.add(calendarDay);
                        }
                    }

                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        scheduledDecorator.setDates(scheduledDates);
        completedDecorator.setDates(completedDates);

        calendarView.invalidateDecorators(); //캘린더 새로고침
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    if (isStartDate) {
                        startDate = date;
                        btnStartDate.setText(date);
                    } else {
                        endDate = date;
                        btnEndDate.setText(date);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void addSchedule() {
        String medicineName = etMedicineName.getText().toString().trim();

        if (medicineName.isEmpty()) {
            Toast.makeText(this, "약 이름을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endDate.isEmpty()) {
            Toast.makeText(this, "종료일을 선택하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String daysOfWeek;
        if (cbEveryday.isChecked()) {
            daysOfWeek = "매일";
        } else {
            List<String> days = new ArrayList<>();
            if (cbMonday.isChecked()) days.add("월");
            if (cbTuesday.isChecked()) days.add("화");
            if (cbWednesday.isChecked()) days.add("수");
            if (cbThursday.isChecked()) days.add("목");
            if (cbFriday.isChecked()) days.add("금");
            if (cbSaturday.isChecked()) days.add("토");
            if (cbSunday.isChecked()) days.add("일");

            if (days.isEmpty()) {
                Toast.makeText(this, "복용 요일을 선택하세요", Toast.LENGTH_SHORT).show();
                return;
            }

            daysOfWeek = String.join(",", days);
        }

        MedicineSchedule schedule = new MedicineSchedule();
        schedule.setMedicineName(medicineName);
        schedule.setDaysOfWeek(daysOfWeek);
        schedule.setStartDate(startDate);
        schedule.setEndDate(endDate);

        long scheduleId = dbHelper.addSchedule(schedule);

        if (scheduleId > 0) {
            //해당 기간 동안의 복용 날짜에 대한 Intake 레코드 생성
            createIntakeRecords(scheduleId, daysOfWeek, startDate, endDate);

            Toast.makeText(this, "스케줄이 추가되었습니다\n캘린더에서 날짜를 클릭하여 복용을 체크하세요", Toast.LENGTH_LONG).show();

            //입력 필드 초기화
            etMedicineName.setText("");
            cbEveryday.setChecked(false);
            cbMonday.setChecked(false);
            cbTuesday.setChecked(false);
            cbWednesday.setChecked(false);
            cbThursday.setChecked(false);
            cbFriday.setChecked(false);
            cbSaturday.setChecked(false);
            cbSunday.setChecked(false);
            endDate = "";
            btnEndDate.setText("종료일");

            loadSchedules();
            updateCalendarDecorators();
        } else {
            Toast.makeText(this, "스케줄 추가 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private void createIntakeRecords(long scheduleId, String daysOfWeek, String startDate, String endDate) {
        try {
            Date start = dateFormat.parse(startDate);
            Date end = dateFormat.parse(endDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);

            while (!calendar.getTime().after(end)) {
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                String currentDate = dateFormat.format(calendar.getTime());

                boolean shouldAddIntake = false;

                if (daysOfWeek.equals("매일")) {
                    shouldAddIntake = true;
                } else {
                    String[] days = daysOfWeek.split(",");
                    for (String day : days) {
                        if (matchesDayOfWeek(day, dayOfWeek)) {
                            shouldAddIntake = true;
                            break;
                        }
                    }
                }

                if (shouldAddIntake) {
                    MedicineIntake intake = new MedicineIntake();
                    intake.setScheduleId(scheduleId);
                    intake.setDate(currentDate);
                    intake.setCompleted(false);
                    dbHelper.addIntake(intake);
                }

                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean matchesDayOfWeek(String day, int calendarDay) {
        switch (day) {
            case "월": return calendarDay == Calendar.MONDAY;
            case "화": return calendarDay == Calendar.TUESDAY;
            case "수": return calendarDay == Calendar.WEDNESDAY;
            case "목": return calendarDay == Calendar.THURSDAY;
            case "금": return calendarDay == Calendar.FRIDAY;
            case "토": return calendarDay == Calendar.SATURDAY;
            case "일": return calendarDay == Calendar.SUNDAY;
            default: return false;
        }
    }

    private void loadSchedules() {
        scheduleList = dbHelper.getAllSchedules();

        if (scheduleAdapter == null) {
            scheduleAdapter = new ScheduleAdapter(this, scheduleList, schedule -> {
                new AlertDialog.Builder(this)
                        .setTitle("스케줄 삭제")
                        .setMessage("이 스케줄을 삭제하시겠습니까?")
                        .setPositiveButton("삭제", (dialog, which) -> {
                            dbHelper.deleteSchedule(schedule.getId());
                            loadSchedules();
                            updateCalendarDecorators();
                            Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("취소", null)
                        .show();
            });
            rvSchedules.setAdapter(scheduleAdapter);
        } else {
            scheduleAdapter.updateData(scheduleList);
        }
    }

    private void showIntakeDialog(String selectedDate) {
        List<MedicineIntake> intakes = dbHelper.getIntakesByDate(selectedDate);

        if (intakes.isEmpty()) {
            Toast.makeText(this, "이 날짜에 복용할 약이 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        //현재 복용 상태 확인
        boolean isAllCompleted = isAllIntakesCompletedForDate(selectedDate);
        String statusMessage = isAllCompleted ? " ✓ 완료" : " (미완료)";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(selectedDate + statusMessage);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        //상태 표시
        if (isAllCompleted) {
            layout.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            layout.setBackgroundColor(Color.parseColor("#E3F2FD"));
        }

        List<CheckBox> checkBoxes = new ArrayList<>();

        for (MedicineIntake intake : intakes) {
            MedicineSchedule schedule = dbHelper.getAllSchedules().stream()
                    .filter(s -> s.getId() == intake.getScheduleId())
                    .findFirst()
                    .orElse(null);

            if (schedule != null) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(schedule.getMedicineName());
                checkBox.setChecked(intake.isCompleted());
                checkBox.setTag(intake);
                checkBox.setTextSize(16);
                checkBox.setPadding(10, 10, 10, 10);
                checkBoxes.add(checkBox);
                layout.addView(checkBox);
            }
        }

        builder.setView(layout);
        builder.setPositiveButton("저장", (dialog, which) -> {
            boolean anyChanged = false;
            for (CheckBox checkBox : checkBoxes) {
                MedicineIntake intake = (MedicineIntake) checkBox.getTag();
                boolean newStatus = checkBox.isChecked();
                if (intake.isCompleted() != newStatus) {
                    anyChanged = true;
                    intake.setCompleted(newStatus);
                    dbHelper.updateIntake(intake);
                }
            }

            if (anyChanged) {
                boolean nowCompleted = isAllIntakesCompletedForDate(selectedDate);
                String message = nowCompleted ?
                        "저장되었습니다! 모든 약을 복용 완료했습니다 ✓" :
                        "저장되었습니다";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                // 캘린더 업데이트
                updateCalendarDecorators();
            } else {
                Toast.makeText(this, "변경사항이 없습니다", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("취소", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean isAllIntakesCompletedForDate(String date) {
        List<MedicineIntake> intakes = dbHelper.getIntakesByDate(date);
        if (intakes.isEmpty()) {
            return false;
        }

        for (MedicineIntake intake : intakes) {
            if (!intake.isCompleted()) {
                return false;
            }
        }
        return true;
    }
}
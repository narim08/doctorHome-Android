package com.example.doctorhome.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.doctorhome.model.Medicine;
import com.example.doctorhome.model.MedicineAlarm;
import com.example.doctorhome.model.MedicineSchedule;
import com.example.doctorhome.model.MedicineIntake;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DoctorHome.db";
    private static final int DATABASE_VERSION = 3; // 버전 업그레이드

    // Medicine 테이블
    private static final String TABLE_MEDICINE = "medicine";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EXPIRY_DATE = "expiry_date";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_MEDICINE_USAGE = "medicine_usage";
    private static final String COL_IMAGE_URL = "image_url";

    // Alarm 테이블
    private static final String TABLE_ALARM = "medicine_alarm";
    private static final String COL_ALARM_ID = "id";
    private static final String COL_ALARM_MEDICINE_NAME = "medicine_name";
    private static final String COL_ALARM_USAGE = "usage";
    private static final String COL_ALARM_TIME = "time";
    private static final String COL_ALARM_IS_ACTIVE = "is_active";

    // Schedule 테이블
    private static final String TABLE_SCHEDULE = "medicine_schedule";
    private static final String COL_SCHEDULE_ID = "id";
    private static final String COL_SCHEDULE_MEDICINE_NAME = "medicine_name";
    private static final String COL_SCHEDULE_DAYS_OF_WEEK = "days_of_week";
    private static final String COL_SCHEDULE_START_DATE = "start_date";
    private static final String COL_SCHEDULE_END_DATE = "end_date";

    // Intake 테이블
    private static final String TABLE_INTAKE = "medicine_intake";
    private static final String COL_INTAKE_ID = "id";
    private static final String COL_INTAKE_SCHEDULE_ID = "schedule_id";
    private static final String COL_INTAKE_DATE = "date";
    private static final String COL_INTAKE_COMPLETED = "completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Medicine 테이블 생성
        String CREATE_MEDICINE_TABLE = "CREATE TABLE " + TABLE_MEDICINE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_EXPIRY_DATE + " TEXT, "
                + COL_QUANTITY + " INTEGER, "
                + COL_MEDICINE_USAGE + " TEXT, "
                + COL_IMAGE_URL + " TEXT"
                + ")";
        db.execSQL(CREATE_MEDICINE_TABLE);

        // Alarm 테이블 생성
        String CREATE_ALARM_TABLE = "CREATE TABLE " + TABLE_ALARM + " ("
                + COL_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_ALARM_MEDICINE_NAME + " TEXT NOT NULL, "
                + COL_ALARM_USAGE + " TEXT, "
                + COL_ALARM_TIME + " TEXT NOT NULL, "
                + COL_ALARM_IS_ACTIVE + " INTEGER DEFAULT 1"
                + ")";
        db.execSQL(CREATE_ALARM_TABLE);

        // Schedule 테이블 생성
        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_SCHEDULE + " ("
                + COL_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_SCHEDULE_MEDICINE_NAME + " TEXT NOT NULL, "
                + COL_SCHEDULE_DAYS_OF_WEEK + " TEXT NOT NULL, "
                + COL_SCHEDULE_START_DATE + " TEXT NOT NULL, "
                + COL_SCHEDULE_END_DATE + " TEXT NOT NULL"
                + ")";
        db.execSQL(CREATE_SCHEDULE_TABLE);

        // Intake 테이블 생성
        String CREATE_INTAKE_TABLE = "CREATE TABLE " + TABLE_INTAKE + " ("
                + COL_INTAKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_INTAKE_SCHEDULE_ID + " INTEGER NOT NULL, "
                + COL_INTAKE_DATE + " TEXT NOT NULL, "
                + COL_INTAKE_COMPLETED + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + COL_INTAKE_SCHEDULE_ID + ") REFERENCES "
                + TABLE_SCHEDULE + "(" + COL_SCHEDULE_ID + ")"
                + ")";
        db.execSQL(CREATE_INTAKE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Alarm 테이블 추가
            String CREATE_ALARM_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ALARM + " ("
                    + COL_ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_ALARM_MEDICINE_NAME + " TEXT NOT NULL, "
                    + COL_ALARM_USAGE + " TEXT, "
                    + COL_ALARM_TIME + " TEXT NOT NULL, "
                    + COL_ALARM_IS_ACTIVE + " INTEGER DEFAULT 1"
                    + ")";
            db.execSQL(CREATE_ALARM_TABLE);
        }
        if (oldVersion < 3) {
            // Schedule 테이블 추가
            String CREATE_SCHEDULE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SCHEDULE + " ("
                    + COL_SCHEDULE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_SCHEDULE_MEDICINE_NAME + " TEXT NOT NULL, "
                    + COL_SCHEDULE_DAYS_OF_WEEK + " TEXT NOT NULL, "
                    + COL_SCHEDULE_START_DATE + " TEXT NOT NULL, "
                    + COL_SCHEDULE_END_DATE + " TEXT NOT NULL"
                    + ")";
            db.execSQL(CREATE_SCHEDULE_TABLE);

            // Intake 테이블 추가
            String CREATE_INTAKE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_INTAKE + " ("
                    + COL_INTAKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_INTAKE_SCHEDULE_ID + " INTEGER NOT NULL, "
                    + COL_INTAKE_DATE + " TEXT NOT NULL, "
                    + COL_INTAKE_COMPLETED + " INTEGER DEFAULT 0"
                    + ")";
            db.execSQL(CREATE_INTAKE_TABLE);
        }
    }

    /* Medicine CRUD */
    public long addMedicine(Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, medicine.getName());
        values.put(COL_EXPIRY_DATE, medicine.getExpiryDate());
        values.put(COL_QUANTITY, medicine.getQuantity());
        values.put(COL_MEDICINE_USAGE, medicine.getMedicineUsage());
        values.put(COL_IMAGE_URL, medicine.getImageUrl());

        long id = db.insert(TABLE_MEDICINE, null, values);
        db.close();
        return id;
    }

    public List<Medicine> getAllMedicines() {
        List<Medicine> medicineList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEDICINE + " ORDER BY " + COL_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Medicine medicine = new Medicine();
                medicine.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                medicine.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
                medicine.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)));
                medicine.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
                medicine.setMedicineUsage(cursor.getString(cursor.getColumnIndexOrThrow(COL_MEDICINE_USAGE)));
                medicine.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)));

                medicineList.add(medicine);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicineList;
    }

    public Medicine getMedicineById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICINE, null, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        Medicine medicine = null;
        if (cursor != null && cursor.moveToFirst()) {
            medicine = new Medicine();
            medicine.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
            medicine.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
            medicine.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)));
            medicine.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
            medicine.setMedicineUsage(cursor.getString(cursor.getColumnIndexOrThrow(COL_MEDICINE_USAGE)));
            medicine.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)));
            cursor.close();
        }

        db.close();
        return medicine;
    }

    public int updateMedicine(Medicine medicine) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAME, medicine.getName());
        values.put(COL_EXPIRY_DATE, medicine.getExpiryDate());
        values.put(COL_QUANTITY, medicine.getQuantity());
        values.put(COL_MEDICINE_USAGE, medicine.getMedicineUsage());
        values.put(COL_IMAGE_URL, medicine.getImageUrl());

        int rowsAffected = db.update(TABLE_MEDICINE, values, COL_ID + "=?",
                new String[]{String.valueOf(medicine.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteMedicine(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINE, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Medicine> searchMedicinesByName(String searchQuery) {
        List<Medicine> medicineList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_MEDICINE +
                " WHERE " + COL_NAME + " LIKE ? ORDER BY " + COL_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchQuery + "%"});

        if (cursor.moveToFirst()) {
            do {
                Medicine medicine = new Medicine();
                medicine.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
                medicine.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
                medicine.setExpiryDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_EXPIRY_DATE)));
                medicine.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
                medicine.setMedicineUsage(cursor.getString(cursor.getColumnIndexOrThrow(COL_MEDICINE_USAGE)));
                medicine.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGE_URL)));

                medicineList.add(medicine);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicineList;
    }

    /* Alarm CRUD */
    public long addAlarm(MedicineAlarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ALARM_MEDICINE_NAME, alarm.getMedicineName());
        values.put(COL_ALARM_USAGE, alarm.getUsage());
        values.put(COL_ALARM_TIME, alarm.getTime());
        values.put(COL_ALARM_IS_ACTIVE, alarm.isActive() ? 1 : 0);

        long id = db.insert(TABLE_ALARM, null, values);
        db.close();
        return id;
    }

    public List<MedicineAlarm> getAllAlarms() {
        List<MedicineAlarm> alarmList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ALARM + " ORDER BY " + COL_ALARM_TIME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MedicineAlarm alarm = new MedicineAlarm();
                alarm.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ALARM_ID)));
                alarm.setMedicineName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARM_MEDICINE_NAME)));
                alarm.setUsage(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARM_USAGE)));
                alarm.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARM_TIME)));
                alarm.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ALARM_IS_ACTIVE)) == 1);

                alarmList.add(alarm);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return alarmList;
    }

    public MedicineAlarm getAlarmById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARM, null, COL_ALARM_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);

        MedicineAlarm alarm = null;
        if (cursor != null && cursor.moveToFirst()) {
            alarm = new MedicineAlarm();
            alarm.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ALARM_ID)));
            alarm.setMedicineName(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARM_MEDICINE_NAME)));
            alarm.setUsage(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARM_USAGE)));
            alarm.setTime(cursor.getString(cursor.getColumnIndexOrThrow(COL_ALARM_TIME)));
            alarm.setActive(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ALARM_IS_ACTIVE)) == 1);
            cursor.close();
        }

        db.close();
        return alarm;
    }

    public int updateAlarm(MedicineAlarm alarm) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_ALARM_MEDICINE_NAME, alarm.getMedicineName());
        values.put(COL_ALARM_USAGE, alarm.getUsage());
        values.put(COL_ALARM_TIME, alarm.getTime());
        values.put(COL_ALARM_IS_ACTIVE, alarm.isActive() ? 1 : 0);

        int rowsAffected = db.update(TABLE_ALARM, values, COL_ALARM_ID + "=?",
                new String[]{String.valueOf(alarm.getId())});
        db.close();
        return rowsAffected;
    }

    public void deleteAlarm(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARM, COL_ALARM_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /* Schedule CRUD */
    public long addSchedule(MedicineSchedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_SCHEDULE_MEDICINE_NAME, schedule.getMedicineName());
        values.put(COL_SCHEDULE_DAYS_OF_WEEK, schedule.getDaysOfWeek());
        values.put(COL_SCHEDULE_START_DATE, schedule.getStartDate());
        values.put(COL_SCHEDULE_END_DATE, schedule.getEndDate());

        long id = db.insert(TABLE_SCHEDULE, null, values);
        db.close();
        return id;
    }

    public List<MedicineSchedule> getAllSchedules() {
        List<MedicineSchedule> scheduleList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SCHEDULE + " ORDER BY " + COL_SCHEDULE_START_DATE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                MedicineSchedule schedule = new MedicineSchedule();
                schedule.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_SCHEDULE_ID)));
                schedule.setMedicineName(cursor.getString(cursor.getColumnIndexOrThrow(COL_SCHEDULE_MEDICINE_NAME)));
                schedule.setDaysOfWeek(cursor.getString(cursor.getColumnIndexOrThrow(COL_SCHEDULE_DAYS_OF_WEEK)));
                schedule.setStartDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_SCHEDULE_START_DATE)));
                schedule.setEndDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_SCHEDULE_END_DATE)));

                scheduleList.add(schedule);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return scheduleList;
    }

    public void deleteSchedule(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // 스케줄 삭제 시 관련 복용 기록도 함께 삭제
        db.delete(TABLE_INTAKE, COL_INTAKE_SCHEDULE_ID + "=?", new String[]{String.valueOf(id)});
        db.delete(TABLE_SCHEDULE, COL_SCHEDULE_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    /* Intake CRUD */
    public long addIntake(MedicineIntake intake) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_INTAKE_SCHEDULE_ID, intake.getScheduleId());
        values.put(COL_INTAKE_DATE, intake.getDate());
        values.put(COL_INTAKE_COMPLETED, intake.isCompleted() ? 1 : 0);

        long id = db.insert(TABLE_INTAKE, null, values);
        db.close();
        return id;
    }

    public List<MedicineIntake> getIntakesByDate(String date) {
        List<MedicineIntake> intakeList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INTAKE, null, COL_INTAKE_DATE + "=?",
                new String[]{date}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                MedicineIntake intake = new MedicineIntake();
                intake.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_INTAKE_ID)));
                intake.setScheduleId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_INTAKE_SCHEDULE_ID)));
                intake.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_INTAKE_DATE)));
                intake.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTAKE_COMPLETED)) == 1);

                intakeList.add(intake);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return intakeList;
    }

    public int updateIntake(MedicineIntake intake) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_INTAKE_COMPLETED, intake.isCompleted() ? 1 : 0);

        int rowsAffected = db.update(TABLE_INTAKE, values, COL_INTAKE_ID + "=?",
                new String[]{String.valueOf(intake.getId())});
        db.close();
        return rowsAffected;
    }

    public MedicineIntake getIntakeByScheduleAndDate(long scheduleId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_INTAKE, null,
                COL_INTAKE_SCHEDULE_ID + "=? AND " + COL_INTAKE_DATE + "=?",
                new String[]{String.valueOf(scheduleId), date}, null, null, null);

        MedicineIntake intake = null;
        if (cursor != null && cursor.moveToFirst()) {
            intake = new MedicineIntake();
            intake.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_INTAKE_ID)));
            intake.setScheduleId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_INTAKE_SCHEDULE_ID)));
            intake.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_INTAKE_DATE)));
            intake.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTAKE_COMPLETED)) == 1);
            cursor.close();
        }

        db.close();
        return intake;
    }
}
package com.example.doctorhome.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.doctorhome.model.Medicine;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "DoctorHome.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MEDICINE = "medicine";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_EXPIRY_DATE = "expiry_date";
    private static final String COL_QUANTITY = "quantity";
    private static final String COL_MEDICINE_USAGE = "medicine_usage";
    private static final String COL_IMAGE_URL = "image_url";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_MEDICINE + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_EXPIRY_DATE + " TEXT, "
                + COL_QUANTITY + " INTEGER, "
                + COL_MEDICINE_USAGE + " TEXT, "
                + COL_IMAGE_URL + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINE);
        onCreate(db);
    }

    /* CRUD */
    //Create: 약 추가
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

    //Read: 약 전체 조회
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

    //Read: 약 상세 조회
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

    //Update: 약 수정
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

    //Delete: 약 삭제
    public void deleteMedicine(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEDICINE, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }


    //약 검색
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
}

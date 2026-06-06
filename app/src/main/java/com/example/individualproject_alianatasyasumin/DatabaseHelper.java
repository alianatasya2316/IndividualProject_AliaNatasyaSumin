package com.example.individualproject_alianatasyasumin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BillDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_BILLS = "bills";

    // Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_UNITS = "units";
    private static final String COLUMN_REBATE = "rebate";
    private static final String COLUMN_TOTAL = "total_charges";
    private static final String COLUMN_FINAL = "final_cost";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_BILLS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MONTH + " TEXT,"
                + COLUMN_UNITS + " REAL,"
                + COLUMN_REBATE + " REAL,"
                + COLUMN_TOTAL + " REAL,"
                + COLUMN_FINAL + " REAL" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        onCreate(db);
    }

    // Method to Insert Data
    public void addBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MONTH, bill.getMonth());
        values.put(COLUMN_UNITS, bill.getUnits());
        values.put(COLUMN_REBATE, bill.getRebate());
        values.put(COLUMN_TOTAL, bill.getTotalCharges());
        values.put(COLUMN_FINAL, bill.getFinalCost());
        db.insert(TABLE_BILLS, null, values);
        db.close();
    }

    // Method to Get All Data (for ListView)
    public List<Bill> getAllBills() {
        List<Bill> billList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BILLS, null);

        if (cursor.moveToFirst()) {
            do {
                Bill bill = new Bill(
                        cursor.getInt(0), cursor.getString(1),
                        cursor.getDouble(2), cursor.getDouble(3),
                        cursor.getDouble(4), cursor.getDouble(5));
                billList.add(bill);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return billList;
    }

    public int updateBill(int id, double units, double rebate, double total, double finalCost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("units", units);
        values.put("rebate", rebate);
        values.put("total_charges", total);
        values.put("final_cost", finalCost);

        // This updates the specific row where ID matches
        return db.update("bills", values, "id = ?", new String[]{String.valueOf(id)});
    }

    // Method to Delete Data
    public void deleteBill(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BILLS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
package com.example.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocDbHelper extends SQLiteOpenHelper {
    public static final String LOC_DB_NAME = "LOCATIONS.db";
    public static final String TABLE_NAME = "Locations";
    public static final String COL_1 = "LID";
    public static final String COL_2 = "Latitude";
    public static final String COL_3 = "Longitude";
    public LocDbHelper(@Nullable Context context) {
        super(context, LOC_DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase locdb) {
        locdb.execSQL("CREATE TABLE if not exists Locations" +
                "(LID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Latitude Double, " +
                "Longitude Double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase locdb, int oldVer, int newVer) {
        locdb.execSQL("DROP TABLE IF EXISTS Locations");
        onCreate(locdb);
    }

    public boolean insertData(String LID, double Latitude, double Longitude) {
        SQLiteDatabase locdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, LID);
        contentValues.put(COL_2, Latitude);
        contentValues.put(COL_3, Longitude);
        long result = locdb.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase locdb = this.getWritableDatabase();
        Cursor res = locdb.rawQuery("select * from Locations", null);
        return res;
    }

    public boolean updateData(Integer LID, double Latitude, double Longitude) {
        SQLiteDatabase locdb = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, LID);
        contentValues.put(COL_2, Latitude);
        contentValues.put(COL_3, Longitude);
        String whereClause = COL_1 + " = ?";
        String[] whereArgs = {LID.toString()};

        int rowsAffected = locdb.update(TABLE_NAME, contentValues, whereClause, whereArgs);
        return rowsAffected > 0;
    }

}
package com.example.SQLite;

import java.sql.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.SQLite.LocDbHelper;

public class TaskDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "TASKS.db";
    public static final String TABLE_NAME = "Tasks";
    public static final String COL_1 = "TaskID";
    public static final String COL_2 = "Title";
    public static final String COL_3 = "Description";
    public static final String COL_4 = "DueDate";
    public static final String COL_5 = "CreationDate";
    public static final String COL_6 = "CompletionStatus";
    public static final String COL_7 = "Progress";
    public static final String COL_8 = "LocationID";

    public TaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if(!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL("CREATE TABLE " + TABLE_NAME +
                    " (" +
                    COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_2 + " TEXT," +
                    COL_3 + " TEXT," +
                    COL_4 + " DATETIME," +
                    COL_5 + " DATETIME DEFAULT (datetime('now', 'localtime'))," +
                    COL_6 + " TEXT," +
                    COL_7 + " INTEGER," +
                    COL_8 + " INTEGER," +
                    " FOREIGN KEY (" + COL_8 + ") REFERENCES Locations(LID)" +
                    ");");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String Title, String Description, String DueDate, String CompletionStatus, int Progress, int LocationID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, Title);
        contentValues.put(COL_3, Description);
        contentValues.put(COL_4, DueDate);
        contentValues.put(COL_6, CompletionStatus);
        contentValues.put(COL_7, Progress);
        contentValues.put(COL_8, LocationID);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    public boolean updateData(int TaskID, String Title, String Description, String DueDate, String CompletionStatus, int Progress, int LocationID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, Title);
        contentValues.put(COL_3, Description);
        contentValues.put(COL_4, DueDate);
        contentValues.put(COL_6, CompletionStatus);
        contentValues.put(COL_7, Progress);
        contentValues.put(COL_8, LocationID);

        String whereClause = COL_1 + " = ?";
        String[] whereArgs = {String.valueOf(TaskID)};

        int rowsAffected = db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
        return rowsAffected > 0;
    }

}

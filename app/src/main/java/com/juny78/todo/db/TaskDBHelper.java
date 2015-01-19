package com.juny78.todo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.provider.BaseColumns;

public class TaskDBHelper extends SQLiteOpenHelper {

    public static final String TABLE = "tasks";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_DATE = "date";

    private static final String DB_NAME = "com.example.TodoList.db.tasks";
    private static final int DB_VERSION = 3;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table " + TABLE + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TASK + " text, "
            + COLUMN_STATUS + " text, "
            + COLUMN_DATE + " text)";

    private static final String DATABASE_DROP = "drop table " + TABLE + ";";

    public TaskDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.d("TaskDBHelper", "Create DB table: " + DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);

        Log.d("TaskDBHelper", "Upgrade DB table " + TABLE + " from version " + oldVersion + " to " + newVersion);
    }
}

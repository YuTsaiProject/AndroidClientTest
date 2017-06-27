package com.myandroid.androidclienttest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "socket.db";
    private static final String TABLE_LOG = "socket_log";
    private static final String COLUME_ID = "_id";
    private static final String COLUME_LOG= "socketLog";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_LOG + " ( " +
                COLUME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUME_LOG + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);
        onCreate(db);
    }
    public String databaseToString(){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_LOG + " WHERE 1";

        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            if(cursor.getString(cursor.getColumnIndex(COLUME_LOG))!= null){
                dbString += cursor.getString(cursor.getColumnIndex(COLUME_LOG));
                dbString += "\n";
            }
            cursor.moveToNext();
        }

        db.close();
        return dbString;
    }

    public void addLog(String newlog){
        ContentValues values = new ContentValues();
        values.put(COLUME_LOG, newlog);
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_LOG, null, values);
        db.close();
    }

    public void deleteLog(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("TRUNCATE TABLE " + TABLE_LOG +";" );
        db.close();
    }

}

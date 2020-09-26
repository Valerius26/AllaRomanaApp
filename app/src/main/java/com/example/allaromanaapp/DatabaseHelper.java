package com.example.allaromanaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Card.db";
    public static final String TABLE_NAME = "card_table";
    public static final String COL_1 = "USER_ID";
    public static final String COL_2 = "CARD_NUM";
    public static final String COL_3 = "PASSWORD";
    String createTabCard = "create table if not exists " + TABLE_NAME + " (USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, CARD_NUM TEXT, PASSWORD TEXT)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void insertCard(ContentValues contentValues){
        getWritableDatabase().insert(TABLE_NAME,"",contentValues);
    }

    public boolean isCorrectCard(String number, String password){
        String sql = "Select count(*) from " + TABLE_NAME + " where CARD_NUM='" + number +  "' and PASSWORD='" + password + "'";
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long l = statement.simpleQueryForLong();
        statement.close();

        if(l == 1)
            return true;
        else
            return false;
    }

    public boolean insertData(String number, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,number);
        contentValues.put(COL_3,password);
        long result = db.insert(TABLE_NAME, "", contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         db.execSQL(createTabCard);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);*/
    }
}

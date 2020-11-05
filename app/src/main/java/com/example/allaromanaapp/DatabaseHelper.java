package com.example.allaromanaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DbCrCard.db";
    public static final String TABLE_NAME = "card_table";
    public static final String COL_1 = "USER_ID";
    public static final String COL_2 = "CARD_NUM";
    public static final String COL_3 = "PASSWORD";
    public static final String COL_4 = "TYPE";
    public static final String COL_5 = "CREDIT";
    public static final String COL_6 = "EMAIL";
    String createTabCard = "create table if not exists " + TABLE_NAME + " (USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, CARD_NUM TEXT, PASSWORD TEXT, TYPE TEXT, CREDIT TEXT, EMAIL TEXT)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public void insertCard(ContentValues contentValues){
        getWritableDatabase().insert(TABLE_NAME,"",contentValues);
    }

    public boolean isCorrectCard(String number, String password, String cardType){
        String sql = "Select count(*) from " + TABLE_NAME + " where CARD_NUM='" + number +  "' and PASSWORD='" + password + "' and TYPE='" + cardType + "'" ;
        SQLiteStatement statement = getReadableDatabase().compileStatement(sql);
        long l = statement.simpleQueryForLong();
        statement.close();

        if(l == 1)
            return true;
        else
            return false;
    }

    public boolean insertData(String number, String password, String cardType, String credit, String email){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,number);
        contentValues.put(COL_3,password);
        contentValues.put(COL_4,cardType);
        contentValues.put(COL_5,credit);
        contentValues.put(COL_6,email);
        long result = db.insert(TABLE_NAME, "", contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean updateCreditinDB(String toPay, String card_num){
        NumberFormat nf = NumberFormat.getInstance();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select CREDIT from " + TABLE_NAME + " where CARD_NUM='" + card_num + "'";
        Cursor cursor = db.rawQuery(sql,null);
        String credit = "";
        if(cursor.moveToFirst()){
            credit = cursor.getString(0);
        }
        Double value = Double.valueOf(0);
        try {
            value = nf.parse(credit).doubleValue();
            value = value - nf.parse(toPay).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(value < 0){
            return false;
        }else{

            String finalValue = String.format("%.2f",value);
            String sql2 = "UPDATE "+TABLE_NAME +" SET " + COL_5+ " = '"+finalValue+"' WHERE "+COL_2+ " = "+card_num;
            db.execSQL(sql2);
        }

        return true;
    }


    public boolean updateCreditCreditorDB(String toPay, String email){
        NumberFormat nf = NumberFormat.getInstance();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select CREDIT from " + TABLE_NAME + " where EMAIL='" + email + "'";
        Cursor cursor = db.rawQuery(sql,null);
        String credit = "";
        if(cursor.moveToFirst()){
            credit = cursor.getString(0);
        }
        Double value = Double.valueOf(0);
        try {
            value = nf.parse(credit).doubleValue();
            value = value + nf.parse(toPay).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String finalValue = String.format("%.2f",value);
        String sql2 = "UPDATE "+TABLE_NAME +" SET " + COL_5+ " = '"+finalValue+"' WHERE "+COL_6+ " = '"+email+"'";
        db.execSQL(sql2);

        return true;
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
package com.example.smartlocker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class sqlHelper extends SQLiteOpenHelper {
    public sqlHelper( Context context) {
        super(context, "mydb", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = "CREATE TABLE checkedAppInfo(_id INTEGER PRIMARY KEY AUTOINCREMENT, AppName TEXT,PackageName TEXT)";
        db.execSQL(sqlQuery);
        String sqlQuery2 = "CREATE TABLE pinDetails(_id INTEGER PRIMARY KEY AUTOINCREMENT, pin TEXT,phoneNo TEXT)";
        db.execSQL(sqlQuery2);
    }

    public boolean insertPin(String pin1,String pNo)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT *  from pinDetails WHERE phoneNo=?",new String[]{pNo});
        if (cursor.getCount()>0){
            ContentValues values = new ContentValues();
            values.put("pin", pin1);
            long rzlt = db.update("pinDetails",values,"phoneNo=?",new String[]{pNo});
            if (rzlt == -1) {
                return false;
            } else {
                return true;
            }
        }
        else {
            ContentValues values = new ContentValues();
            values.put("pin", pin1);
            values.put("phoneNo", pNo);
            long rzlt = db.insert("pinDetails", null, values);
            if (rzlt == -1) {
                return false;
            } else {
                return true;
            }
        }
    }
    public String getPin(){
        String p="";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  pinDetails",new String[]{});
        if (cursor.getCount()>0) {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {
                p = cursor.getString(1);
            }
            while (cursor.moveToNext());
        }
        return p;
    }

    public String getPhoneNo(String pNo)
    {
        String p="";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM  pinDetails WHERE phoneNo =?",new String[]{pNo});
        if (cursor.getCount()>0) {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {
                p = cursor.getString(2);
            }
            while (cursor.moveToNext());
        }
        return p;
    }

    boolean insertCheckedApps(String appName,String packageName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT *  from checkedAppInfo WHERE AppName=?",new String[]{appName});
        if (cursor.getCount()>0){
        }
        else {
            ContentValues values = new ContentValues();
            values.put("AppName", appName);
            values.put("PackageName", packageName);
            long rzlt = db.insert("checkedAppInfo", null, values);
            if (rzlt == -1) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    public StringBuilder displayApp(String packageName)
    {
       // String aName="";
        String pName="";
        StringBuilder sb = new StringBuilder();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM checkedAppInfo WHERE PackageName=?",new String[]{packageName});
        if (cursor.getCount()>0) {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do {
                // aName = cursor.getString(1);
                pName = cursor.getString(2);
                sb.append(pName);
            }
            while (cursor.moveToNext());
        }
        return sb;
    }

    boolean deleteApps(String appName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT *  from checkedAppInfo WHERE AppName=? ",new String[]{appName});
        if (cursor.getCount()>0){
            long rzlt = db.delete("checkedAppInfo","AppName=?",new String[]{appName});
            if (rzlt==-1){
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}

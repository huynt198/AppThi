package com.example.thi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {
    //tạo bảng database
    private static final String NAME = "users.db";
    private static final Integer VER = 1;
//    câu truy vấn tạo người dùng
    private static String creteuser = "CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,email TEXT,pass TEXT,token TEXT)";

    public UserHelper(Context context) {
        super(context, NAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //thực thi câu truy vần khi tạo lần đầu tiên
        db.execSQL(creteuser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getUser() {
        return getReadableDatabase().rawQuery("SELECT * FROM users", null);
    }

    public void insert(User u) {
        //thêm 1 user
        ContentValues c = new ContentValues();
        c.put("name", u.getName());
        c.put("token", u.getToken());
        c.put("email", u.getEmail());

        getWritableDatabase().insert("users", "token", c);
    }

    public Boolean delete(String email) {
        //xóa user khi người dùng logout
        return getWritableDatabase().delete("users", "email='"+email+"'", null)>0;
    }
}

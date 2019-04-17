package com.example.thi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHelper extends SQLiteOpenHelper {
    private static final String NAME = "users.db";
    private static final Integer VER = 1;
    private static String creteuser = "CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,email TEXT,pass TEXT,token TEXT)";

    public UserHelper(Context context) {
        super(context, NAME, null, VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(creteuser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Cursor getUser() {
        return getReadableDatabase().rawQuery("SELECT * FROM users", null);
    }

    public void insert(User u) {
        ContentValues c = new ContentValues();
        c.put("name", u.getName());
        c.put("token", u.getToken());
        c.put("email", u.getEmail());

        getWritableDatabase().insert("users", "token", c);
    }

    public int delete(String email) {
//        String sql="DELETE FROM users WHERE email='"+email+"'";
        return getWritableDatabase().delete("users", "email='"+email+"'", null);
    }
}

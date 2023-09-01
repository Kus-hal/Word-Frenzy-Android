package com.example.wordfrenzy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class WordDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordData";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "words";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "word";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT);";

    public WordDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    ArrayList<String> findSimilar(String input){
        //String query = "SELECT word FROM dictionary WHERE word LIKE 'input%';";
        ArrayList<String> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAME+" LIKE '"+input+"%';",null);

        while(cursor.moveToNext()){
            String model = cursor.getString(1);
            res.add(model);
        }

        cursor.close();
        db.close();

        return res;
    }
    public long addWord(String word){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,word);
        return db.insert(TABLE_NAME,null,values);
    }

    public boolean isPresent(String input){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_NAME+" = ?",new String[] {input});
        return cursor.moveToNext();
    }
}
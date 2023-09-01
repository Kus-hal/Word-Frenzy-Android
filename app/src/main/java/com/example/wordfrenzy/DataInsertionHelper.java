package com.example.wordfrenzy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataInsertionHelper {

    public static void insertDataFromTextFile(Context context) {
        WordDatabase dbHelper = new WordDatabase(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        InputStream inputStream = context.getResources().openRawResource(R.raw.all_words);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                ContentValues values = new ContentValues();
                values.put(WordDatabase.COLUMN_NAME, line);

                db.insert(WordDatabase.TABLE_NAME, null, values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        db.close();
    }

//    public static boolean alreadyExists(Context context){
//        WordDatabase word = new WordDatabase(context);
//        SQLiteDatabase db = word.getWritableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM "+WordDatabase.TABLE_NAME+" LIMIT 10000",null);
//        int i=0;
//        while(cursor.moveToNext()){
//            i++;
//        }
//        cursor.close();
//        db.close();
//        return i == 10000;
//    }
}
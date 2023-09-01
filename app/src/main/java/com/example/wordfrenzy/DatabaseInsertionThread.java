package com.example.wordfrenzy;


   import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

    public class DatabaseInsertionThread extends Thread {

        private Context context;

        public DatabaseInsertionThread(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            // Perform database insertion logic here
            // After each entry is inserted, you can update a counter or flag
            // to track the progress of database population
            DataInsertionHelper.insertDataFromTextFile(context);

        }
    }

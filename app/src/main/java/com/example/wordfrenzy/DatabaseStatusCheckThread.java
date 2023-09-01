package com.example.wordfrenzy;


import android.content.Context;
import android.content.Intent;

import com.airbnb.lottie.LottieAnimationView;

public class DatabaseStatusCheckThread extends Thread {
        private Context context;
        private DatabaseInsertionThread insertionThread;

        public DatabaseStatusCheckThread(Context context , DatabaseInsertionThread insertionThread) {
            this.insertionThread = insertionThread;
            this.context = context;
        }

        @Override
        public void run() {
            while (insertionThread.isAlive()) {
                // Keep checking if the insertion thread is still running
                try {
                    Thread.sleep(1000);


                    // Wait for 1 second before checking again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Once the insertion thread is done, navigate to the main page
            ((splash)context).stopText();
            ((splash)context).callMain();

        }


    }


package com.example.wordfrenzy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import org.w3c.dom.Text;

public class splash extends AppCompatActivity {
    boolean isLightTheme;
    TypedValue typedValue;
    LottieAnimationView loadingview, animationView;
    TextView loadingTextView;
    private DatabaseInsertionThread insertionThread;
    private DatabaseStatusCheckThread statusCheckThread;
    private Handler handler = new Handler();
    int currentMessageIndex = 0;
    private String[] loadingMessages = {"...Preparing your experience...",
            "...Creating the magic...",
            "...Almost there...",
            "...Setting things up for you...",
            "...Crafting your journey...",
            "...Loading wonders...",
            "...Connecting the dots...",
            "...Assembling the adventure...",
            "...Unraveling mysteries...",
            "...Polishing the details..."};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // FOR FULLSCREEN

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(uiOptions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
//         --Initialization--
        loadingTextView = findViewById(R.id.loadingText);
        animationView = findViewById(R.id.Bgm);
        loadingview = findViewById(R.id.loading);
        typedValue = new TypedValue();
        isLightTheme = getTheme().resolveAttribute(android.R.attr.isLightTheme, typedValue, true);


        //   --CHECKING THE FIRST LAUNCH--
        SharedPreferences prefs = getSharedPreferences("Launch", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);


        Log.d("isFirstLaunch", "" + isFirstLaunch);
        if (isFirstLaunch) {
            //   ---Loading animation
            startLoadAnimation();
            startDatabaseInsertionThread();
            startStatusCheckThread();
            handler.postDelayed(updateMessageRunnable, 2000);


        } else {
            loadingview.setVisibility(View.GONE);
            Frenzyanimation();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    callMain();
                }
            }, 4000);
        }

    }

    //  Methods for Database
    private void startDatabaseInsertionThread() {
        insertionThread = new DatabaseInsertionThread(this);
        insertionThread.start();
    }

    private void startStatusCheckThread() {
        statusCheckThread = new DatabaseStatusCheckThread(this, insertionThread);
        statusCheckThread.start();
    }

    private void stopStatusCheckThread() {
        if (statusCheckThread != null) {
            statusCheckThread.interrupt();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopStatusCheckThread();
        handler.removeCallbacks(updateMessageRunnable);
    }

    //              --To call the MAIN_ACTIVITY AFTER COMPLETION OF DATABASE--
    public void callMain() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    //                  --TO REPEAT AND CHANGE LOADING TEXT--
    private Runnable updateMessageRunnable = new Runnable() {
        @Override
        public void run() {
            loadingTextView = findViewById(R.id.loadingText);
            loadingTextView.setText(loadingMessages[currentMessageIndex]);
            currentMessageIndex = (currentMessageIndex + 1) % loadingMessages.length;
            handler.postDelayed(this, 6000);
        }
    };

    //                    --TO STOP THE LOADING TEXT FROM REPEATATION--
    public void stopText() {
        handler.removeCallbacks(updateMessageRunnable);
    }

    //                  TO REPEAT THE LOADING ANIMATION
    public void startLoadAnimation() {
        loadingview.setAnimation(R.raw.loading);
        loadingview.playAnimation();
        loadingview.setRepeatCount(40);
    }

    public void Frenzyanimation() {
        if (typedValue.data == 0)
            animationView.setAnimation(R.raw.wordfrenzydark);
        else
        animationView.setAnimation(R.raw.wordfrenzylight);

        animationView.playAnimation();
    }

}
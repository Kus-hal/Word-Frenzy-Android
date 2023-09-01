package com.example.wordfrenzy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private EditText editSearch;
    private Button btnSearch, btnAddWord;
    private ListView listWords;
    ArrayList<String> arr;
    private Map<Character, Set<String>> wordMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.WF);
        editSearch = findViewById(R.id.editSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnAddWord = findViewById(R.id.btnAddWord);
        listWords = findViewById(R.id.listWords);


        // FOR FULLSCREEN
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(uiOptions);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        //Threads

        SharedPreferences prefs = getSharedPreferences("Launch", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstLaunch", false);
        editor.apply();

        arr = new ArrayList<>();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arr);
        listWords.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editSearch.clearFocus();
                arr.clear();
                String input = editSearch.getText().toString().trim();
                if (input.contains(" ")) {
                    Toast.makeText(MainActivity.this, "Please Remove the Space!", Toast.LENGTH_SHORT).show();
                } else {
                    ArrayList<String> res = (ArrayList<String>) generateUnscrambledWords(input);
                    arr.addAll(res);
                    if (res.size() == 1) {
                        Toast.makeText(MainActivity.this, res.size() + " word Found", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, res.size() + " words Found", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });

        btnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_word);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                EditText editAddWord = dialog.findViewById(R.id.editAddWord);
                Button btnAddWord = dialog.findViewById(R.id.btnAddWord);
                editAddWord.setText(editSearch.getText().toString());
                editAddWord.requestFocus();

                btnAddWord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WordDatabase database = new WordDatabase(MainActivity.this);
                        if (!editAddWord.getText().toString().matches("[a-zA-Z ]+")) {
                            editAddWord.setError("No numbers allowed");
                        } else if (database.isPresent(editAddWord.getText().toString().toLowerCase())) {
                            Toast.makeText(MainActivity.this, "Word already present", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            long res = database.addWord(editAddWord.getText().toString().toLowerCase());
                            Toast.makeText(MainActivity.this, res + " Word Added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    private List<String> generateUnscrambledWords(String input) {
        List<String> unscrambledWords = new ArrayList<>();
        Set<Character> usedChars = new HashSet<>();

        for (char c : input.toCharArray()) {
            if (usedChars.contains(c)) {
                continue;
            }
            usedChars.add(c);

            Set<String> words = wordMap.get(c);
            if (words == null) {
                words = getWordsStartingWith(c);
                wordMap.put(c, words);
            }

            List<String> combos = new ArrayList<>();
            generateCombos("", input, combos);

            for (String combo : combos) {
                if (words.contains(combo)) {
                    unscrambledWords.add(combo);
                }
            }
        }
        return unscrambledWords;
    }

    private Set<String> getWordsStartingWith(char c) {
        WordDatabase database = new WordDatabase(MainActivity.this);
        ArrayList<String> arr = database.findSimilar("" + c);
        Log.d("Words", "" + arr.size());
        return new HashSet<>(arr);
    }

    private void generateCombos(String prefix, String remaining, List<String> unscrambledWords) {
        if (remaining.length() == 0) {
            checkWord(prefix, unscrambledWords);
        } else {
            for (int i = 0; i < remaining.length(); i++) {
                String newPrefix = prefix + remaining.charAt(i);
                String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
                generateCombos(newPrefix, newRemaining, unscrambledWords);
            }
        }
    }

    private void checkWord(String word, List<String> unscrambledWords) {
        if (word.length() > 1) {
            Set<String> initialWords = wordMap.get(word.charAt(0));
            if (initialWords == null) {
                initialWords = getWordsStartingWith(word.charAt(0));
                wordMap.put(word.charAt(0), initialWords);
            }
            if (initialWords.contains(word) && !unscrambledWords.contains(word)) {
                unscrambledWords.add(word);
            }
        }
    }

}
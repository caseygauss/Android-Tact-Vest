package com.bhaptics.bhapticsandroid.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.bhaptics.bhapticsandroid.BhapticsModule;
import com.bhaptics.bhapticsandroid.CSVLoader;
import com.bhaptics.bhapticsandroid.HapticPattern;
import com.bhaptics.bhapticsandroid.HapticsProcessor;
import com.bhaptics.bhapticsandroid.R;
import com.bhaptics.bhapticsmanger.HapticPlayer;
import com.bhaptics.commons.model.DotPoint;
import com.bhaptics.commons.model.PositionType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LanguageActivity extends Activity implements View.OnClickListener {
    public static final String TAG = CompassActivity.class.getSimpleName();

    public static Map<String, String> phonemesMap;
    public static Map<String, HapticPattern> hapticsMap;
    public static Map<Integer, String> trainingMap;
    WorkManager workManager;

    Button submitButton, guess1, guess2, guess3, genRandom, backButton;
    EditText messageToRead;
    EditText wpm;

    public static TextView phoneView;
    public static TextView individualPhoneView;

    public static String languageQueue = "languageQueue";

    public Integer correctButton;
    public Integer currentStreak = 0;
    public String correctAnswer;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonetics);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

        submitButton = findViewById(R.id.submit_button);
        messageToRead = findViewById(R.id.text_to_read);
        wpm = findViewById(R.id.wpm_input);
        genRandom = findViewById(R.id.random_word_button);
        guess1 = findViewById(R.id.guess_1);
        guess2 = findViewById(R.id.guess_2);
        guess3 = findViewById(R.id.guess_3);
        phoneView = findViewById(R.id.phone_view);
        individualPhoneView = findViewById(R.id.individual_phone);

        CSVLoader loader = new CSVLoader();
        loader.setPhonemesMap();
        phonemesMap = CSVLoader.phonemesMap;
        hapticsMap = CSVLoader.hapticsMap;
        trainingMap = CSVLoader.trainingMap;
    }

    public void processMessage(View view){
        Log.i("CSV", "The message to read is: " +messageToRead.getText().toString());
        phonemesMap = CSVLoader.phonemesMap;
        hapticsMap = CSVLoader.hapticsMap;
        trainingMap = CSVLoader.trainingMap;

        String returnedReply = messageToPhonemes(messageToRead.getText().toString());
        individualPhoneView.setText(returnedReply);

        hideKeyboard();
    }

    public String messageToPhonemes(String message){
        String[] words = message.split(" ");
        workManager = WorkManager.getInstance(this);

        Log.i("Live", "messageToPhonemes "+message);

        String combinedPhrase = "";
        for(String word : words) {
            String bigWord = word.toUpperCase();
            try {
                String phoneticPhrase = phonemesMap.get(bigWord);
                String[] phones = phoneticPhrase.split(" ");
                Integer wordLength = phones.length;
                Integer place = wordLength;

                combinedPhrase += phoneticPhrase + " ";


                Log.i("CSV", "The phonetic message is: " + phoneticPhrase);

                for (int i = 0; i < phones.length; i++) {
                    Log.i("CSV", "The inner phone is: " + phones[i]);
                    Data.Builder builder = new Data.Builder();
                    builder.putString("phone", phones[i]);
                    builder.putInt("place", place);
                    builder.putInt("readRate", 10);

                    Integer speed = Integer.valueOf(wpm.getText().toString());
                    if(i == 0){
                        speed += 100;
                    }

                    OneTimeWorkRequest.Builder work = new OneTimeWorkRequest.Builder(HapticsProcessor.class);
                    work.setInputData(builder.build());
                    work.setInitialDelay(speed, TimeUnit.MILLISECONDS);
                    workManager.beginUniqueWork(languageQueue, ExistingWorkPolicy.APPEND, work.build()).enqueue();
                    place--;
                }
            }catch (Exception e){
                Log.i("Speech", "No match for word " + bigWord+ e.getMessage());
            }


        }
        return combinedPhrase;

    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void setPhoneView(String phone){
        //phoneView.setText(phone);
    }

    public void generateRandomWordGame(View view){
        int randomA = (int)(Math.random() * 100 + 1);
        Log.i("CSV", "New term is "+randomA);
        Log.i("CSV", "Is there a key at " + trainingMap.containsKey(randomA));
        String answer = trainingMap.get(randomA);

        correctAnswer = answer.toUpperCase();

        int randomB = (int)(Math.random() * 100 + 1);
        int randomC = (int)(Math.random() * 100 + 1);

        String wrongAnswer1 = trainingMap.get(randomB);
        String wrongAnswer2 = trainingMap.get(randomC);

        int randB = (int)(Math.random()* 3 + 1);
        correctButton = randB;
        if(randB == 1){
            guess1.setText(correctAnswer);
            guess2.setText(wrongAnswer1.toUpperCase());
            guess3.setText(wrongAnswer2.toUpperCase());
        }else if(randB == 2){
            guess2.setText(correctAnswer);
            guess1.setText(wrongAnswer1.toUpperCase());
            guess3.setText(wrongAnswer2.toUpperCase());
        }else{
            guess3.setText(correctAnswer);
            guess1.setText(wrongAnswer1.toUpperCase());
            guess2.setText(wrongAnswer2.toUpperCase());
        }
        individualPhoneView.setTextColor(Color.parseColor("#ffffff"));
        String returnedReply = messageToPhonemes(correctAnswer);
        individualPhoneView.setText(returnedReply);
    }

    public void checkAnswer(View view){
        Integer selectedInt = 0;
        if (view.getId() == R.id.guess_1) {
            selectedInt = 1;
        }else if(view.getId() == R.id.guess_2){
            selectedInt = 2;
        }else if(view.getId() == R.id.guess_3){
            selectedInt = 3;
        }
        if (selectedInt == correctButton){
            phoneView.setText("Correct! Woohoo!");
            currentStreak++;
        }else{
            currentStreak = 0;
            phoneView.setText("Try Again :(");
        }
    }
    public void repeatRandom(View view){
        messageToPhonemes(correctAnswer);
    }
    public void revealPhonemes(View view){
        individualPhoneView.setTextColor(Color.parseColor("#000000"));
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}

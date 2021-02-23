package com.bhaptics.bhapticsandroid;

import android.os.Build;
import android.os.FileUtils;
import android.util.Log;
import android.content.Context;

import androidx.annotation.RequiresApi;

import com.bhaptics.bhapticsandroid.activities.AmplifyApp;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVLoader {
    public static Map<String,String> phonemesMap;
    public static Map<String, HapticPattern> hapticsMap;
    public static Map<Integer, String> trainingMap;
    public static Integer readRate;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String setPhonemesMap(){
        try {
            Context context = AmplifyApp.context;
            File outputDir = context.getCacheDir();
            File tempFile = File.createTempFile("prefix", "suffix", outputDir);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            FileUtils.copy(context.getResources().openRawResource(R.raw.phonemesraw), outputStream);

            //use Tab separated list as there is a formatting error with commas
            List<Phoneme> beans = new CsvToBeanBuilder(new FileReader(tempFile)).withSeparator('\t').withType(Phoneme.class).build().parse();
            createMap(beans);

            setHapticCoordinates();
            return "Success";
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CSV Error", "The specified file was not found with error: " + e.getMessage());
            return "Error";
        }
    }

    public static final Map<String, String> createMap(List<Phoneme> beans){
        Map<String,String> resultMap = new HashMap<>();
        beans.forEach(phone -> resultMap.put(phone.getWord(), phone.getPhoneme()));
        phonemesMap = Collections.unmodifiableMap(resultMap);
        return phonemesMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void setHapticCoordinates(){
        try {
            Context context = AmplifyApp.context;
            File outputDir = context.getCacheDir();
            File tempFile = File.createTempFile("prefix1", "suffix1", outputDir);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            FileUtils.copy(context.getResources().openRawResource(R.raw.phoneticpatterndot), outputStream);

            List<HapticPattern> beans = new CsvToBeanBuilder(new FileReader(tempFile)).withSeparator('\t').withType(HapticPattern.class).build().parse();

            createHMap(beans);

            HapticPattern haptix = hapticsMap.get("K");
            Log.i("CSV", "Haptics pattern for K is  : "+ haptix.getxCo() + ","+haptix.getyCo() +"; direction: "+haptix.getDirection());

            setTrainingMap();
            Log.i("CSV", "Does training map have value at 5 like: " + trainingMap.get(2));
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CSV Error", "The specified file was not found with error: " + e.getMessage());
        }
    }
    public static final Map<String, HapticPattern> createHMap(List<HapticPattern> beans){
        Map<String,HapticPattern> resultMap = new HashMap<>();
        beans.forEach(phone -> resultMap.put(phone.getPhone(), phone));
        hapticsMap = Collections.unmodifiableMap(resultMap);
        return hapticsMap;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static final Map<Integer, String> setTrainingMap(){
        try {
            Context context = AmplifyApp.context;
            File outputDir = context.getCacheDir();
            File tempFile = File.createTempFile("prefix1", "suffix1", outputDir);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            FileUtils.copy(context.getResources().openRawResource(R.raw.topenglishwords), outputStream);
            CSVReader reader = new CSVReader(new FileReader(tempFile));
            List<String[]> myEntries = reader.readAll();
            Map<Integer, String> tempTrainingMap = new HashMap<Integer, String>();
            myEntries.forEach(strings ->{
                String[] split = strings[0].split("\t");
                tempTrainingMap.put(Integer.parseInt(split[0]), String.valueOf(split[1]));
            });

            trainingMap = Collections.unmodifiableMap(tempTrainingMap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("CSV Error", "The specified file was not found with error: " + e.getMessage());
        }
        return trainingMap;
    }

}

package com.bhaptics.bhapticsandroid;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.bhaptics.bhapticsandroid.activities.LanguageActivity;
import com.bhaptics.bhapticsmanger.HapticPlayer;
import com.bhaptics.commons.model.DotPoint;
import com.bhaptics.commons.model.PathPoint;
import com.bhaptics.commons.model.PositionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HapticsProcessor extends Worker {
    public HapticsProcessor(
            @NonNull Context context,
            @NonNull WorkerParameters params){
        super(context, params);
    }
    Map<String, HapticPattern> hapticsMap = CSVLoader.hapticsMap;
    private HapticPlayer hapticPlayer;

    @NonNull
    @Override
    public Result doWork() {

        //pass the phonetic sound, reading rate, word length, and the number the phoneme is in the word in reverse order,
        // the first phone of a 6 phone word, would come in with "6"

        try {
            Log.i("CSV", "Input params are..." + getInputData().getString("phone"));

            toHaptic(getInputData().getString("phone"), getInputData().getInt("readRate", 10), getInputData().getInt("place", 1));
        }catch (Exception e){
            Log.i("CSV", "Error submitting haptics with error "+ e.getMessage());
        }
        return Result.success();
    }

    public void toHaptic(String phone, Integer wpm, Integer placement) {

        HapticPattern pattern = hapticsMap.get(phone);
        hapticPlayer = BhapticsModule.getHapticPlayer();
        Integer intensity;
        Integer duration = 100;
        if(placement == 1){
            intensity = 90;
            duration = 200;
        }else if(placement == 2){
            intensity = 65;
        }else if(placement == 3){
            intensity = 50;
        }else if(placement == 4){
            intensity = 35;
        }else if(placement == 5){
            intensity = 22;
        }else{
            intensity = 10;
        }
        LanguageActivity.setPhoneView(phone);

        DotPoint dotPoint = new DotPoint(Math.round(pattern.getxCo()),intensity);

        List<DotPoint> dots = Arrays.asList(dotPoint);


        if(pattern.getDirection() == 1) {
            hapticPlayer.submitDot("VestFront", PositionType.VestFront,  dots, duration);
            //hapticPlayer.submitPath("VestFront", PositionType.VestFront,Arrays.asList(new PathPoint(pattern.getxCo(), pattern.getyCo(), intensity)), duration);
        }else{
            hapticPlayer.submitDot("VestBack", PositionType.VestBack,  dots, duration);
            //    hapticPlayer.submitPath("VestBack", PositionType.VestBack, Arrays.asList(new PathPoint(pattern.getxCo(), pattern.getyCo(), intensity)), duration);
        }

        Log.i("CSVNew", "Delayed output of phoneme here: " + phone + ". With intensity: "+ intensity);
    }

}
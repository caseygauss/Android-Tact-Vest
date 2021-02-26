package com.bhaptics.bhapticsandroid

import android.util.Log
import com.bhaptics.bhapticsandroid.activities.LanguageActivity
import com.bhaptics.bhapticsmanger.HapticPlayer
import com.bhaptics.commons.model.DotPoint
import com.bhaptics.commons.model.PositionType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class HapticsBackgroundProcessor {

    var hapticsMap: MutableMap<String, HapticPattern> = CSVLoader.hapticsMap
    var phonemesMap: MutableMap<String, String> = CSVLoader.phonemesMap

    fun messageToPhonemes(message: String, speed: Long){
        val words = message.split(" ").toTypedArray()
        Log.i("Live", "messageToPhonemes $message")
        var combinedPhrase = ""
        for (word in words) {
            val bigWord = word.toUpperCase()
            try {
                val phoneticPhrase = phonemesMap[bigWord]
                val phones = phoneticPhrase!!.split(" ").toTypedArray()
                val wordLength = phones.size
                var place = wordLength
                combinedPhrase += "$phoneticPhrase "
                Log.i("CSV", "The phonetic message is: $phoneticPhrase")
                for (i in phones.indices) {
                    Log.i("CSV", "The inner phone is: " + phones[i])

                    runBlocking {
                        launch {
                            Log.i("Live", "Running in launch with " + phones[i])
                            toHaptic(phones[i], speed, place)
                            delay(speed)
                            Log.i("Live", "Post delay")
                        }
                    }
                    place -= 1
                }
            } catch (e: Exception) {
                Log.i("Speech", "No match for word " + bigWord + e.message)
            }
        }
    }


    private fun toHaptic(phone: String, wpm: Long, placement: Int) {
        val pattern = hapticsMap[phone]
        var hapticPlayer: HapticPlayer = BhapticsModule.getHapticPlayer()
        val intensity: Int
        var duration = wpm
        if (placement == 1) {
            intensity = 90
            duration += 50
        } else if (placement == 2) {
            intensity = 65
        } else if (placement == 3) {
            intensity = 50
        } else if (placement == 4) {
            intensity = 35
        } else if (placement == 5) {
            intensity = 30
        } else {
            intensity = 25
        }
        LanguageActivity.setPhoneView(phone)
        val dotPoint = DotPoint(Math.round(pattern!!.getxCo()), intensity)
        val dots = Arrays.asList(dotPoint)
        Log.i("Live", "Posted " + phone + "with direction: " + pattern.direction + " and dot number: " + Math.round(pattern.getxCo()))
        if (pattern.direction == 1) {
            hapticPlayer.submitDot("VestFront", PositionType.VestFront, dots, duration.toInt())
            //hapticPlayer.submitPath("VestFront", PositionType.VestFront,Arrays.asList(new PathPoint(pattern.getxCo(), pattern.getyCo(), intensity)), duration);
        } else {
            hapticPlayer.submitDot("VestBack", PositionType.VestBack, dots, duration.toInt())
            //    hapticPlayer.submitPath("VestBack", PositionType.VestBack, Arrays.asList(new PathPoint(pattern.getxCo(), pattern.getyCo(), intensity)), duration);
        }
    }
}
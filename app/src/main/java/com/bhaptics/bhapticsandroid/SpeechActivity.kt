package com.bhaptics.bhapticsandroid

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bhaptics.bhapticsandroid.utils.AudioEmitter
import com.google.api.gax.rpc.ApiStreamObserver
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.speech.v1.*
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean


private const val TAG = "Speech"

class SpeechActivity : View.OnClickListener, Activity() {

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.RECORD_AUDIO)
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private var mPermissionToRecord = false
    private var mAudioEmitter: AudioEmitter? = null
    private lateinit var mTextView: TextSwitcher

    private lateinit var toggleOnButton: Button
    private lateinit var backButton: Button
    private var listenOn: Boolean = true
    private lateinit var csvLoader: CSVLoader
    private lateinit var toHaptics: HapticsBackgroundProcessor
    private var readRate: Long = 75

    private val mSpeechClient by lazy {
        // NOTE: The line below uses an embedded credential (res/raw/sa.json).
        //       You should not package a credential with real application.
        //       Instead, you should get a credential securely from a server.
        applicationContext.resources.openRawResource(R.raw.credential).use {
            SpeechClient.create(SpeechSettings.newBuilder()
                    .setCredentialsProvider { GoogleCredentials.fromStream(it) }
                    .build())
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Speech", "Trying to talk");
        setContentView(R.layout.activity_speech)

        // get permissions
        //ActivityCompat.requestPermissions( this, PERMISSIONS, REQUEST_RECORD_AUDIO_PERMISSION)

        // get UI element

        toggleOnButton = findViewById(R.id.listen_button)
        mTextView = findViewById(R.id.last_recognize_result)
        mTextView.setFactory {
            val t = TextView(this)
            t.setText(R.string.start_talking)
            t.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            t.setTextAppearance(android.R.style.TextAppearance_Large)
            t
        }
        mTextView.setInAnimation(applicationContext, android.R.anim.fade_in)
        mTextView.setOutAnimation(applicationContext, android.R.anim.fade_out)
        backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener(this)

        val loader = CSVLoader()
        CSVLoader.setPhonemesMap()

        toHaptics = HapticsBackgroundProcessor()
    }

    override fun onResume() {
        super.onResume()
        if(listenOn) {
            beginListening();
        }
    }

    private fun beginListening(){
        Log.i("Speech", "Begun listening..");
        mPermissionToRecord = true

        if (mPermissionToRecord) {
            val isFirstRequest = AtomicBoolean(true)
            mAudioEmitter = AudioEmitter()


            var lastWord = "dog"
            var lastWords: String = "oodieBout"
            var concatString = ""
            var time = java.util.Calendar.getInstance()

            try {
                // start streaming the data to the server and collect responses
                val requestStream = mSpeechClient.streamingRecognizeCallable()
                        .bidiStreamingCall(object : ApiStreamObserver<StreamingRecognizeResponse> {
                            override fun onNext(value: StreamingRecognizeResponse) {
                                runOnUiThread {
                                    when {
                                        value.resultsCount > 0 && value.getResults(0).stability > 0.8 -> {
                                            if (!value.getResults(0).isFinal) {
                                                var said = value.getResults(0).getAlternatives(0).transcript
                                                var split = said.split(" ").toTypedArray()
                                                Log.i("SXP", "The Split " + split[0]);
                                                for (word in split) {
                                                    if (!lastWords.contains(word)) {
                                                        mTextView.setText(word)
                                                        lastWord = word
                                                        concatString = concatString.plus(" ").plus(word)
                                                        lastWords += word
                                                        Log.i("SXP", "Concat String: " + concatString)
                                                        Log.i("Live", word)
                                                        toHaptics.messageToPhonemes(word, readRate)
                                                    } else {
                                                        //do something to manage sentences with a single word used twice
                                                    }
                                                }
                                            } else {
                                                Log.i("SXP", "Final message with " + value.getResults(0).getAlternatives(0).transcript)
                                                lastWord = value.getResults(0).getAlternatives(0).transcript;
                                                var endWord = lastWord.replace(concatString, "");
                                                mTextView.setText(endWord)
                                                lastWord = ""
                                                lastWords = ""
                                                concatString = ""
                                                Log.i("Live Final", endWord)
                                            }
                                        }
                                        else -> {
                                            Log.i("SX", "Word" + value.getResults(0).getAlternatives(0).transcript + " with stability: " + value.getResults(0).stability)
                                            if (value.getResults(0).getAlternatives(0).transcript.isNotEmpty() && value.getResults(0).stability <= 0.00) {
                                                //to include final word which typical ends with stability of 0 if no audio is presented at the end
                                                    //should update to concatString
                                                if (!concatString.contains(value.getResults(0).getAlternatives(0).transcript)) {
                                                    Log.i("SXP", "Unstable message with " + value.getResults(0).getAlternatives(0).transcript + " last words are: "+ lastWords)
                                                    lastWord = value.getResults(0).getAlternatives(0).transcript;
                                                    var split = lastWord.split(" ").toTypedArray()
                                                    mTextView.setText(split[split.size - 1])
                                                    Log.i("Live Low", split[split.size - 1])
                                                    lastWord = ""
                                                    lastWords = ""
                                                    concatString = ""
                                                    toHaptics.messageToPhonemes(split[split.size - 1], readRate)
                                                }else{
                                                    lastWord = ""
                                                    lastWords = ""
                                                    concatString = ""
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onError(t: Throwable) {
                                Log.i(TAG, "an error occurred", t)
                            }

                            override fun onCompleted() {
                                Log.d(TAG, "stream closed")
                            }
                        })

                // monitor the input stream and send requests as audio data becomes available
                mAudioEmitter!!.start { bytes ->
                    val builder = StreamingRecognizeRequest.newBuilder()
                            .setAudioContent(bytes)

                    // if first time, include the config
                    if (isFirstRequest.getAndSet(false)) {
                        builder.streamingConfig = StreamingRecognitionConfig.newBuilder()
                                .setConfig(RecognitionConfig.newBuilder()
                                        .setLanguageCode("en-US")
                                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                        .setSampleRateHertz(16000)
                                        .setUseEnhanced(true)
                                        .setModel("video")
                                        .build())
                                .setInterimResults(true)
                                .setSingleUtterance(false)
                                .build()
                    }

                    // send the next request
                    requestStream.onNext(builder.build())
                }
            }catch (e:Exception){
                Log.i("Speech Activity", "Error with message "+e.localizedMessage);
            }
        } else {
            Log.e(TAG, "No permission to record! Please allow and then relaunch the app!")
        }

    }

    fun toggleListening(view: View){
        if(listenOn){
            listenOn = false
            mAudioEmitter?.stop()
            mAudioEmitter = null
            mTextView.setText("I Can't Hear You")
        }else{
            listenOn = true
            mTextView.setText("I'm Listening")
            beginListening()
        }
    }

    override fun onPause() {
        super.onPause()
        //mAudioEmitter?.stop()
        //mAudioEmitter = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mSpeechClient.shutdown()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCode) {
            mPermissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }

        // bail out if audio recording is not available
        if (!mPermissionToRecord) {
            finish()
        }
    }

    override fun onClick(v: View?) {
        finish()
    }
}

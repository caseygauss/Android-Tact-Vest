package com.bhaptics.bhapticsandroid.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.HRate;
import com.bhaptics.bhapticsandroid.BhapticsModule;
import com.bhaptics.bhapticsandroid.R;
import com.bhaptics.bhapticsandroid.SpeechActivity;
import com.bhaptics.bhapticsandroid.adapters.ListViewAdapter;
import com.bhaptics.bhapticsmanger.BhapticsManager;
import com.bhaptics.bhapticsmanger.BhapticsManagerCallback;
import com.bhaptics.bhapticsmanger.PlayerResponse;
import com.bhaptics.commons.model.BhapticsDevice;

import java.util.List;

public class LobbyActivity extends Activity implements View.OnClickListener {
    public static final String TAG = LobbyActivity.class.getSimpleName();

    private BhapticsManager bhapticsManager;
    private ListViewAdapter adapter;

    private Button scanButton, drawingButton, tactFileButton, tactotExampleButton, pingallButton, compassButton, phoneticsButton, speechButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkVoicePermission();
            Log.i("Speech", "Permissions not set");
        }

        if(SpeechRecognizer.isRecognitionAvailable(this)){
            Log.i("Speech", "Recognition is available.");
        }else{
            Log.i("Speech", "Not available");
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });

        Amplify.DataStore.observe(HRate.class,
                started -> Log.i("Tutorial", "Observation began."),
                change -> Log.i("Tutorial", change.item().toString()),
                failure -> Log.e("Tutorial", "Observation failed.", failure),
                () -> Log.i("Tutorial", "Observation complete.")
        );

        BhapticsModule.initialize(getApplicationContext());


        bhapticsManager = BhapticsModule.getBhapticsManager();

        adapter = new ListViewAdapter(this, bhapticsManager.getDeviceList());
        ListView listview = (ListView) findViewById(R.id.deviceListView) ;
        listview.setAdapter(adapter) ;

        scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        drawingButton = findViewById(R.id.drawing_button);
        drawingButton.setOnClickListener(this);

        tactFileButton = findViewById(R.id.tact_file_button);
        tactFileButton.setOnClickListener(this);

        tactotExampleButton = findViewById(R.id.tactot_file_button);
        tactotExampleButton.setOnClickListener(this);

        pingallButton = findViewById(R.id.ping_button);
        pingallButton.setOnClickListener(this);

        compassButton = findViewById(R.id.compassButton);
        compassButton.setOnClickListener(this);

        phoneticsButton = findViewById(R.id.phonetics_button);
        phoneticsButton.setOnClickListener(this);

        speechButton = findViewById(R.id.speech_button);
        phoneticsButton.setOnClickListener(this);


        bhapticsManager.addBhapticsManageCallback(new BhapticsManagerCallback() {
            @Override
            public void onDeviceUpdate(List<BhapticsDevice> list) {
                adapter.onChangeListUpdate(list);
            }

            @Override
            public void onScanStatusChange(boolean b) {
                if (b) {
                    scanButton.setText("Scanning");
                } else {
                    //scanButton.setText("Scan");
                }
            }

            @Override
            public void onChangeResponse(PlayerResponse playerResponse) { }

            @Override
            public void onConnect(String s) { }

            @Override
            public void onDisconnect(String s) { }
        });

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasPermissions(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Permission is not granted
            Log.e(TAG, "onResume: permission ACCESS_FINE_LOCATION"  );
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
            bhapticsManager.scan();
        }

        if (!hasPermissions(this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bhapticsManager.dispose();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan_button) {
            if (bhapticsManager.isScanning()) {
                bhapticsManager.stopScan();
            } else {
                bhapticsManager.scan();
            }

        } else if ("cat" == "dog") {
            bhapticsManager.pingAll();
        } else if (v.getId() == R.id.drawing_button) {
            startActivityForResult(new Intent(this, DrawingActivity.class), 1);
        } else if (v.getId() == R.id.tact_file_button) {
            startActivityForResult(new Intent(this, TactFileActivity.class), 1);
        } else if(v.getId() == R.id.compassButton) {
            startActivityForResult(new Intent(this, CompassActivity.class), 1);
        } else if(v.getId() == R.id.phonetics_button) {
            startActivityForResult(new Intent(this, LanguageActivity.class), 1);
        }else if(v.getId() == R.id.ping_button) {
            Log.i("Speech", "Button pressed");
            SpeechActivity speechActivity = new SpeechActivity();
            startActivityForResult(new Intent(this, speechActivity.getClass()), 1);
        } else{
            startActivityForResult(new Intent(this, VestDemoActivity.class), 1);
        }
    }

    public void checkVoicePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 200);
        }
    }
}

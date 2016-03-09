package net.ddns.frebern.chessaronline;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class SplashActivity extends Activity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "SplashActivity";
    private BroadcastReceiver broadcastReceiver;
    private boolean isReceiverRegistered;

    private String gcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("GCM","onReceive");
                stopService(new Intent(SplashActivity.this,GcmRegistrationIntentService.class));
                gcmToken = intent.getStringExtra("GCM_TOKEN");
                if(intent.getBooleanExtra("GCM_REGISTERED",false)){
                    Log.e("GCM", "GCM Registered");
                    new Handler(){
                        @Override
                        public void handleMessage(Message msg){
                            super.handleMessage(msg);
                            Intent toMain = new Intent(SplashActivity.this, MainActivity.class);
                            toMain.putExtra("GCM_TOKEN",gcmToken);
                            startActivity(toMain);
                            finish();
                        }
                    }.sendEmptyMessageDelayed(0, 4321);
                }
                else{
                    Log.e("GCM","GCM Registration Error!");
                    finish();
                }

            }
        };

        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.e("GCM","Call GcmRegistrationIntentService");
            Intent intent = new Intent(this,GcmRegistrationIntentService.class);
            startService(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(broadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){
        Log.e("GCM","registerReceiver called "+!isReceiverRegistered);
        if(!isReceiverRegistered) {
            registerReceiver(broadcastReceiver,new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}

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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

public class SplashActivity extends Activity {


    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.i("ACTIVITY", "Splash Activity");
        new Handler(){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }.sendEmptyMessageDelayed(0, 5000);

    }
}

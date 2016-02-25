package net.ddns.frebern.chessaronline;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SplashActivity extends Activity {

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
        }.sendEmptyMessageDelayed(0,5000);

    }
}

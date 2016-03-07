package net.ddns.frebern.chessaronline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class CustomGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }
        Intent intent = new Intent(this,UnityPlayerActivity.class);
        intent.putExtra("from",from);
        intent.putExtra("msg",message);
        sendBroadcast(intent);

    }

}

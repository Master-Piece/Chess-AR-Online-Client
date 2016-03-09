package net.ddns.frebern.chessaronline;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GcmRegistrationIntentService extends IntentService {

    public GcmRegistrationIntentService(){
        super("GcmRegistrationIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmRegistrationIntentService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("GCM", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    /*
     * 여기서 구글 GCM 서버로부터 Registration Token을 얻는다. (GCM 서버 != 게임 서버)
     * 토큰을 게임 운영하는 서버로 POST한다.
     * 그 후 sharered preference에 토큰을 게임서버로 보냈는지 안보냈는지를 저장한다.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Log.e("GCM","Now Register...");

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE,null);
            Log.e("GCM","GCM Registration Token: "+token);
            QuickstartPreferences.token = token;
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }


    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    /* */
    // 여기서 토큰을 게임서버로 전송한다.
    private void sendRegistrationToServer(final String token) {
        // Add custom implementation, as needed.

        new AsyncTask<Void,Void,String>(){

            @Override
            protected String doInBackground(Void... params) {
                Log.e("GCM", "Now TOKEN Sending...");
                String address = "http://www.kostrian.xyz/post_return.php";
                ContentValues values = new ContentValues();
                values.put("type","GCM_REGISTER");
                //values.put("token", token); //Something Error in receive result
                String response = HttpPostTask.getInstance().sendRequest(address,values);
                Log.e("GCM_REGISTER", response);
                return response;

            }

            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                Log.e("GCM_RESPONSE", response);
                Intent registerDone = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                registerDone.putExtra("GCM_REGISTERED", true);
                registerDone.putExtra("GCM_TOKEN",token);
                sendBroadcast(registerDone);
            }
        }.execute();
    }



}

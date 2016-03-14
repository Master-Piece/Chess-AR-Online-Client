package net.ddns.frebern.chessaronline;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class VoiceActivity extends ASR implements SensorEventListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Magnetic Sensor
        _sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        registerMagneticBtn();
    }

    //DO NOT MODIFY BELOW ATTRIBUTES!!!
    // Default values for the language model and maximum number of recognition results
    // They are shown in the GUI when the app starts, and they are used when the user selection is not valid
    private final static int DEFAULT_NUMBER_RESULTS = 3;
    private final static String DEFAULT_LANG_MODEL = RecognizerIntent.LANGUAGE_MODEL_FREE_FORM;
    // Attributes
    private int numberRecoResults = DEFAULT_NUMBER_RESULTS;
    private String languageModel = DEFAULT_LANG_MODEL;


    //Log Cat Tag
    private static final String LOGTAG = "ASR";


    //커맨드 풀
    protected HashMap<String,String[]> commandPool;

    //커맨드 풀 세팅
    protected abstract void setCommandPool();

    //커맨드 Filtering
    protected abstract String filtering(String str);

    /* recognize() 실행 중에 호출되는 메서드. */
    protected abstract void setVoiceCommandAction(String result);


    /* 이하는 음성인식 부분. 꼭 필요하지 않는 이상 건드리지 말 것 */

    /**
     *  Shows the formatted best of N best recognition results (N-best list) from
     *  best to worst in the <code>ListView</code>.
     *  For each match, it will render the recognized phrase and the confidence with
     *  which it was recognized.
     *
     *  @param nBestList	    list of matches
     *  @param nBestConfidences	confidence values (from 0 = worst, to 1 = best) for each match
     */
    @Override
    public void processAsrResults(ArrayList<String> nBestList, float[] nBestConfidences) {

        //Creates a collection of strings, each one with a recognition result and its confidence, e.g. "Phrase matched (conf: 0.5)"
        String text="Failed to recognize correct speak";
        if(nBestList!=null) {
            int size = nBestList.size();
            for (int i = 0; i < size; i++) {
                if (nBestConfidences != null) {
                    if (nBestConfidences[i] >= 0) {
                        text = nBestList.get(i).trim();
                        break;
                    }
                }
            }
        }

        //음성인식이 끝난 후 지점.

        //Adds information to log
        Log.d(LOGTAG, "D:Recognized Text : " + text);
        setVoiceCommandAction(filtering(text));
        recogLock=false;//음성인식 락해제.
        //stopListening();
    }

    @Override
    public void processAsrError(int errorCode) {

        String errorMessage;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                errorMessage = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                errorMessage = "죄송합니다.\n애플리케이션에 문제가 발생했습니다.";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "인터넷 연결상태를 확인해주세요.";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "인터넷 연결상태를 확인해주세요.";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "인식된 결과가 없습니다.";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "음성 인식 중 입니다...";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "죄송합니다.\n서버에 이상이 발생했습니다.";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "인식된 결과가 없습니다.";
                break;
            default:
                errorMessage = "죄송합니다.\n애플리케이션에 문제가 발생했습니다.";
                break;
        }

        //음성인식 중 에러 발생시 도착하는 지점.
        //errorMessage = "ERROR("+errorCode+") : "+errorMessage;
        setVoiceCommandAction("Error : "+errorMessage);
        Log.e("ASR_ERR", errorMessage);
        recogLock = false;
    }
    @Override
    public void processAsrReadyForSpeech() {

    }
    /**
     * Reads the values for the language model and the maximum number of recognition results
     * from the GUI
     */
    private void setRecognitionParams(){
        // Not need to implement
    }

    private boolean recogLock = false;
    protected void recognize(){
        //Speech recognition does not currently work on simulated devices,
        //it the user is attempting to run the app in a simulated device
        //they will get a Toast
        if (!recogLock) { //음성인식 락 설정(recognize()를 동시에 여러번 호출해도 의미 없게끔)

            recogLock = true;
            Log.e("ASR","IN THE LOCK");
            if ("generic".equals(Build.BRAND.toLowerCase())) {
                String err = "E:ASR attempt on virtual device";
                Log.e(LOGTAG, err);
                recogLock = false;
                setVoiceCommandAction(err);
            } else {

                setRecognitionParams(); //Read ASR parameters
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            listen(languageModel, numberRecoResults); //Start listening
                        } catch (Exception e) {
                            String err = "F:ASR could not be started: invalid params";
                            Log.e(LOGTAG, "F:" + e.getMessage());
                            setVoiceCommandAction(e.getMessage());
                            Log.e("ASR_ERR", e.getMessage());
                            recogLock = false;
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

    }

    private SensorManager _sensorManager;
    private int[] _del = {0,0,0};
    private long _cnt =0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent){
        if(sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            _cnt++;
            if(_cnt>=250000) _cnt=25;
            int i;
            float diff=0;
            for(i=0;i<3;i++){
                diff=sensorEvent.values[i]- _del[i];
                diff=diff<0?-diff:diff;
                if(diff<3) break;
            }
            Log.e("MAGNETIC","diff:"+diff+" and "+sensorEvent.values[0]+"   "+sensorEvent.values[1]+"   "+sensorEvent.values[2]);
            if(i==3 && _cnt >25 && !recogLock) {
                _cnt=0;
                Log.e("MAGNETIC","RECOGNIZE");
                recognize();
            }
            for(i=0;i<3;i++) _del[i]=(int)sensorEvent.values[i];
        }
    }
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Ignoring this for now
    }

    private void registerMagneticBtn(){
        _cnt =0;
        _del[0]= _del[1]= _del[2]=0;
        _sensorManager.registerListener(VoiceActivity.this, _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_NORMAL);
    }
    private void unregisterMagneticBtn(){
        _sensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        unregisterMagneticBtn();
        super.onPause();
    }

    @Override
    protected void onStop() {
        unregisterMagneticBtn();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerMagneticBtn();
    }

}

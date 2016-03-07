package net.ddns.frebern.chessaronline;

import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.unity3d.player.UnityPlayer;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class VoiceActivity extends ASR {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected abstract void setVoiceCommand(String result);


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

        //Adds information to log
        Log.d(LOGTAG, "D:Recognized Text : " + text);
        setVoiceCommand(filtering(text));
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
                errorMessage = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                errorMessage = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                errorMessage = "Network related error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                errorMessage = "Network operation timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                errorMessage = "No recognition result matched";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                errorMessage = "음성 인식 중 입니다...";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                errorMessage = "Server sends error status";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                errorMessage = "No speech input";
                break;
            default:
                errorMessage = "ASR error";
                break;
        }
        errorMessage = "ERROR("+errorCode+") : "+errorMessage;
        setVoiceCommand(errorMessage);
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
    protected void recognize(){
        //Speech recognition does not currently work on simulated devices,
        //it the user is attempting to run the app in a simulated device
        //they will get a Toast
        if ("generic".equals(Build.BRAND.toLowerCase())) {
            String err = "E:ASR attempt on virtual device";
            Log.e(LOGTAG, err);
            setVoiceCommand(err);
        } else {

            setRecognitionParams(); //Read ASR parameters
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            listen(languageModel, numberRecoResults); //Start listening
                        }
                    } catch (Exception e) {
                        String err = "F:ASR could not be started: invalid params";
                        Log.e(LOGTAG, "F:"+e.getMessage());
                        setVoiceCommand(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

        }
    }

}

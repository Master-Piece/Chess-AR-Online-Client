package net.ddns.frebern.chessaronline;

import com.unity3d.player.*;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class UnityPlayerActivity extends VoiceActivity
{
	protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    protected Window win;
    protected LinearLayout layout;

    private Button _recogCancel;
    private Button _recogConfirm;

    private TextView _srLabel;
    private TextView _srVoice;
    //컨텍스트에 따라 바뀌는 명령어 종류들을 담는 컬렉션.
    private ArrayList<String> _commandSet;
    //AR Recognize Context.
    private ArrayList<String> _markerRecogCommands;

    private PanelHandler _handler;
    private TimerThread _timer;

    private void removeCurrentUI(){
        ((ViewGroup) layout.getParent()).removeView(layout);
    }

    private void setUI(int layoutId){
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(layoutId,null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        win.addContentView(layout, layoutParams);
    }

    private void changeUI(int layoutId){
        removeCurrentUI();
        setUI(layoutId);
    }

    private void initRecogLayout(){
        /* recogPanel 버튼 이벤트 연결 */
        _recogCancel = (Button) findViewById(R.id.recogCancel);
        _recogConfirm = (Button) findViewById(R.id.recogConfirm);

        _recogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnityPlayer.quit();
                //System.exit(0);을 해도 됨.
                //finish(); // finish 호출시 SIGNAL 9 (Kill)로 인해 어플리케이션 전체가 종료됨.
            }
        });
        _recogConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUI(R.layout.layout_matchmaking);
            }
        });

        _srLabel = (TextView) findViewById(R.id.srLabel);
        _srLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognize();
            }
        });

        _srVoice = (TextView) findViewById(R.id.srVoice);

        _handler = new PanelHandler();
        new TimerThread().start();
    }

    private void initVoiceRecognizer(){
        createRecognizer(UnityPlayerActivity.this);
        setCommandPool();
        setCommandSets();
        changeCommandSet(_markerRecogCommands);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        win = getWindow();
        win.setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

        mUnityPlayer = new UnityPlayer(this);
        win.setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        setUI(R.layout.layout_recog);
        initRecogLayout();
        initVoiceRecognizer();

    }



    private void setCommandSets(){
        _markerRecogCommands = new ArrayList<String>();
        _markerRecogCommands.add("Cancel");
        _markerRecogCommands.add("Confirm");
    }

    private void changeCommandSet(ArrayList<String> commandSet){
        _commandSet = commandSet;
    }

    /* Voice Activity Override Methods.*/

    @Override
    protected void setCommandPool(){
        commandPool = new HashMap<String,String[]>();
        String cancel[] = {"캔슬","캔쓸","취소","켄슬","켄쓸","캐슬","캔","뒤로","뒤로가기"};
        String confirm[] = {"컨펌","얼로우","오케이","오키","확인"};

        Arrays.sort(cancel);
        Arrays.sort(confirm);

        commandPool.put("Cancel", cancel);
        commandPool.put("Confirm", confirm);
    }

    @Override
    protected String filtering(String str) {
        for(String key:_commandSet)
            if (Arrays.binarySearch(commandPool.get(key), str) > 0)
                return key;
        return str;
    }

    @Override
    protected void setVoiceCommand(String filteredResult) {

        _srLabel.setText("Recognized Command : ");
        _srVoice.setText(filteredResult);

        if(filteredResult.equals("Cancel"))
            _recogCancel.callOnClick();
        else if(filteredResult.equals("Confirm"))
            _recogConfirm.callOnClick();
        else if(filteredResult.equals("Credit"));
        else if(filteredResult.equals("Quit"));

    }

    private static final int CHANGE_TO_RECOG_PANEL=0;

    class PanelHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CHANGE_TO_RECOG_PANEL:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
            }
        }
    }
    class TimerThread extends Thread implements Runnable{
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2400);
                _handler.sendEmptyMessage(CHANGE_TO_RECOG_PANEL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }















    // Quit Unity
	@Override protected void onDestroy ()
	{
		mUnityPlayer.quit();
		super.onDestroy();
	}

	// Pause Unity
	@Override protected void onPause()
	{
		super.onPause();
		mUnityPlayer.pause();
	}


    // Resume Unity
	@Override protected void onResume()
	{
		super.onResume();
		mUnityPlayer.resume();
	}

	// This ensures the layout will be correct.
	@Override public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		mUnityPlayer.windowFocusChanged(hasFocus);
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override public boolean dispatchKeyEvent(KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
	@Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
	/*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }


}

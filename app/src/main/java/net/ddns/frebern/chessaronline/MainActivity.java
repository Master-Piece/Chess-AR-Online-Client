package net.ddns.frebern.chessaronline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class MainActivity extends VoiceActivity {

    private Button _start;
    private Button _option;
    private Button _credit;
    private Button _quit;
    private OptionDialog _optDialog;
    private TextView _srLabel;
    private TextView _srVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMenuButtons();
        MainActivity.this.setCommandPool();
    }


    private void setMenuButtons() {
        _start = (Button)findViewById(R.id.start);
        _option = (Button)findViewById(R.id.option);
        _credit = (Button)findViewById(R.id.credit);
        _quit = (Button)findViewById(R.id.quit);
        _srLabel = (TextView)findViewById(R.id.srLabel);
        _srVoice = (TextView)findViewById(R.id.srVoice);

        //Start Button Clicked
        _start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UnityPlayerActivity.class));
            }
        });

        //Option Button Clicked
        _option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _optDialog = new OptionDialog(MainActivity.this);
                _optDialog.show();
            }
        });

        //Credit Button Clicked
        _credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UnityPlayerActivity.class));
                finish();
            }
        });

        //Quit Button Clicked
        _quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                finish();
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        _srLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.recognize();
            }
        });

    }

    @Override
    public void onBackPressed(){

    }


    /* Voice Activity Override Methods.*/

    @Override
    protected void setCommandPool(){
        createRecognizer(MainActivity.this);
        commandPool = new HashMap<String,String[]>();
        String start[] = {"스타트","스타뜨","스타토","스탠다드","스타","스타츠","스타킹","스타스","스카치",
                "스타추","스태츄","스태추","스탸트","스탸츠","코타츠","코다츠","스탓","스카트","스커트","카트",
                "시작","지작","이작","시쟉","지쟉","이쟉","시샵","씨샵","지샵","시샥","시삭","치사","시자",
                "시장","지장","시잡","씨잡","씨작","씨장","게임시작","게임","대전","시작해"};
        String option[] = {"옵션","옵선","욥선","욥션","설정","설성","절정","절전","옥션","옥선"};
        String credit[] = {"크레딧","크래딧","개발자","크레딕","크레인","크래딕","크래인","개발","개발진"};
        String quit[] = {"빛","큇","퀵","킥","킉","엑싯","엑씻","엑스","엑시트","엑씨트","엑싰","엑씼",
                "액싯","액씻","액스","액시트","액씨트","액싰","액씼","에셋","엑소","엑시트","나가기","나가","끝",
                "셧다운","클로즈","클로우즈","닫아","꺼져","꺼저","종료","종로","다다"};
        Arrays.sort(start);
        Arrays.sort(option);
        Arrays.sort(credit);
        Arrays.sort(quit);

        commandPool.put("Start", start);
        commandPool.put("Option", option);
        commandPool.put("Credit", credit);
        commandPool.put("Quit", quit);
    }

    @Override
    protected String filtering(String str) {
        Set<String> keys = commandPool.keySet();
        for(String key:keys)
            if(Arrays.binarySearch(commandPool.get(key),str)>0)
                return key;
        return str;
    }

    @Override
    protected void setVoiceCommand(String filteredResult) {
        Log.e("ASR","Set Voice Command");
        _srLabel.setText("Recognized Command : ");
        _srVoice.setText(filteredResult);

        if(filteredResult.equals("Start"))
            _start.callOnClick();
        else if(filteredResult.equals("Option"))
            _option.callOnClick();
        else if(filteredResult.equals("Credit"))
            _credit.callOnClick();
        else if(filteredResult.equals("Quit"))
            _quit.callOnClick();

    }



}

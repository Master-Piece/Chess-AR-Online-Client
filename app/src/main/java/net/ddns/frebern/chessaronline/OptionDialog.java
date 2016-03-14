package net.ddns.frebern.chessaronline;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class OptionDialog extends Dialog {

    private Button _optSave;
    private Button _optCancel;
    private RadioGroup _optVolume;
    private Switch _optVoiceConfirm;
    private LinearLayout _optCard;
    private LinearLayout _optFull;
    private Context _context;
    private TextView _srLabel;
    private TextView _srVoice;

    VoiceActivity voiceActivity = new VoiceActivity() {
        @Override
        protected void setCommandPool(){

            commandPool = new HashMap<String,String[]>();

            String save[] = {"네이버","네버","세이브","저장","서장","세브","세이버","처장","저작","케이브","싸이","세입","세잇","새잇"};
            String cancel[] = {"캔슬","캔쓸","취소","켄슬","켄쓸","캐슬","캔","뒤로","뒤로가기","cancel","치소"};
            String max[] = {"맥스","최대","최대음량","최고","맥시멈","맥시","맥스사운드","맥시멈사운드",
                    "사운드맥스","사운드 맥스","사운드 맥시멈","사운드 최대","사운드최대","체대",
                    "음량 최대","음량최대","음악 최대","음악최대","음악체대","음악 체대","맥","넥스","넥슨","낵스",
                    "음악 최고","음악최고","뮤직 맥스","뮤직맥스","뮤직 맥시멈","뮤직맥시멈","맵시","맥주","섹스","네",
                    "볼륨 맥스","볼륨 맥시멈","볼륨맥스","볼륨맥시멈","볼륨 최대","볼륨최대","볼륨 최대로","max"};
            String high[] = {"하이","높게","높음","음량 높게","음량높게","사운드 하이","사운드하이","하이어","하이여","높음",
                        "음악 높게","음악높게","음악 하이","음악하이","뮤직 하이","뮤직하이","볼륨 하이","볼륨하이",
                        "볼륨 업","볼륨업","볼륨 크게","볼륨크게","볼륨 높게","볼륨높게","high"};
            String medium[] = {"미듐","믿음","미디움","미디윰","미드","미디","밋","밑","중간",
                    "사운드미듐","사운드 미듐","사운드 미디움","사운드미디움","사운드 중간","사운드중간",
                    "뮤직 미듐","뮤직미듐","뮤직 미디움","뮤직미디움","볼륨미듐","볼륨 미듐","볼륨 중간","볼륨중간","medium"};
            String low[] = {"로우","로","로오","낮게","낮음","음량낮게","음량 낮게","낮은음량","낮은 음량",
                    "뮤직 로우","뮤직로우","사운드 로우","사운드로우","사운드로","볼륨 로우","볼륨로우","low",
                    "볼륨 로","볼륨로","볼륨 낮게","볼륨 작게","소리 작게","소리작게","볼륨작게"};
            String mute[] = {"뮤트","무트","무드","뮤토","뮤드","뮤츠","뮤","음소거","셧다운","턴다운","턴오프","턴 오프","mute",
                    "조용히","닥쳐","조용히해","조용해","꺼","음악꺼","뮤직 뮤트","뮤직뮤트","뮷","뮻","소리 꺼","소리꺼","볼륨오프","볼륨 오프"};
            String card[] = {"카드","카드보드","카드보드뷰","카드보드 뷰","화면분할","분할",
                    "디바이드","에이알","에이 알","에이알 뷰","헤드 마운트 뷰","헤드마운트뷰","화면 분할"};
            String full[] = {"풀","풀뷰","폴","푸울","푸","풀 뷰","전체 화면","전체화면","합치기","풍","풍부"};
            String toggle[] = {"토글","toggle","토굴","구글","토글링","토그","투글","투 글"};

            Arrays.sort(save); commandPool.put("Save", save);
            Arrays.sort(cancel); commandPool.put("Cancel", cancel);
            Arrays.sort(max); commandPool.put("Max", max);
            Arrays.sort(high); commandPool.put("High", high);
            Arrays.sort(medium); commandPool.put("Medium", medium);
            Arrays.sort(low); commandPool.put("Low", low);
            Arrays.sort(mute); commandPool.put("Mute", mute);
            Arrays.sort(card); commandPool.put("Card", card);
            Arrays.sort(full); commandPool.put("Full", full);
            Arrays.sort(toggle); commandPool.put("Toggle", toggle);


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
        protected void setVoiceCommandAction(String filteredResult) {

            Log.e("ASROption", filteredResult);
            _srLabel.setText("Recognized Command : ");
            _srVoice.setText(filteredResult);

            if (filteredResult.equals("Save"))
                _optSave.callOnClick();
            else if (filteredResult.equals("Cancel"))
                _optCancel.callOnClick();
            else if (filteredResult.equals("Max"))
                _optVolume.check(R.id.volMax);
            else if (filteredResult.equals("High"))
                _optVolume.check(R.id.volHigh);
            else if (filteredResult.equals("Medium"))
                _optVolume.check(R.id.volMedium);
            else if (filteredResult.equals("Low"))
                _optVolume.check(R.id.volLow);
            else if (filteredResult.equals("Mute"))
                _optVolume.check(R.id.volMute);
            else if (filteredResult.equals("Card"))
                _optCard.callOnClick();
            else if (filteredResult.equals("Full"))
                _optFull.callOnClick();
            else if (filteredResult.equals("Toggle"))
                _optVoiceConfirm.toggle();

        }
    };

    public OptionDialog(Context context) {
        super(context);
        _context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_option);
        init();

    }

    private void setDialogAttributes() {

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.copyFrom(getWindow().getAttributes());
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount=0.6f;
        getWindow().setAttributes(lpWindow);
    }

    private void init(){
        voiceActivity.setCommandPool();
        voiceActivity.createRecognizer(OptionDialog.this.getContext());
        setDialogAttributes();
        _optSave = (Button)findViewById(R.id.optSave);
        _optCancel = (Button)findViewById(R.id.optCancel);
        _optVolume = (RadioGroup)findViewById(R.id.optSound);
        _optVoiceConfirm = (Switch)findViewById(R.id.optVoiceConfirm);
        _optCard = (LinearLayout)findViewById(R.id.optCard);
        _optFull = (LinearLayout)findViewById(R.id.optFull);
        _srLabel = (TextView)findViewById(R.id.srLabel);
        _srVoice = (TextView)findViewById(R.id.srVoice);

        _srLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceActivity.recognize();
            }
        });

        SharedPreferences pref = _context.getSharedPreferences("option", _context.MODE_PRIVATE);
        Option opt = Option.getInstance();
        opt.volLevel = pref.getInt("volLevel",3);
        opt.isCardView = pref.getBoolean("isCardView",true);
        opt.voiceConfirm = pref.getBoolean("voiceConfirm",true);

        //Save
        _optSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Option opt = Option.getInstance();
                int volumeLevel = 0;
                switch (_optVolume.getCheckedRadioButtonId()) {
                    case R.id.volMax:
                        volumeLevel = 4;break;
                    case R.id.volHigh:
                        volumeLevel = 3;break;
                    case R.id.volMedium:
                        volumeLevel = 2;break;
                    case R.id.volLow:
                        volumeLevel = 1;break;
                    case R.id.volMute:
                        volumeLevel = 0;break;
                }
                opt.setOption(volumeLevel, _optCard.getAlpha() == 1, _optVoiceConfirm.isChecked());
                saveOption();
                Toast.makeText(OptionDialog.this.getContext(), "Setting Saved.", Toast.LENGTH_SHORT).show();
                OptionDialog.this.dismiss();
            }
        });

        //Cancel
        _optCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialogOption();
                OptionDialog.this.dismiss();
            }
        });

        //CardView Selected
        _optCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _optCard.setAlpha(1);
                _optFull.setAlpha(0.5f);
            }
        });

        //FullView Selected
        _optFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _optCard.setAlpha(0.5f);
                _optFull.setAlpha(1f);
            }
        });

        setDialogOption();

    }

    private void setDialogOption(){
        Option opt = Option.getInstance();
        switch(opt.volLevel){
            case 4: _optVolume.check(R.id.volMax);break;
            case 3: _optVolume.check(R.id.volHigh);break;
            case 2: _optVolume.check(R.id.volMedium);break;
            case 1: _optVolume.check(R.id.volLow);break;
            case 0: _optVolume.check(R.id.volMute);break;
        }
        _optCard.setAlpha(1f - (opt.isCardView ? 0f : 0.5f));
        _optFull.setAlpha(0.5f + (opt.isCardView ? 0f : 0.5f));
        _optVoiceConfirm.setChecked(opt.voiceConfirm);
    }

    public void saveOption(){
        SharedPreferences pref = _context.getSharedPreferences("option", _context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Option opt = Option.getInstance();
        editor.putInt("volLevel", opt.volLevel);
        editor.putBoolean("isCardView", opt.isCardView);
        editor.putBoolean("voiceConfirm", opt.voiceConfirm);
        editor.commit();
    }

}

package net.ddns.frebern.chessaronline;

import android.content.Context;
import android.content.SharedPreferences;

public class Option{
    private static Option ourInstance = new Option();
    public int volLevel;
    public boolean isCardView;
    public boolean voiceConfirm;


    public static Option getInstance() {
        return ourInstance;
    }

    private Option() {

    }

    public void setOption(int level, boolean view, boolean confirm){
        volLevel =level;
        isCardView=view;
        voiceConfirm=confirm;
    }

}

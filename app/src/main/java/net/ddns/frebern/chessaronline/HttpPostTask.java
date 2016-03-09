package net.ddns.frebern.chessaronline;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


import javax.net.ssl.HttpsURLConnection;

public class HttpPostTask {

    private static HttpPostTask instance = new HttpPostTask();
    private HttpURLConnection conn;

    public static HttpPostTask getInstance() {
        return instance;
    }

    private HttpPostTask(){

    }

    private String getURLQuery(ContentValues params){
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;

        for (String key : params.keySet())
        {
            if (first)
                first = false;
            else
                stringBuilder.append("&");

            try {
                stringBuilder.append(URLEncoder.encode(key, "EUC-KR"));
                stringBuilder.append("=");
                stringBuilder.append(URLEncoder.encode(params.getAsString(key), "EUC-KR"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private String getJson(ContentValues params){
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String key : params.keySet())
        {
            if (first)
                first = false;
            else
                stringBuilder.append(",");

            try {
                stringBuilder.append(URLEncoder.encode(key, "EUC-KR"));
                stringBuilder.append(":");
                stringBuilder.append(URLEncoder.encode(params.getAsString(key), "EUC-KR"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return "{"+stringBuilder.toString()+"}";
    }



    public String sendRequest(String address, ContentValues values){
        String response="";
        try {

            String params = getURLQuery(values);

            Log.e("HTTP","params:"+params);
            URL url = new URL(address);
            Log.e("HTTP",address);
            conn = (HttpURLConnection)url.openConnection();
            Log.e("HTTP", "3");
            conn.setRequestMethod("POST");
            Log.e("HTTP", "4");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("HTTP", "5");
            conn.setRequestProperty("Content-Type", "application/json");
            Log.e("HTTP", "6");

            OutputStream os = conn.getOutputStream();
            Log.e("HTTP", "7");
            os.write(params.getBytes("euc-kr"));
            os.flush();
            os.close();
            Log.e("HTTP", "8");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "euc-kr"), conn.getContentLength());
            Log.e("HTTP", "9");
            String buf;
            while ((buf = br.readLine()) != null)
                response += buf;

        } catch (Exception e) {
            Log.e("HTTP",e.toString());
            response = "ERROR";
            e.printStackTrace();
        }
        finally {
            Log.e("HTTP","FINALLY");
            conn.disconnect();
        }
        Log.e("HTTP","END");
        return response;

    }

}
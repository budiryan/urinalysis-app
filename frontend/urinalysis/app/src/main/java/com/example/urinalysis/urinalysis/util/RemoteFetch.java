package com.example.urinalysis.urinalysis.util;
/*
 * Created by budiryan on 2/15/18.\
 * Class for interacting with Django backend server
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class RemoteFetch {
    private static final String TAG = "RemoteFetch";

    public static StringBuffer get(String requestUrl){
        try {

            URL url = new URL(String.format(requestUrl));

            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            // Create a new reader
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));


            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            return json;
        }catch(Exception e){
            Log.e(TAG, "Error at RemoteFetch", e);
            return null;
        }
    }

}

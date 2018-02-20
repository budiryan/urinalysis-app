package com.example.urinalysis.urinalysis; /**
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
    private static final String URINALYSIS_API =
            "https://urinalysis.herokuapp.com/api";

    public static JSONObject getAvgPerDay(int days, String category){
        try {
            String daysString = String.valueOf(days);
            String requestUrl = URINALYSIS_API + "/getavgperday" + "?days=" + daysString + "&category=" + category;
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


            JSONObject data = new JSONObject(json.toString());
            Log.d("tag", json.toString());

            // This value will be 404 if the request was not
            // successful


            return data;
        }catch(Exception e){
            Log.e("tag", "I got an error", e);
            return null;
        }
    }
}

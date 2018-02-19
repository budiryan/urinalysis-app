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

    public static JSONObject getJSON(){
        try {
            URL url = new URL(String.format(URINALYSIS_API));
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

            // This value will be 404 if the request was not
            // successful


            Log.d("tag", "run till here");
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            Log.e("tag", "I got an error", e);
            return null;
        }
    }
}

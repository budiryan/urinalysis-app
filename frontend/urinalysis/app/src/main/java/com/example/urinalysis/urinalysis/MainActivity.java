package com.example.urinalysis.urinalysis;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private LineChart chart;
    private int [] xAxis = {1,2,3};
    private int [] yAxis = {1,2,3};
    private void testJson(){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getJSON();
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Log.d("tag", "nothing happened");
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            Log.d("tag", json.toString());
                        }
                    });
                }

            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "Hello world!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = (LineChart) findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<Entry>();

        for(int i = 0 ; i < 3 ; i++)
            entries.add(new Entry(xAxis[i], yAxis[i]));

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        chart.invalidate();
    }
}

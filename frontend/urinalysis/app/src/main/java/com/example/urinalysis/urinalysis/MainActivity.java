package com.example.urinalysis.urinalysis;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Handler handler = new Handler();
    private LineChart chart;
    Float[] yAxis;
    String[] xAxis;

    private void drawChart(){
        new Thread(){
            public void run(){
                final JSONObject json = RemoteFetch.getAvgPerDay(14, "glucose");
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                            Log.d("tag", "nothing happened");
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            try {
                                // Parse Dates
                                JSONArray tempDates = json.getJSONArray("dates");
                                int lengthDates = tempDates.length();
                                if (lengthDates > 0) {
                                    xAxis = new String[lengthDates];
                                    for (int i = 0; i < lengthDates; i++) {
                                        xAxis[i] = tempDates.getString(i);
                                    }
                                }

                                // Parse the values
                                JSONArray tempValues = json.getJSONArray("values");
                                int lengthValues = tempValues.length();
                                if (lengthValues > 0) {
                                    yAxis = new Float[lengthValues];
                                    for (int i = 0; i < lengthValues; i++) {
                                        yAxis[i] = Float.parseFloat(tempValues.getString(i));
                                    }
                                }        chart = findViewById(R.id.chart);

                                List<Entry> entries = new ArrayList<Entry>();

                                for(int i = 0 ; i < xAxis.length ; i++)
                                    entries.add(new Entry(i, yAxis[i]));

                                LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

                                LineData lineData = new LineData(dataSet);

                                chart.setData(lineData);

                                chart.getAxisLeft().setDrawGridLines(false);
                                chart.getXAxis().setDrawGridLines(false);

                                chart.invalidate();

                            }
                            catch(Exception e){
                                Log.e("tag", "I got an error 2", e);
                            }
                        }
                    });
                }

            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawChart();
    }
}

package com.example.urinalysis.urinalysis;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by budiryan on 2/21/18.
 */

public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    private LineChart chart;
    private Float[] yAxis;
    private String[] xAxis;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawChart();
        }
    };

    public void drawChart(){
        chart = getView().findViewById(R.id.chart);
        chart.setNoDataText("");

        List<Entry> entries;

        entries = new ArrayList<Entry>();

        for(int i = 0 ; i < xAxis.length ; i++)
            entries.add(new Entry(i, yAxis[i]));

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);
//        LineData lineData = new LineData(generateLineDataSet(yVals, ContextCompat.getColor(getContext(), R.color.urinalysis_pink)));

        chart.setData(lineData);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.urinalysis_text_light));
        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.urinalysis_text_light));
        leftAxis.disableGridDashedLine();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLimitLinesBehindData(true);

        chart.getAxisRight().setEnabled(false);
        chart.setBackgroundColor(Color.parseColor("#FFFFFF"));
        chart.setGridBackgroundColor(Color.parseColor("#FFFFFF"));


        chart.invalidate();
    }

    private void getAvgPerDayData(){
        // Get the averages data from Backend
        final JSONObject json = RemoteFetch.getAvgPerDay(14, "glucose");

        if(json == null)
            Log.d("tag", "json is null");
        else {
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
                }
            }
            catch(Exception e){
                Log.e("tag", "Handler Main Activity error", e);
            }

        }

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_fragment, container, false);

        getAvgPerDayData();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                getAvgPerDayData();
                handler.sendEmptyMessage(0);
            }
        };
        Thread getAvgDataThread = new Thread(r);

        getAvgDataThread.start();
        return view;
    }
}

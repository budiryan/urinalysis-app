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
    private Float[] yVal;
    private String[] xVal;

    private List<String> xValues = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawChart(chart);
        }
    };

    public void drawChart(LineChart chart){
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

        LineData data;
        data = generateData();
        chart.setData(data);



        chart.invalidate();
    }

    private void getAvgPerDayData(){
        // Get the averages data from Backend Server, depending on the queried substance type
        final JSONObject json = RemoteFetch.getAvgPerDay(20, "glucose");

        if(json == null)
            Log.d("tag", "json is null");
        else {
            try {
                // Parse Dates
                JSONArray tempDates = json.getJSONArray("dates");
                int lengthDates = tempDates.length();
                if (lengthDates > 0) {
                    xVal = new String[lengthDates];
                    for (int i = 0; i < lengthDates; i++) {
                        xVal[i] = tempDates.getString(i);
                    }
                }

                // Parse the values
                JSONArray tempValues = json.getJSONArray("values");
                int lengthValues = tempValues.length();
                if (lengthValues > 0) {
                    yVal = new Float[lengthValues];
                    for (int i = 0; i < lengthValues; i++) {
                        yVal[i] = Float.parseFloat(tempValues.getString(i));
                    }
                }
            }
            catch(Exception e){
                Log.e("tag", "Handler Main Activity error", e);
            }

        }

    }

    private LineData generateData() {
        List<Entry> yValEntry = new ArrayList<>();

        // Day view
        for (int i = 0; i < yVal.length; i++) {
            yValEntry.add(new Entry(i, yVal[i]));
        }

        LineData data = new LineData(generateLineDataSet(yValEntry, ContextCompat.getColor(getContext(), R.color.urinalysis_pink)));
        return data;
    }


    private LineDataSet generateLineDataSet(List<Entry> vals, int color) {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals, "");
        List<Integer> colors = new ArrayList<>();

        if (color == ContextCompat.getColor(getContext(), R.color.urinalysis_pink)) {
            for (Entry val : vals) {
                if (val.getY() == (0)) {
                    colors.add(Color.TRANSPARENT);
                } else {
                    colors.add(color);
                }
            }
            set1.setCircleColors(colors);
        } else {
            set1.setCircleColor(color);
        }

        set1.setColor(color);
        set1.setLineWidth(2f);
        set1.setCircleSize(4f);
        set1.setDrawCircleHole(true);
        set1.disableDashedLine();
        set1.setFillAlpha(255);
        set1.setDrawFilled(true);
        set1.setValueTextSize(0);
        set1.setValueTextColor(Color.parseColor("#FFFFFF"));
        set1.setFillDrawable(getResources().getDrawable(R.drawable.graph_gradient));
        set1.setHighLightColor(ContextCompat.getColor(getContext(), R.color.urinalysis_gray_light));
        set1.setCubicIntensity(0.2f);
        return set1;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        chart = view.findViewById(R.id.chart);
        chart.setNoDataText("");

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

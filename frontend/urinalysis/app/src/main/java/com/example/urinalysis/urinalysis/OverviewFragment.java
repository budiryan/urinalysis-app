package com.example.urinalysis.urinalysis;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.urinalysis.urinalysis.util.RemoteFetch;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.example.urinalysis.urinalysis.util.MyXAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by budiryan on 2/21/18.
 */

public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";
    private static final String URINALYSIS_API = "https://urinalysis.herokuapp.com/api";
    private LineChart chart;
    private Float[] yVal;
    private String[] xVal;
    private List<String> categories;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawChart(chart);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_fragment, container, false);
        chart = view.findViewById(R.id.chart);
        chart.setNoDataText("");

        // Spinner element
        Spinner spinner = (Spinner) view.findViewById(R.id.chart_spinner_range);

        // Spinner click listener
//        spinner.setOnItemSelectedListener(getActivity());

        // Spinner Drop down elements
        List<String> categories2 = new ArrayList<String>();
        categories2.add("Automobile");
        categories2.add("Business Services");
        categories2.add("Computers");
        categories2.add("Education");
        categories2.add("Personal");
        categories2.add("Travel");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories2);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        // Populate graph data from backend
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getAllCategories();
                getAvgPerDayData(14, "glucose");
                handler.sendEmptyMessage(0);
            }
        };
        Thread getAvgDataThread = new Thread(r);

        getAvgDataThread.start();
        return view;
    }

    private void getAllCategories(){
        // Get the substance categories from Backend Server
        String requestUrl = URINALYSIS_API + "/category";
        categories = new ArrayList<String>();
        final StringBuffer data = RemoteFetch.get(requestUrl);
        JSONArray json;
        try {
            json = new JSONArray(data.toString());
        }
        catch (Exception e){
            json = null;
            Log.e(TAG, "RemoteFetch getAllCategories error", e);
        }


        if(json == null)
            Log.d(TAG, "json is null in getAllCategories");
        else {
            try {
                // Parse all categories from json
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonobject = json.getJSONObject(i);
                    String name = jsonobject.getString("name");
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    categories.add(name);
                }

            }
            catch(Exception e){
                Log.e(TAG, "GetAllCategories Error", e);
            }

        }
    }

    private void getAvgPerDayData(int days, String category){
        // Get the averages data from Backend Server, depending on the queried substance type
        String daysString = String.valueOf(days);
        String requestUrl = URINALYSIS_API + "/getavgperday" + "?days=" + daysString + "&category=" + category;
        final StringBuffer data = RemoteFetch.get(requestUrl);
        JSONObject json;
        try{
            json = new JSONObject(data.toString());
        }
        catch (Exception e){
            json = null;
            Log.e(TAG, "getAvgPerDayData Json Parsing Error");
        }
        Log.d(TAG, json.toString());

        if(json == null)
            Log.d(TAG, "json is null");
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
                Log.e(TAG, "GetAvgPerDayData Error", e);
            }

        }

    }

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

        Legend legend = chart.getLegend();
        legend.setEnabled(false);
        setData();

    }


    private void setData() {
        LineData data;
        data = generateData();

        if (data.getEntryCount() != 0) {
            chart.setData(data);
        } else {
            chart.setData(null);
        }
        chart.setPinchZoom(true);
        chart.setHardwareAccelerationEnabled(true);
        chart.setNoDataTextColor(getResources().getColor(R.color.urinalysis_text));
        chart.animateY(1000, Easing.EasingOption.EaseOutCubic);
        chart.invalidate();
        chart.notifyDataSetChanged();
        chart.fitScreen();
        chart.setDescription(null);
        chart.setVisibleXRangeMaximum(20);
        chart.moveViewToX(data.getXMax());

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(new MyXAxisValueFormatter(xVal));

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



}

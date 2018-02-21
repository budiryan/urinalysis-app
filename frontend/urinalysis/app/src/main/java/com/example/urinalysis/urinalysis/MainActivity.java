package com.example.urinalysis.urinalysis;

import android.support.design.widget.TabLayout;

import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TableLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private Float[] yAxis;
    private String[] xAxis;
    private static final String TAG = "MainActivity";

    private SectionPageAdapter mSectionPageAdapter;

    private ViewPager mViewPager;

    private Toolbar toolbar;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            drawChart();
        }
    };

    private void setupViewPager(ViewPager viewPager){
        SectionPageAdapter adapter = new SectionPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new OverviewFragment(), "Overview");
        adapter.addFragment(new HistoryFragment(), "History");
        adapter.addFragment(new ConnectFragment(), "Connect");
        viewPager.setAdapter(adapter);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: starting...");


        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("FYP Urinalysis");



//        getAvgPerDayData();
//        Runnable r = new Runnable() {
//            @Override
//            public void run() {
//                getAvgPerDayData();
//                handler.sendEmptyMessage(0);
//            }
//        };
//        Thread getAvgDataThread = new Thread(r);
//        getAvgDataThread.start();

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

    public void drawChart(){
        chart = findViewById(R.id.chart);

        List<Entry> entries;

        entries = new ArrayList<Entry>();

        for(int i = 0 ; i < xAxis.length ; i++)
            entries.add(new Entry(i, yAxis[i]));

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset

        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);

        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);

        chart.invalidate();
    }
}

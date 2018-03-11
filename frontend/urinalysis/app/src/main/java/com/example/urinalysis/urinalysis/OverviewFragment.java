package com.example.urinalysis.urinalysis;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.models.AveragesPerDay;
import com.example.urinalysis.urinalysis.models.Category;
import com.example.urinalysis.urinalysis.models.Stats;
import com.example.urinalysis.urinalysis.models.User;
import com.example.urinalysis.urinalysis.models.UserCategory;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.example.urinalysis.urinalysis.util.MyXAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by budiryan on 2/21/18.
 */

public class OverviewFragment extends Fragment {
    private static final String TAG = "OverviewFragment";

    // For storing the chart data
    private LineChart chart;
    private Float[] yVal;
    private String[] xVal;
    private String unit;

    // Data for the spinner
    private String[] categories;
    private String currentCategory;

    // Data for user spinner
    private String[] users;
    private String currentUser;

    // For calling the API through retrofit
    private Api api;
    private Retrofit retrofit;

    // For the statistics stuff
    private Float avg;
    private Float std;
    private Float latest;
    private String latestDate;
    private String latestTime;
    private Float highest;
    private String highestDate;
    private String highestTime;
    private Float lowest;
    private String lowestDate;
    private String lowestTime;

    // For the view of the statistics
    private TextView avgVal;
    private TextView stdVal;
    private TextView lowestVal;
    private TextView lowestDateView;
    private TextView highestVal;
    private TextView highestDateView;
    private TextView latestVal;
    private TextView latestDateView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_fragment, container, false);

        // Some initalization
        chart = view.findViewById(R.id.chart);
        chart.setNoDataText("");
        final Spinner spinner = view.findViewById(R.id.spinner1);
        final Spinner spinnerUser = view.findViewById(R.id.spinner2);
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        avgVal = view.findViewById(R.id.average_value);
        stdVal = view.findViewById(R.id.std_value);
        lowestVal = view.findViewById(R.id.lowest_value);
        lowestDateView = view.findViewById(R.id.lowest_date);
        highestVal = view.findViewById(R.id.highest_value);
        highestDateView = view.findViewById(R.id.highest_date);
        latestVal = view.findViewById(R.id.last_reading_value);
        latestDateView = view.findViewById(R.id.last_reading_date);

        // Call to get the list of categories
        Call <UserCategory> call_categories = api.getUserCategory();

        call_categories.enqueue(new Callback<UserCategory>() {
            @Override
            public void onResponse(Call<UserCategory> call, Response<UserCategory> response) {
                UserCategory userCategory = response.body();
                users = userCategory.getUsers();
                categories = userCategory.getCategories();

                // Array adapter for categories
                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.spinner_item, categories);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(spinnerArrayAdapter);

                // Array adapter for users
                final ArrayAdapter<String> spinnerArrayAdapterUser = new ArrayAdapter<String>(
                        getActivity(), R.layout.spinner_item, users);
                spinnerArrayAdapterUser.setDropDownViewResource(R.layout.spinner_item);
                spinnerUser.setAdapter(spinnerArrayAdapterUser);
            }

            @Override
            public void onFailure(Call<UserCategory> call, Throwable t) {
//                Toast.makeText(getActivity().getApplicationContext(),
//                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                currentCategory = parent.getItemAtPosition(position).toString();

                if (currentUser == null)
                    currentUser = spinnerUser.getItemAtPosition(0).toString();

                // Call to get graph data
                Call<AveragesPerDay> call_graph = api.getAveragePerDay(currentCategory, currentUser);

                // Generate the graph
                call_graph.enqueue(new Callback<AveragesPerDay>() {
                    @Override
                    public void onResponse(Call<AveragesPerDay> call, Response<AveragesPerDay> response) {
                        AveragesPerDay avgPerDay = response.body();
                        xVal = avgPerDay.getDates();
                        yVal = avgPerDay.getValues();
                        unit = avgPerDay.getUnit();
                        drawChart(chart);
                    }

                    @Override
                    public void onFailure(Call<AveragesPerDay> call, Throwable t) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Fill in the statistics section below graph
                Call<Stats> call_stats = api.getStats(currentCategory, currentUser);
                call_stats.enqueue(new Callback<Stats>() {
                    @Override
                    public void onResponse(Call<Stats> call, Response<Stats> response) {
                        Stats statsDay = response.body();
                        avg = statsDay.getAvg();
                        std = statsDay.getStd();
                        latest = statsDay.getLatest();
                        latestDate = statsDay.getLatest_date();
                        latestTime = statsDay.getLatest_time();
                        highest = statsDay.getHighest();
                        highestDate = statsDay.getHighest_date();
                        highestTime = statsDay.getHighest_time();
                        lowest = statsDay.getLowest();
                        lowestDate = statsDay.getLowest_date();
                        lowestTime = statsDay.getLowest_time();
                        unit = statsDay.getUnit();

                        avgVal.setText(String.format("%s %s", avg.toString(), unit));
                        stdVal.setText(String.format("%s %s", std.toString(), unit));
                        lowestVal.setText(String.format("%s %s", lowest.toString(), unit));
                        highestVal.setText(String.format("%s %s", highest.toString(), unit));
                        latestVal.setText(String.format("%s %s", latest.toString(), unit));

                        latestDateView.setText(String.format("%s %s", latestDate, latestTime));
                        highestDateView.setText(String.format("%s %s", highestDate, highestTime));
                        lowestDateView.setText(String.format("%s %s", lowestDate, lowestTime));

                    }

                    @Override
                    public void onFailure(Call<Stats> call, Throwable t) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });


        // Spinner click listener
        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                currentUser = parent.getItemAtPosition(position).toString();
                if (currentCategory == null)
                    currentCategory = spinner.getItemAtPosition(0).toString();

                // Call to get graph data
                Call<AveragesPerDay> call_graph = api.getAveragePerDay(currentCategory, currentUser);

                // Generate the graph
                call_graph.enqueue(new Callback<AveragesPerDay>() {
                    @Override
                    public void onResponse(Call<AveragesPerDay> call, Response<AveragesPerDay> response) {
                        AveragesPerDay avgPerDay = response.body();
                        xVal = avgPerDay.getDates();
                        yVal = avgPerDay.getValues();
                        unit = avgPerDay.getUnit();
                        drawChart(chart);
                    }

                    @Override
                    public void onFailure(Call<AveragesPerDay> call, Throwable t) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // Fill in the statistics section below graph
                Call<Stats> call_stats = api.getStats(currentCategory, currentUser);
                call_stats.enqueue(new Callback<Stats>() {
                    @Override
                    public void onResponse(Call<Stats> call, Response<Stats> response) {
                        Stats statsDay = response.body();
                        avg = statsDay.getAvg();
                        std = statsDay.getStd();
                        latest = statsDay.getLatest();
                        latestDate = statsDay.getLatest_date();
                        latestTime = statsDay.getLatest_time();
                        highest = statsDay.getHighest();
                        highestDate = statsDay.getHighest_date();
                        highestTime = statsDay.getHighest_time();
                        lowest = statsDay.getLowest();
                        lowestDate = statsDay.getLowest_date();
                        lowestTime = statsDay.getLowest_time();
                        unit = statsDay.getUnit();

                        avgVal.setText(String.format("%s %s", avg.toString(), unit));
                        stdVal.setText(String.format("%s %s", std.toString(), unit));
                        lowestVal.setText(String.format("%s %s", lowest.toString(), unit));
                        highestVal.setText(String.format("%s %s", highest.toString(), unit));
                        latestVal.setText(String.format("%s %s", latest.toString(), unit));

                        latestDateView.setText(String.format("%s %s", latestDate, latestTime));
                        highestDateView.setText(String.format("%s %s", highestDate, highestTime));
                        lowestDateView.setText(String.format("%s %s", lowestDate, lowestTime));

                    }

                    @Override
                    public void onFailure(Call<Stats> call, Throwable t) {
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });



        return view;
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
        setChartData();

    }


    private void setChartData() {
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

        LineData data = new LineData(generateLineDataSet(yValEntry,
                ContextCompat.getColor(getContext(), R.color.urinalysis_pink)));
        return data;
    }


    private LineDataSet generateLineDataSet(List<Entry> vals, int color) {
        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals, unit);
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

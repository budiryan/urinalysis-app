package com.example.urinalysis.urinalysis;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.models.AveragesPerDay;
import com.example.urinalysis.urinalysis.models.Category;
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
    private ArrayList<String> categories;

    // For calling the API through retrofit
    private Api api;
    private Retrofit retrofit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.overview_fragment, container, false);

        // Some initalization
        chart = view.findViewById(R.id.chart);
        chart.setNoDataText("");
        categories = new ArrayList<String>();
        final Spinner spinner = view.findViewById(R.id.spinner1);
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        // Call to get the list of categories
        Call <List<Category>> call_categories = api.getCategories();
        call_categories.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                List<Category> categoryBody = response.body();
                for(Category c: categoryBody){
                    categories.add(c.getName().substring(0, 1).toUpperCase()
                            + c.getName().substring(1));
                }
                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.spinner_item, categories);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(),
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                String item = parent.getItemAtPosition(position).toString();

                // Call to get graph data
                Call<AveragesPerDay> call_graph = api.getAveragePerDay(
                        item.substring(0, 1).toLowerCase() + item.substring(1));
                // Call the backend asynchronously
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
                        Toast.makeText(getActivity().getApplicationContext(),
                                t.getMessage(), Toast.LENGTH_SHORT).show();
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

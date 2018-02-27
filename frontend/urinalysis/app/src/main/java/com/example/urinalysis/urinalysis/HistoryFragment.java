package com.example.urinalysis.urinalysis;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.adapters.HistoryAdapter;
import com.example.urinalysis.urinalysis.models.AveragesPerDay;
import com.example.urinalysis.urinalysis.models.Category;
import com.example.urinalysis.urinalysis.models.Stats;
import com.example.urinalysis.urinalysis.models.Substance;
import com.example.urinalysis.urinalysis.models.Unit;

import java.lang.reflect.Array;
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

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    // For calling the API through retrofit
    private Api api;
    private Retrofit retrofit;

    // Data for the spinner
    private ArrayList<String> categories;
    private ArrayList<Integer> categoriesId;

    // Data for sending post request
    private ArrayList<Integer> unitsId;
    private ArrayList<String> units;

    // Data to display on the body
    private List<Substance> substances;

    // Recycle view
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        categories = new ArrayList<>();
        categoriesId = new ArrayList<>();
        units =  new ArrayList<>();
        unitsId = new ArrayList<>();
        final Spinner spinner = view.findViewById(R.id.spinner_history);
        final Context context = getActivity().getApplicationContext();

        mRecyclerView =  view.findViewById(R.id.fragment_history_recycler_view);
        mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Call to get the list of categories
        Call<List<Unit>> call_categories = api.getUnits();
        call_categories.enqueue(new Callback<List<Unit>>() {
            @Override
            public void onResponse(Call<List<Unit>> call, Response<List<Unit>> response) {
                List<Unit> uniBody = response.body();
                for(Unit c: uniBody){
                    categories.add(c.getCategoryName().substring(0, 1).toUpperCase()
                            + c.getCategoryName().substring(1));
                    categoriesId.add(c.getCategory());
                    units.add(c.getName());
                    unitsId.add(c.getId());
                }
                final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                        getActivity(), R.layout.spinner_item, categories);
                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                spinner.setAdapter(spinnerArrayAdapter);
            }

            @Override
            public void onFailure(Call<List<Unit>> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(),
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                String category = parent.getItemAtPosition(position).toString();
                String query_category = category.substring(0, 1).toLowerCase() +
                        category.substring(1);

                // Call to get the history given that category
                Call<List<Substance>> call_categories = api.getSubstance(query_category, null);
                call_categories.enqueue(new Callback<List<Substance>>() {
                    @Override
                    public void onResponse(Call<List<Substance>> call, Response<List<Substance>> response) {
                        substances = response.body();
                        mAdapter = new HistoryAdapter(context, substances);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<Substance>> call, Throwable t) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//                Call<Substance> save_substance = api.saveSubstance((float)8.0, unitsId.get(0), categoriesId.get(0), "Test posting from android!");
//                save_substance.enqueue(new Callback<Substance>() {
//                    @Override
//                    public void onResponse(Call<Substance> call, Response<Substance> response) {
//                        Log.i(TAG, "post submitted to API.");
//                        Log.d(TAG, "response is: " + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<Substance> call, Throwable t) {
//                        Log.d(TAG, "ERROR SENDING");
//                    }
//                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        return view;
    }
}

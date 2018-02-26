package com.example.urinalysis.urinalysis;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.models.AveragesPerDay;
import com.example.urinalysis.urinalysis.models.Category;
import com.example.urinalysis.urinalysis.models.Stats;

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        categories = new ArrayList<String>();
        final Spinner spinner = view.findViewById(R.id.spinner_history);

        // Call to get the list of categories
        Call<List<Category>> call_categories = api.getCategories();
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
                String category = parent.getItemAtPosition(position).toString();
                String query_category = category.substring(0, 1).toLowerCase() +
                        category.substring(1);

                


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        return view;
    }
}

package com.example.urinalysis.urinalysis;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.example.urinalysis.urinalysis.models.UserCategory;

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

public class HistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "HistoryFragment";
    // For calling the API through retrofit
    private Api api;
    private Retrofit retrofit;

    // Data for the spinner
    private String[] categories;
    private String[] users;
    private String currentCategory;
    private String currentUser;
    private ArrayList<Integer> categoriesId;

    // Data to display on the body
    private List<Substance> substances;

    // Recycle view
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    SwipeRefreshLayout swipeLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);
        final Spinner spinner = view.findViewById(R.id.spinner_history);
        final Spinner spinnerUser = view.findViewById(R.id.spinner_history_user);

        final Context context = getActivity().getApplicationContext();

        mRecyclerView =  view.findViewById(R.id.fragment_history_recycler_view);
        mLayoutManager = new LinearLayoutManager(super.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(this);

        // Call to get the list of categories
        Call<UserCategory> callUserCategory = api.getUserCategory();

        callUserCategory.enqueue(new Callback<UserCategory>() {
            @Override
            public void onResponse(Call<UserCategory> call, Response<UserCategory> response) {
                UserCategory userCategory = response.body();
                categories = userCategory.getCategories();
                users = userCategory.getUsers();

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
                // Do nothing
            }
        });

        // Spinner click listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                currentCategory = parent.getItemAtPosition(position).toString();

                if(currentUser == null)
                    currentUser = spinnerUser.getItemAtPosition(0).toString();

                // Call to get the history given that category
                Call<List<Substance>> call_categories = api.getSubstance(currentCategory, null, currentUser);

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

                if(currentCategory == null)
                    currentCategory = spinner.getItemAtPosition(0).toString();

                // Call to get the history given that category
                Call<List<Substance>> call_categories = api.getSubstance(currentCategory, null, currentUser);

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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}

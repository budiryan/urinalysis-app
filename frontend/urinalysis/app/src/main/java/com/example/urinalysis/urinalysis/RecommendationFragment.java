package com.example.urinalysis.urinalysis;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.adapters.HistoryAdapter;
import com.example.urinalysis.urinalysis.models.Substance;
import com.example.urinalysis.urinalysis.models.SuggestedWaterIntake;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Budi Ryan on 02-Apr-18.
 */

public class RecommendationFragment extends Fragment {
    private static final String TAG = "RecommendationFragment";

    // Attributes
    private Float suggestedWaterIntake;

    // Progressbar GUI
    private ProgressBar progressBar;

    // For calling the API through retrofit
    private Api api;
    private Retrofit retrofit;

    // Suggested Water Intake
    private TextView messageText;
    private TextView suggestedWaterIntakeText;

    // Text message to be shown
    private static final String okayMessage = "You are hydrated, you are consuming enough water!" ;
    private static final String notOkayMessage = "You are dehydrated, you should increase your water consumption by: ";

    // List of users for user spinner
    private final String[] users = {"user1", "user2", "user3"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommendation_fragment, container, false);

        final Spinner spinnerUser = view.findViewById(R.id.spinner1);
        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        messageText = view.findViewById(R.id.message);
        suggestedWaterIntakeText = view.findViewById(R.id.water_intake);

        progressBar = view.findViewById(R.id.progressBar);

        // Array adapter for users
        final ArrayAdapter<String> spinnerArrayAdapterUser = new ArrayAdapter<String>(
                getActivity(), R.layout.spinner_item, users);
        spinnerArrayAdapterUser.setDropDownViewResource(R.layout.spinner_item);
        spinnerUser.setAdapter(spinnerArrayAdapterUser);


        spinnerUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                // On selecting a spinner item
                final String currentUser = parent.getItemAtPosition(position).toString();

                Call<SuggestedWaterIntake> call_suggestedwaterintake = api.getSuggestedWaterIntake(currentUser);

                call_suggestedwaterintake.enqueue(new Callback<SuggestedWaterIntake>() {
                    @Override
                    public void onResponse(Call<SuggestedWaterIntake> call, Response<SuggestedWaterIntake> response) {
                        progressBar.setVisibility(View.GONE);
                        SuggestedWaterIntake suggestedWaterIntakeCall = response.body();
                        suggestedWaterIntake = suggestedWaterIntakeCall.getSuggestedWaterIntake();

                        if (suggestedWaterIntake <= 0.0){
                            messageText.setText(okayMessage);
                            suggestedWaterIntakeText.setText("");
                        }
                        else {
                            messageText.setText(notOkayMessage);
                            suggestedWaterIntakeText.setText(String.valueOf(suggestedWaterIntake + " mL"));
                        }
                    }

                    @Override
                    public void onFailure(Call<SuggestedWaterIntake> call, Throwable t) {
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
}

package com.example.urinalysis.urinalysis;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.urinalysis.urinalysis.models.SuggestedWaterIntake;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommendation_fragment, container, false);

        retrofit = new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(Api.class);

        messageText = view.findViewById(R.id.message);
        suggestedWaterIntakeText = view.findViewById(R.id.water_intake);

        progressBar = view.findViewById(R.id.progressBar);

        // Call to get the list of categories
        Call<SuggestedWaterIntake> call_suggestedwaterintake = api.getSuggestedWaterIntake();

        call_suggestedwaterintake.enqueue(new Callback<SuggestedWaterIntake>() {
            @Override
            public void onResponse(Call<SuggestedWaterIntake> call, Response<SuggestedWaterIntake> response) {
                progressBar.setVisibility(View.GONE);
                SuggestedWaterIntake suggestedWaterIntakeCall = response.body();
                suggestedWaterIntake = suggestedWaterIntakeCall.getSuggestedWaterIntake();

                if (suggestedWaterIntake < 0.0){
                    messageText.setText(okayMessage);
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

        return view;
    }
}

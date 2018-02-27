package com.example.urinalysis.urinalysis;

import com.example.urinalysis.urinalysis.models.AveragesPerDay;
import com.example.urinalysis.urinalysis.models.Category;
import com.example.urinalysis.urinalysis.models.Stats;
import com.example.urinalysis.urinalysis.models.Substance;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by budiryan on 2/23/18.
 */

public interface Api {
    String BASE_URL = "https://urinalysis.herokuapp.com/api/";

    @GET("category")
    Call<List<Category>> getCategories();

    @GET("substance")
    Call<List<Substance>> getSubstance(@Query("category") String category,
                                       @Query("num") Integer num);

    @GET("getavgperday")
    Call<AveragesPerDay> getAveragePerDay(@Query("category") String category);

    @GET("getstats")
    Call<Stats> getStats(@Query("category") String category);


}

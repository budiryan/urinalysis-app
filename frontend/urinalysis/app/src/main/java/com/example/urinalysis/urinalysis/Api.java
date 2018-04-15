package com.example.urinalysis.urinalysis;

import com.example.urinalysis.urinalysis.models.AveragesPerDay;
import com.example.urinalysis.urinalysis.models.Category;
import com.example.urinalysis.urinalysis.models.Stats;
import com.example.urinalysis.urinalysis.models.Substance;
import com.example.urinalysis.urinalysis.models.SuggestedWaterIntake;
import com.example.urinalysis.urinalysis.models.Unit;
import com.example.urinalysis.urinalysis.models.User;
import com.example.urinalysis.urinalysis.models.UserCategory;
import com.example.urinalysis.urinalysis.models.UserCategoryUnit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by budiryan on 2/23/18.
 */

public interface Api {
    String BASE_URL = "https://urinalysis.herokuapp.com/api/";

    @GET("category")
    Call<List<Category>> getCategories();

    @GET("unit")
    Call<List<Unit>> getUnits();

    @GET("user")
    Call<List<User>> getUsers();

    @GET("substance")
    Call<List<Substance>> getSubstance(@Query("category") String category,
                                       @Query("num") Integer num,
                                       @Query("user") String user);

    @GET("getavgperday")
    Call<AveragesPerDay> getAveragePerDay(@Query("category") String category,
                                          @Query("user") String user);

    @GET("getusercategory")
    Call<UserCategory> getUserCategory();

    @GET("getusercategoryunit")
    Call<UserCategoryUnit> getUserCategoryUnit();

    @GET("getstats")
    Call<Stats> getStats(@Query("category") String category,
                         @Query("user") String user);

    @GET("suggestedwaterintake")
    Call<SuggestedWaterIntake> getSuggestedWaterIntake(@Query("user") String user);

    @POST("substance")
    @FormUrlEncoded
    Call<Substance> saveSubstance(@Field("value") Float value, @Field("unit") Integer unit,
                        @Field("category") Integer category, @Field("user") Integer user,
                        @Field("notes") String notes);

}

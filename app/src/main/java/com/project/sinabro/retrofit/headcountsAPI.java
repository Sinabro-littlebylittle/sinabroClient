package com.project.sinabro.retrofit;

import com.project.sinabro.models.Place;
import com.project.sinabro.models.Headcount;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface headcountsAPI {
    @GET("/api/headcounts/public/placeInformations")
    Call<List<Headcount>> getPlaceInformations();

    @GET("/api/headcounts/public/{id}/placeInformations")
    Call<List<Headcount>> getPlaceInformationsById(@Path("id") String id);

    @POST("/api/headcounts/private")
    Call<Place> addPeopleNumber(@Body Headcount peopleNumber);
}
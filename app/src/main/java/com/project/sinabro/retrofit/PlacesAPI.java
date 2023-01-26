package com.project.sinabro.retrofit;

import com.project.sinabro.model.Places;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PlacesAPI {
    @GET("/api/places")
    Call<List<Places>> getAllEmployees();

    @POST("/api/places/save")
    Call<Places> save(@Body Places places);
}

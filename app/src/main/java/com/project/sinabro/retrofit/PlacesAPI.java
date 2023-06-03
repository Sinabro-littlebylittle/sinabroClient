package com.project.sinabro.retrofit;

import com.project.sinabro.models.Place;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PlacesAPI {
    @GET("/places")
    Call<List<Place>> getPlaces();

    @POST("/places")
    Call<Place> addPlaceInformation(@Body Place place);

    @PATCH("/places/{id}")
    Call<Place> updatePlace(@Path("id") String id, @Body Place place);

    @DELETE("/places/{id}")
    Call<Integer> deletePlace(@Path("id") String id);
}

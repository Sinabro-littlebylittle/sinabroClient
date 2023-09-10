package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.SearchHistory;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SearchHistoriesAPI {
    @GET("/api/search-histories/private")
    Call<List<SearchHistory>> getSearchHistories();

    @POST("/api/search-histories/private")
    Call<SearchHistory> postSearchHistory(@Body SearchHistory searchHistory);

    @DELETE("/api/search-histories/private/{searchHistoryId}")
    Call<ResponseBody> deleteSearchHistory(@Path("searchHistoryId") String searchHistoryId);
}

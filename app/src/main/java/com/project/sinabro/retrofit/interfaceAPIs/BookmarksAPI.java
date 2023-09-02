package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.Bookmark;
import com.project.sinabro.models.Headcount;
import com.project.sinabro.models.requests.PlaceIdRequest;

import java.util.List;

import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookmarksAPI {
    @GET("/api/bookmarks/private")
    Call<List<Bookmark>> getBookmarkList();

    @GET("/api/bookmarks/private/bookmarkedPlace/bookmarks/{bookmarkId}")
    Call<List<Headcount>> getBookmarkedPlaceInformations(@Path("bookmarkId") String bookmarkId);

    @GET("/api/bookmarks/private/bookmarkedPlace/places/{placeId}")
    Call<List<Bookmark>> getBookmarkedListsForPlace(@Path("placeId") String placeId);

    @POST("/api/bookmarks/private")
    Call<Bookmark> addBookmarkList(@Body Bookmark bookmark);

    @POST("/api/bookmarks/private/bookmarkedPlace/places/{placeId}")
    Call<ResponseBody> addBookmarkedPlaceInBookmarkList(@Path("placeId") String placeId,
                                                        @Body List<String> bookmarkIds);

    @HTTP(method = "DELETE", path = "/api/bookmarks/private", hasBody = true)
    Call<ResponseBody> deleteBookmarkLists(@Body List<String> bookmarkIds);

    @HTTP(method = "DELETE", path = "/api/bookmarks/private/bookmarkedPlace/places/{placeId}", hasBody = true)
    Call<ResponseBody> deleteBookmarkedPlaceInBookmarkListForPlace(@Path("placeId") String placeId, @Body List<String> bookmarkIds);

    @PATCH("/api/bookmarks/private/bookmarks/{bookmarkId}")
    Call<ResponseBody> updateBookmarkList(@Path("bookmarkId") String bookmarkId, @Body Bookmark bookmark);
}

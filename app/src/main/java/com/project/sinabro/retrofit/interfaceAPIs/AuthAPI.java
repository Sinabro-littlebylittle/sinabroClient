package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.requests.LoginRequest;
import com.project.sinabro.models.UserInfo;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthAPI {
    @GET("/api/auth/public/search")
    Call<ResponseBody> checkExistEmail(@Query("email") String email);

    @POST("/api/auth/public/login")
    Call<ResponseBody> login(@Body LoginRequest loginRequest);

    @POST("/api/auth/public/sign-up")
    Call<UserInfo> signUp(@Body UserInfo userinfo);
}

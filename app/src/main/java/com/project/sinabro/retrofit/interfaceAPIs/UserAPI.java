package com.project.sinabro.retrofit.interfaceAPIs;

import com.project.sinabro.models.UserInfo;
import com.project.sinabro.models.requests.ChangePasswordRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;

public interface UserAPI {
    @GET("/api/user/private/info")
    Call<ResponseBody> getUserSelfInfo();

    @PATCH("/api/user/private/point")
    Call<ResponseBody> changePoint(@Body UserInfo user);

    @PATCH("/api/user/private/info")
    Call<ResponseBody> changeUserSelfInfo(@Body UserInfo userInfo);

    @PATCH("api/user/private/password")
    Call<ResponseBody> changePassword(@Body ChangePasswordRequest request);
}

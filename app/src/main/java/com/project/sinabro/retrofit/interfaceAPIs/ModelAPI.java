package com.project.sinabro.retrofit.interfaceAPIs;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ModelAPI {
    @Multipart
    @POST("inference")
    Call<ResponseBody> uploadVideo(@Part MultipartBody.Part file);
}

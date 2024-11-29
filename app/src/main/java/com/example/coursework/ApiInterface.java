package com.example.coursework;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers({
            "Content-Type: application/json"  // Ensure the content type is JSON
    })
    @POST("/upload")
    Call<Upload> getUserInformation(@Body JSONObject jsonPayload);
}
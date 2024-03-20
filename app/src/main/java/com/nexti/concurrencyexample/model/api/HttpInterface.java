package com.nexti.concurrencyexample.model.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HttpInterface {
    @GET("delay")
    Call<ResponseBody> getDelayedResponse(@Query("seconds") Long seconds);
}

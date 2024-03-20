package com.nexti.concurrencyexample.model

import com.nexti.concurrencyexample.view.ResponseListener
import com.nexti.concurrencyexample.model.api.HttpInterface
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

object KotlinModel {
    fun makeAsyncRequest(delay: Long, responseListener: ResponseListener, httpInterface: HttpInterface) {
        GlobalScope.launch {
            try {
                val t0: Long = System.currentTimeMillis()
                val response: Response<ResponseBody> = httpInterface.getDelayedResponse(delay).execute()
                val t1: Long = System.currentTimeMillis()

                if (response.body() != null && response.code() == 200) {
                    responseListener.processResult(
                        ResponseDto(
                            response.body().string(),
                            t1 - t0
                        )
                    )
                } else {
                    responseListener.processResult(
                        ResponseDto(
                            "Error while making request. Code: ${response.code()}",
                            t1 - t0
                        )
                    )
                }
            } catch (e: Exception) {
                responseListener.processResult(
                    ResponseDto(
                        "Error: ${e.message}",
                        -1L
                    )
                )
            }
        }
    }
}
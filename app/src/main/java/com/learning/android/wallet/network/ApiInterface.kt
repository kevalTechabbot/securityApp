package com.learning.android.wallet.network

import com.learning.android.wallet.network.NetworkConstants.ApiEndPoint.API_ENDPOINT_TEMP
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    // Temp API Endpoint
    @GET(API_ENDPOINT_TEMP)
    suspend fun tempApi(): Response<ResponseBody>
}
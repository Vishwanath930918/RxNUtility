package com.vishwanath.retroutililty

import androidx.annotation.Keep
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url


@Keep
interface WebService {
    @GET()
    fun downloadFileFromServer(@Url url: String): retrofit2.Call<ResponseBody>
}
package com.movietest.displaymovie.api

import com.movietest.displaymovie.utils.UtilKeys.BASEURL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object MovieApi {
    private var retrofit: Retrofit? = null
    val getClient: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASEURL)
                    .build()
            }
            return retrofit
        }
}
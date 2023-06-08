package com.movietest.displaymovie.api

import com.movietest.displaymovie.models.AllMovies
import com.movietest.displaymovie.models.AllSeries
import com.movietest.displaymovie.models.Result
import com.movietest.displaymovie.models.TvResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
@GET("/3/discover/movie")
fun getTopRatedMovies(
    @Query("api_key") apiKey: String?,
    @Query("with_original_language") language: String?,
    @Query("page") pageIndex: Int,
    @Query("sort_by") sortBy: String?
): Call<AllMovies?>?

    @GET("/3/discover/movie")
    fun getTopRatedLanguageMovies(
        @Query("api_key") apiKey: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("sort_by") sortBy: String?
    ): Call<AllMovies?>?

    @GET("/3/search/movie")
    fun getSearchMovies(
        @Query("query") query: String?,
        @Query("api_key") apiKey: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("sort_by") sortBy: String?
    ): Call<AllMovies?>?

    @GET("/3/search/movie")
    fun getSearchLanguageMovies(
        @Query("query") query: String?,
        @Query("api_key") apiKey: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("sort_by") sortBy: String?
    ): Call<AllMovies?>?

    @GET("/3/movie/{id}/recommendations")
    fun getRecommendationMovies(
        @Path("id") id: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("api_key") apiKey: String?
    ): Call<AllMovies?>?

    @GET("/3/movie/{id}")
    fun getDetailsById(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<Result?>?

    @GET("/3/discover/tv")
    fun getTopRatedSerices(
        @Query("api_key") apiKey: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("sort_by") sortBy: String?
    ): Call<AllSeries?>?

    @GET("/3/search/tv")
    fun getSearchSeries(
        @Query("query") query: String?,
        @Query("api_key") apiKey: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("sort_by") sortBy: String?
    ): Call<AllSeries?>?

    @GET("/3/tv/{id}")
    fun getDetailsByIdSeries(
        @Path("id") id: Int,
        @Query("api_key") apiKey: String?
    ): Call<TvResult?>?

    @GET("/3/tv/{id}/recommendations")
    fun getRecommendationSeries(
        @Path("id") id: String?,
        @Query("with_original_language") language: String?,
        @Query("page") pageIndex: Int,
        @Query("api_key") apiKey: String?
    ): Call<AllSeries?>?
}
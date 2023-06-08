package com.movietest.displaymovie.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllMovies {

    @SerializedName("page")
    @Expose
    var page: Int? = null
    @SerializedName("results")
    @Expose
    var results: List<Result> = ArrayList<Result>()

    @SerializedName("total_results")
    @Expose
    var totalResults: Int? = null
    @SerializedName("total_pages")
    @Expose
    var totalPages: Int? = null
}
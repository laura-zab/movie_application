package com.movietest.displaymovie.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.movietest.displaymovie.R
import com.movietest.displaymovie.adapter.PaginationAdapter
import com.movietest.displaymovie.api.MovieApi
import com.movietest.displaymovie.api.MovieService
import com.movietest.displaymovie.classes.SharedPreferenceClass.read
import com.movietest.displaymovie.classes.SharedPreferenceClass.remove
import com.movietest.displaymovie.classes.SharedPreferenceClass.save
import com.movietest.displaymovie.database.DatabaseClass
import com.movietest.displaymovie.database.DatabaseModel
import com.movietest.displaymovie.databinding.ActivityDetailsBinding
import com.movietest.displaymovie.models.AllMovies
import com.movietest.displaymovie.models.Result
import com.movietest.displaymovie.utils.UtilKeys.API_KEY
import com.movietest.displaymovie.utils.UtilKeys.BASE_URL_IMG
import com.movietest.displaymovie.utils.UtilKeys.INTENTKEY_MOVIEID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class DetailsActivity : AppCompatActivity() {
    private var id = 0
    var databaseModel: DatabaseModel? = null
    var binding: ActivityDetailsBinding? = null
    var databaseClass: DatabaseClass? = null
    var image: String? = null
    var results: List<Result>? = null
    var adapter: PaginationAdapter? = null
    var linearLayoutManager: GridLayoutManager? = null
    private var movieService: MovieService? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseClass = DatabaseClass.getDatabase(this)
        results = ArrayList()
        setClicks()
        deatilsData
        movieService = MovieApi.getClient?.create(MovieService::class.java)
        setAdapter()
        loadDetailsPage()
        loadFirstPage()
    }

    private fun loadFirstPage() {
        callTopRatedMoviesApi(id.toString(), 1)!!.enqueue(object : Callback<AllMovies?> {

            override fun onResponse(call: Call<AllMovies?>, response: Response<AllMovies?>) {
                Log.e(TAG, "onMenuItemClick: $response")
                results = fetchResults(response)
                Log.e(TAG, "onMenuItemClick: " + results!!.size)
                binding!!.mainProgress.visibility = View.GONE
                if (results!!.isEmpty()) {
                    binding!!.noData.visibility = View.VISIBLE
                } else {
                    adapter!!.setMovies(results)
                }
            }

            override fun onFailure(call: Call<AllMovies?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        adapter = PaginationAdapter(this)
        linearLayoutManager = GridLayoutManager(this, 2)
        binding!!.recRV.layoutManager = linearLayoutManager
        binding!!.recRV.itemAnimator = DefaultItemAnimator()
        binding!!.recRV.adapter = adapter
    }

    private fun fetchResults(response: Response<AllMovies?>): List<Result> {
        val allMovies = response?.body()!!
        return allMovies.results
    }

    private fun setClicks() {
        binding!!.backbtn.setOnClickListener { onBackPressed() }
        binding!!.save.setOnClickListener {
            binding!!.save.visibility = View.GONE
            binding!!.saved.visibility = View.VISIBLE
            save(this@DetailsActivity, "movie", id.toString())
            save(this@DetailsActivity, id.toString(), id.toString())
            databaseModel = DatabaseModel(
                id,
                image!!,
                binding!!.yearTxt.text.toString(),
                binding!!.titleTxt.text.toString(),
                binding!!.descTxt.text.toString(),
                binding!!.releasedateTxt.text.toString(),
                binding!!.ratingsTxt.text.toString()
            )
            databaseClass?.dao()?.insertAll(databaseModel)
            Toast.makeText(this@DetailsActivity, "Added into Favorite List", Toast.LENGTH_SHORT)
                .show()
        }
        binding!!.saved.setOnClickListener {
            binding!!.saved.visibility = View.GONE
            binding!!.save.visibility = View.VISIBLE
            remove(this@DetailsActivity, id.toString())
            databaseModel = DatabaseModel(
                id,
                image!!,
                binding!!.yearTxt.text.toString(),
                binding!!.titleTxt.text.toString(),
                binding!!.descTxt.text.toString(),
                binding!!.releasedateTxt.text.toString(),
                binding!!.ratingsTxt.text.toString()
            )
            databaseClass?.dao()?.delete(databaseModel)
            Toast.makeText(this@DetailsActivity, "Removed From Favorite List", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun callTopRatedMoviesApi(id: String, page: Int): Call<AllMovies?>? {
        val language = read(this, "language", "en")
        return movieService!!.getRecommendationMovies(
            id,
            language,
            page,
            API_KEY
        )
    }

    val deatilsData: Unit
        get() {
            val extras = intent.extras
            var mId: String? = ""
            if (extras != null) {
                mId = extras.getString(INTENTKEY_MOVIEID)
            }
            id = mId!!.toInt()
        }

    private fun loadDetailsPage() {
        val call = movieService!!.getDetailsById(
            id,
            API_KEY
        )
        call!!.enqueue(object : Callback<Result?> {
            override fun onResponse(call: Call<Result?>, response: Response<Result?>) {
                Log.e(
                    TAG,
                    "onMenuItemClick: detail $response"
                )
                try {
                    image = BASE_URL_IMG + response.body()!!.posterPath
                    Glide.with(baseContext)
                        .load(BASE_URL_IMG + response.body()!!.posterPath)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(GenericTransitionOptions.with<Drawable>(android.R.anim.fade_in))
                        .error(R.drawable.noimg)
                        .into(binding!!.imgPoster)
                    binding!!.recommendationText.visibility = View.VISIBLE
                    val _id = read(this@DetailsActivity, id.toString(), "noId")
                    if (id.toString() == _id) {
                        binding!!.save.visibility = View.GONE
                        binding!!.saved.visibility = View.VISIBLE
                    } else {
                        binding!!.save.visibility = View.VISIBLE
                        binding!!.saved.visibility = View.GONE
                    }
                    Log.e(
                        TAG,
                        "onMenuItemClick: detail" + response.body()!!.releaseDate!!.substring(
                            0,
                            4
                        )
                                + " | "
                                + response.body()!!.originalLanguage!!.uppercase(Locale.getDefault())
                    )
                    binding!!.yearTxt.text =
                        (response.body()!!.releaseDate!!.substring(0, 4)
                                + " | "
                                + response.body()!!.originalLanguage!!.uppercase(Locale.getDefault()))
                    binding!!.titleTxt.text = response.body()!!.title
                    binding!!.descTxt.text = response.body()!!.overview
                    Log.e(TAG, "onMenuItemClick: detail " + response.body()!!.title)
                    Log.e(TAG, "onMenuItemClick: detail " + response.body()!!.overview)
                    Log.e(TAG, "onMenuItemClick: detail " + response.body()!!.releaseDate)
                    Log.e(TAG, "onMenuItemClick: detail " + response.body()!!.voteAverage)
                    binding!!.releasedateTxt.text =
                        "Release Date: " + response.body()!!.releaseDate
                    binding!!.ratingsTxt.text = "Vote Average: " + response.body()!!.voteAverage + ""
                    binding!!.mainProgress.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Result?>, t: Throwable) {
               t.printStackTrace()
            }
        })
    }

    companion object {
        private val TAG = MainActivity::class.java.name
    }
}
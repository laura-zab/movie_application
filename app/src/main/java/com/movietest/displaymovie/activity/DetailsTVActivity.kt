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
import com.bumptech.glide.request.target.Target
import com.movietest.displaymovie.R
import com.movietest.displaymovie.adapter.TvAdapter
import com.movietest.displaymovie.api.MovieApi
import com.movietest.displaymovie.api.MovieService
import com.movietest.displaymovie.classes.SharedPreferenceClass.read
import com.movietest.displaymovie.classes.SharedPreferenceClass.remove
import com.movietest.displaymovie.classes.SharedPreferenceClass.save
import com.movietest.displaymovie.database.DatabaseClass
import com.movietest.displaymovie.database.DatabaseModel
import com.movietest.displaymovie.databinding.ActivityDetailsTvactivityBinding
import com.movietest.displaymovie.models.AllSeries
import com.movietest.displaymovie.models.TvResult
import com.movietest.displaymovie.utils.UtilKeys.API_KEY
import com.movietest.displaymovie.utils.UtilKeys.BASE_URL_IMG
import com.movietest.displaymovie.utils.UtilKeys.INTENTKEY_MOVIEID
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class DetailsTvActivity : AppCompatActivity() {
    private var id = 0
    private var movieService: MovieService? = null
    var databaseModel: DatabaseModel? = null
    var binding: ActivityDetailsTvactivityBinding? = null
    var databaseClass: DatabaseClass? = null
    var image: String? = null
    var results: List<TvResult>? = null
    var adapter: TvAdapter? = null
    var linearLayoutManager: GridLayoutManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsTvactivityBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        databaseClass = DatabaseClass.getDatabase(this)
        setClicks()
        deatilsData
        movieService = MovieApi.getClient?.create(MovieService::class.java)
        loadDetailsPage()
        setAdapter()
        loadFirstPage()
    }

    private fun loadFirstPage() {
        callTopRatedMoviesApi(id.toString(), 1)!!.enqueue(object : Callback<AllSeries?> {
            override fun onResponse(call: Call<AllSeries?>, response: Response<AllSeries?>) {
                Log.e(TAG, "onMenuItemClick: $response")
                results = fetchResults(response)
                Log.e(TAG, "onMenuItemClick: " + results!!.size)
                binding?.mainProgress?.setVisibility(View.GONE)
                if (results!!.isEmpty()) {
                    binding?.noData?.setVisibility(View.VISIBLE)
                } else {
                    adapter!!.setMovies(results as MutableList<TvResult>?)
                }
            }

            override fun onFailure(call: Call<AllSeries?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        adapter = TvAdapter(this)
        linearLayoutManager = GridLayoutManager(this, 2)
        binding?.recRV?.layoutManager = linearLayoutManager
        binding?.recRV?.setItemAnimator(DefaultItemAnimator())
        binding?.recRV?.setAdapter(adapter)
    }

    private fun fetchResults(response: Response<AllSeries?>): List<TvResult> {
        val allMovies = response.body()!!
        return allMovies.results
    }

    private fun callTopRatedMoviesApi(id: String, page: Int): Call<AllSeries?>? {
        val language = read(this, "language", "en")
        return movieService!!.getRecommendationSeries(
            id,
            language,
            page,
            API_KEY
        )
    }

    private fun setClicks() {
        binding?.backbtn?.setOnClickListener(View.OnClickListener { onBackPressed() })
        binding?.save?.setOnClickListener(View.OnClickListener {
            binding?.save?.setVisibility(View.GONE)
            binding?.saved?.setVisibility(View.VISIBLE)
            save(this@DetailsTvActivity, id.toString(), id.toString())
            databaseModel = DatabaseModel(
                id,
                image!!,
                binding!!.yearTxt.getText().toString(),
                binding!!.titleTxt.getText().toString(),
                binding!!.descTxt.getText().toString(),
                binding!!.releasedateTxt.getText().toString(),
                binding!!.ratingsTxt.getText().toString()
            )
            databaseClass?.dao()?.insertAll(databaseModel)
            Toast.makeText(this@DetailsTvActivity, "Added into Favorite List", Toast.LENGTH_SHORT)
                .show()
        })
        binding?.saved?.setOnClickListener(View.OnClickListener {
            binding?.saved?.setVisibility(View.GONE)
            binding?.save?.setVisibility(View.VISIBLE)
            remove(this@DetailsTvActivity, id.toString())
            databaseModel = DatabaseModel(
                id,
                image!!,
                binding!!.yearTxt.getText().toString(),
                binding!!.titleTxt.getText().toString(),
                binding!!.descTxt.getText().toString(),
                binding!!.releasedateTxt.getText().toString(),
                binding!!.ratingsTxt.getText().toString()
            )
            databaseClass?.dao()?.delete(databaseModel)
            Toast.makeText(this@DetailsTvActivity, "Removed From Favorite List", Toast.LENGTH_SHORT)
                .show()
        })
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
        val call = movieService!!.getDetailsByIdSeries(
            id,
            API_KEY
        )
        call!!.enqueue(object : Callback<TvResult?> {
            override fun onResponse(call: Call<TvResult?>, response: Response<TvResult?>) {
                try {
                    image = BASE_URL_IMG + response.body()!!.posterPath
                    Glide.with(baseContext)
                        .load(BASE_URL_IMG + response.body()!!.posterPath)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .transition(GenericTransitionOptions.with<Drawable>(android.R.anim.fade_in))
                        .error(R.drawable.noimg)
                        .into(binding!!.imgPoster)
                    val _id = read(this@DetailsTvActivity, id.toString(), "noId")
                    binding!!.recommendationText.setVisibility(View.VISIBLE)
                    if (id.toString() == _id) {
                        binding!!.save.setVisibility(View.GONE)
                        binding!!.saved.setVisibility(View.VISIBLE)
                    } else {
                        binding!!.save.setVisibility(View.VISIBLE)
                        binding!!.saved.setVisibility(View.GONE)
                    }
                    binding!!.yearTxt.setText(
                        response.body()!!.releaseDate!!.substring(0, 4)
                                + " | "
                                + response.body()!!.originalLanguage!!.uppercase(Locale.getDefault())
                    )
                    binding!!.titleTxt.setText(response.body()!!.title)
                    binding!!.descTxt.setText(response.body()!!.overview)
                    binding!!.releasedateTxt.setText("Release Date: " + response.body()!!.releaseDate)
                    binding!!.ratingsTxt.setText("Vote Average: " + response.body()!!.voteAverage + "")
                    binding!!.mainProgress.setVisibility(View.GONE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<TvResult?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    companion object {
        private val TAG = MainActivity::class.java.name
    }
}
package com.movietest.displaymovie.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.movietest.displaymovie.R
import com.movietest.displaymovie.classes.SharedPreferenceClass.save
import com.movietest.displaymovie.databinding.ActivityMainBinding
import com.movietest.displaymovie.fragment.MoviesFragment
import com.movietest.displaymovie.fragment.TvFragment
import com.movietest.displaymovie.utils.UtilKeys.INTENTKEY_MOVIEID
import com.movietest.displaymovie.utils.UtilKeys.SPKEY_SORTING

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        save(this@MainActivity, "movie", "movie")
        supportFragmentManager.beginTransaction().replace(R.id.here, MoviesFragment()).commit()

        binding!!.saved.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SavedMovies::class.java
                )
            )
        }
        binding!!.scanner.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this@MainActivity)
            intentIntegrator.setPrompt("Scan a barcode or QR Code")
            intentIntegrator.initiateScan()
        }
        binding!!.movies.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.here, MoviesFragment()).commit()
            binding!!.series.setBackgroundColor(Color.parseColor("#152F4F"))
            binding!!.movies.setBackgroundColor(Color.parseColor("#0D1F30"))
        }
        binding!!.series.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.here, TvFragment()).commit()
            binding!!.movies.setBackgroundColor(Color.parseColor("#152F4F"))
            binding!!.series.setBackgroundColor(Color.parseColor("#0D1F30"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (intentResult.contents == null) {
                Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val i = Intent(this, DetailsActivity::class.java)
                i.putExtra(INTENTKEY_MOVIEID, intentResult.contents)
                startActivity(i)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onRestart() {
        save(this@MainActivity, "search", "no")
        super.onRestart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        save(this@MainActivity, "tv", "en")
        save(this@MainActivity, "language", "en")
        save(this@MainActivity, "search", "no")
        save(this@MainActivity, SPKEY_SORTING, "popularity.desc")
        super.onDestroy()
    }
}
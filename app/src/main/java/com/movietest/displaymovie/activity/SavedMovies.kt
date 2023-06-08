package com.movietest.displaymovie.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.zxing.integration.android.IntentIntegrator
import com.movietest.displaymovie.adapter.Favadapter
import com.movietest.displaymovie.database.DatabaseClass
import com.movietest.displaymovie.database.DatabaseModel
import com.movietest.displaymovie.databinding.ActivitySavedMoviesBinding
import com.movietest.displaymovie.utils.UtilKeys.INTENTKEY_MOVIEID
import java.util.Locale

class SavedMovies : AppCompatActivity() {
    var binding: ActivitySavedMoviesBinding? = null
    var database: DatabaseClass? = null
    var linearLayoutManager: GridLayoutManager? = null
    var adapter: Favadapter? = null
    var list: ArrayList<DatabaseModel?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedMoviesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        adapter = Favadapter(this)
        database = DatabaseClass.getDatabase(this)
        list = ArrayList()
        list = database!!.dao()!!.all as ArrayList<DatabaseModel?>?
        linearLayoutManager = GridLayoutManager(this, 2)
        binding!!.rv.layoutManager = linearLayoutManager
        binding!!.rv.itemAnimator = DefaultItemAnimator()
        adapter!!.setMovies(list)
        binding!!.rv.adapter = adapter
        binding!!.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.length > 0) {
                    search(s.toString())
                } else {
                    onResume()
                }
            }
        })
        binding!!.scanner.setOnClickListener {
            val intentIntegrator = IntentIntegrator(this@SavedMovies)
            intentIntegrator.setPrompt("Scan a barcode or QR Code")
            intentIntegrator.initiateScan()
        }
        binding!!.backbtn.setOnClickListener { onBackPressed() }
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

    private fun search(toString: String) {
        val newList = ArrayList<DatabaseModel?>()
        for (model in list!!) {
            if (model!!.title.lowercase(Locale.getDefault())
                    .contains(toString.lowercase(Locale.getDefault()))
            ) {
                newList.add(model)
            }
        }
        adapter?.setMovies(newList)
    }

    override fun onResume() {
        super.onResume()
        list = ArrayList()
        list = database!!.dao()!!.all as ArrayList<DatabaseModel?>?
        adapter?.setMovies(list)
        if (list!!.isEmpty()) {
            binding!!.noData.visibility = View.VISIBLE
        }
    }
}
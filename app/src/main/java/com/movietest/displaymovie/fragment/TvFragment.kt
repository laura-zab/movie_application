package com.movietest.displaymovie.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.movietest.displaymovie.R
import com.movietest.displaymovie.activity.MainActivity
import com.movietest.displaymovie.adapter.TvAdapter
import com.movietest.displaymovie.api.MovieApi
import com.movietest.displaymovie.api.MovieService
import com.movietest.displaymovie.classes.SharedPreferenceClass.read
import com.movietest.displaymovie.classes.SharedPreferenceClass.save
import com.movietest.displaymovie.databinding.FragmentTvBinding
import com.movietest.displaymovie.models.AllSeries
import com.movietest.displaymovie.models.TvResult
import com.movietest.displaymovie.utils.PaginationScrollListener
import com.movietest.displaymovie.utils.UtilKeys.API_KEY
import com.movietest.displaymovie.utils.UtilKeys.SPKEY_SORTING
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TvFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    var binding: FragmentTvBinding? = null
    var snackbar: Snackbar? = null
    var results: List<TvResult>? = null
    private val TOTAL_PAGES = 10
    private var internetConnected = true
    private var menuitemClickposition = 0
    var adapter: TvAdapter? = null
    var linearLayoutManager: GridLayoutManager? = null
    private var isLoading = false
    private var isLastPage = false
    val totalPageCount = 10
    private var currentPage = PAGE_START
    private var movieService: MovieService? = null
    private var mLastClickTime: Long = 0
    var sortingType: String? = "popularity.desc"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTvBinding.inflate(inflater, container, false)
        results = ArrayList<TvResult>()
        setClicks()

        movieService = MovieApi.getClient?.create(MovieService::class.java)
        setAdapter()
        binding!!.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (editable.length > 0) {
                    save(context, "search", editable.toString())
                    loadFirstPage()
                } else {
                    save(context, "search", "no")
                    loadFirstPage()
                }
            }
        })
        return binding!!.root
    }

    private fun setAdapter() {
        adapter = context?.let { TvAdapter(it) }
        linearLayoutManager = GridLayoutManager(context, 2)
        binding!!.mainRecycler.layoutManager = linearLayoutManager
        binding!!.mainRecycler.itemAnimator = DefaultItemAnimator()
        binding!!.mainRecycler.adapter = adapter
        binding!!.mainRecycler.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager!!) {
            override fun loadMoreItems() {
                this@TvFragment.isLoading = true
                currentPage += 1
                Handler().postDelayed({ loadNextPage() }, 1000)
            }

            override val totalPageCount: Int
                get() = TOTAL_PAGES
            override val isLastPage: Boolean
                get() = this@TvFragment.isLastPage
            override val isLoading: Boolean
                get() = this@TvFragment.isLoading
        })
    }

    private fun setClicks() {
        binding!!.sortbtn.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@OnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            showPopup(binding!!.sortbtn)
        })
    }

    fun showPopup(v: View?) {

        val popup = PopupMenu(
            requireContext(),
            v!!
        )
        val inflater = popup.menuInflater
        popup.setOnMenuItemClickListener(this)
        inflater.inflate(R.menu.menu_items, popup.menu)
        popup.show()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.action_sort_rating -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "popularity.desc"
                menuitemClickposition = 1
                save(context, SPKEY_SORTING, sortingType)
                loadFirstPage()
            }

            R.id.action_sort_date -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 2
                save(context, SPKEY_SORTING, sortingType)
                loadFirstPage()
            }

            R.id.en -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 3
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("en")
            }

            R.id.fr -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 4
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("fr")
            }

            R.id.es -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 5
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("es")
            }

            R.id.de -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 6
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("de")
            }

            R.id.ar -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 7
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ar")
            }

            R.id.pl -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 8
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("pl")
            }

            R.id.ru -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 9
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ru")
            }

            R.id.zh -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 10
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("zh")
            }

            R.id.ko -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 11
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ko")
            }

            R.id.ur -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 12
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ur")
            }

            R.id.hi -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 13
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("hi")
            }

            R.id.th -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 14
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("th")
            }

            R.id.enG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 15
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("en")
            }

            R.id.frG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 16
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("fr")
            }

            R.id.esG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 17
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("es")
            }

            R.id.deG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 18
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("de")
            }

            R.id.arG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 19
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ar")
            }

            R.id.plG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 20
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("pl")
            }

            R.id.ruG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 21
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ru")
            }

            R.id.zhG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 22
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("zh")
            }

            R.id.koG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 23
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ko")
            }

            R.id.urG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 24
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("ur")
            }

            R.id.hiG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 25
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("hi")
            }

            R.id.thG -> {
                Log.e(TAG, "onMenuItemClick: " + menuItem.itemId)
                sortingType = "release_date.desc"
                menuitemClickposition = 26
                save(context, SPKEY_SORTING, sortingType)
                loadLanguage("th")
            }

            else -> {}
        }
        return false
    }

    private fun loadLanguage(hi: String) {
        save(context, "tv", hi)
        loadFirstPage()
    }

    private fun loadFirstPage() {
        val language = read(context, "tv", "en")
        val s = read(context, "search", "no")
        if (s != "no") {
            callFromSearch(sortingType, s, language)?.enqueue(object : Callback<AllSeries?> {
                override fun onResponse(call: Call<AllSeries?>, response: Response<AllSeries?>) {
                    Log.e(TAG, "onMenuItemClick: $response")
                    results = fetchResults(response)
                    Log.e(TAG, "onMenuItemClick: " + results!!.size)
                    binding!!.mainProgress.visibility = View.GONE
                    adapter?.setMovies(results as MutableList<TvResult>?)
                    if (currentPage <= totalPageCount) adapter?.addLoadingFooter() else isLastPage =
                        true
                }

                override fun onFailure(call: Call<AllSeries?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } else {
            callTopRatedMoviesApi(sortingType, language)?.enqueue(object : Callback<AllSeries?> {
                override fun onResponse(call: Call<AllSeries?>, response: Response<AllSeries?>) {
                    // Got data. Send it to adapter
                    Log.e(TAG, "onMenuItemClick: $response")
                    results = fetchResults(response)
                    Log.e(TAG, "onMenuItemClick: " + results!!.size)
                    binding!!.mainProgress.visibility = View.GONE
                    adapter?.setMovies(results as MutableList<TvResult>?)
                    if (currentPage <= totalPageCount) adapter?.addLoadingFooter() else isLastPage =
                        true
                }

                override fun onFailure(call: Call<AllSeries?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    private fun fetchResults(response: Response<AllSeries?>): List<TvResult> {
        val allMovies: AllSeries? = response.body()

            return allMovies?.results!!

    }

    private fun loadNextPage() {
        val language = read(context, "tv", "en")
        val s = read(context, "search", "no")
        if (s != "no") {
            callFromSearch(sortingType, s, language)?.enqueue(object : Callback<AllSeries?> {
                override fun onResponse(call: Call<AllSeries?>, response: Response<AllSeries?>) {
                    adapter?.removeLoadingFooter()
                    isLoading = false
                    results = fetchResults(response)
                    adapter?.addAll(results!!)
                    if (currentPage != totalPageCount) adapter?.addLoadingFooter() else isLastPage =
                        true
                }

                override fun onFailure(call: Call<AllSeries?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } else {
            callTopRatedMoviesApi(sortingType, language)?.enqueue(object : Callback<AllSeries?> {
                override fun onResponse(call: Call<AllSeries?>, response: Response<AllSeries?>) {
                    adapter?.removeLoadingFooter()
                    isLoading = false
                    results = fetchResults(response)
                    adapter?.addAll(results!!)
                    if (currentPage != totalPageCount) adapter?.addLoadingFooter() else isLastPage =
                        true
                }

                override fun onFailure(call: Call<AllSeries?>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }

    private fun callFromSearch(
        sortingType: String?,
        query: String?,
        language: String?
    ): Call<AllSeries?>? {
        return movieService?.getSearchSeries(
            query,
            API_KEY,
            language,
            currentPage, sortingType
        )
    }

    private fun callTopRatedMoviesApi(sortingType: String?, language: String?): Call<AllSeries?>? {
        return movieService?.getTopRatedSerices(
            API_KEY,
            language,
            currentPage, sortingType
        )
    }

    override fun onResume() {
        super.onResume()
        registerInternetCheckReceiver()
        save(context, "search", "no")
        sortingType = read(context, SPKEY_SORTING, sortingType)
        loadFirstPage()
    }

    override fun onPause() {
        save(context, "search", "no")
        super.onPause()
        requireContext().unregisterReceiver(broadcastReceiver)
    }

    private fun registerInternetCheckReceiver() {
        val internetFilter = IntentFilter()
        internetFilter.addAction("android.net.wifi.STATE_CHANGE")
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        requireContext().registerReceiver(broadcastReceiver, internetFilter)
    }

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = getConnectivityStatusString(context)
            setSnackbarMessage(status, false)
        }
    }

    private fun setSnackbarMessage(status: String?, showBar: Boolean) {
        var internetStatus = ""
        internetStatus = if (status.equals(
                "Wifi enabled",
                ignoreCase = true
            ) || status.equals("Mobile data enabled", ignoreCase = true)
        ) {
            "Internet Connected"
        } else {
            "Lost Internet Connection"
        }
        snackbar = Snackbar
            .make(binding!!.coordinatorLayout, internetStatus, Snackbar.LENGTH_LONG)
            .setAction("Connect") {
                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(intent)
            }
        snackbar!!.setActionTextColor(Color.WHITE)
        val sbView = snackbar!!.view
        val textView =
            sbView.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        if (internetStatus.equals("Lost Internet Connection", ignoreCase = true)) {
            if (internetConnected) {
                snackbar!!.show()
                internetConnected = false
            }
        } else {
            if (!internetConnected) {
                internetConnected = true
                loadFirstPage()
            }
        }
    }

    companion object {
        var TYPE_WIFI = 1
        var TYPE_MOBILE = 2
        var TYPE_NOT_CONNECTED = 0
        private val TAG = MainActivity::class.java.name
        private const val REQUEST_CODE_QR_SCAN = 101
        private const val PAGE_START = 1
        fun getConnectivityStatus(context: Context): Int {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (null != activeNetwork) {
                if (activeNetwork.type == TYPE_WIFI) return TYPE_WIFI
                if (activeNetwork.type == TYPE_MOBILE) return TYPE_MOBILE
            }
            return TYPE_NOT_CONNECTED
        }

        fun getConnectivityStatusString(context: Context): String? {
            val conn = getConnectivityStatus(context)
            var status: String? = null
            if (conn == TYPE_WIFI) {
                status = "Wifi enabled"
            } else if (conn == TYPE_MOBILE) {
                status = "Mobile data enabled"
            } else if (conn == TYPE_NOT_CONNECTED) {
                status = "Not connected to Internet"
            }
            return status
        }
    }
}

package com.movietest.displaymovie.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.movietest.displaymovie.R
import com.movietest.displaymovie.activity.DetailsActivity
import com.movietest.displaymovie.database.DatabaseModel
import com.movietest.displaymovie.utils.UtilKeys.INTENTKEY_MOVIEID

class Favadapter(var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var model: DatabaseModel? = null
    var list: ArrayList<DatabaseModel?>?
    private val isLoadingAdded = false

    init {
        list = ArrayList()
    }

    val movies: List<DatabaseModel?>?
        get() = list

    fun setMovies(movieResults: ArrayList<DatabaseModel?>?) {
        list = movieResults
        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            ITEM -> viewHolder = getViewHolder(parent, inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingVH(v2)
            }
        }
        return viewHolder!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        model = list!![position]
        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MyViewHodler
                movieVH.movieId.text = "" + model!!.id
                movieVH.mMovieTitle.text = model!!.title
                movieVH.mYear.text = model!!.releaseDate
                movieVH.mMovieDesc.text = model!!.description
                Glide.with(context).load(model!!.image)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .addListener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            movieVH.mProgress.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            movieVH.mProgress.visibility = View.GONE
                            return false
                        }
                    })
                    .error(R.drawable.noimg)
                    .centerCrop()
                    .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                    .into(movieVH.mPosterImg)
            }

            LOADING -> {}
        }
    }

    private fun getViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v1 = inflater.inflate(R.layout.item_list, parent, false)
        viewHolder = MyViewHodler(v1)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

    internal inner class MyViewHodler(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mMovieTitle: TextView
        val movieId: TextView
        val mMovieDesc: TextView
        val mYear
                : TextView
        val mPosterImg: ImageView
        val mProgress: ProgressBar
        private val ll_card: CardView

        init {
            movieId = itemView.findViewById<View>(R.id.movie_id) as TextView
            mMovieTitle = itemView.findViewById<View>(R.id.movie_title) as TextView
            mMovieDesc = itemView.findViewById<View>(R.id.movie_desc) as TextView
            mYear = itemView.findViewById<View>(R.id.movie_year) as TextView
            mPosterImg = itemView.findViewById<View>(R.id.movie_poster) as ImageView
            mProgress = itemView.findViewById<View>(R.id.progressbar) as ProgressBar
            ll_card = itemView.findViewById<View>(R.id.ll_card) as CardView
            ll_card.setOnClickListener {
                val i = Intent(context, DetailsActivity::class.java)
                i.putExtra(INTENTKEY_MOVIEID, movieId.text)
                context.startActivity(i)
            }
        }
    }

    protected inner class LoadingVH(itemView: View?) : RecyclerView.ViewHolder(
        itemView!!
    )

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
    }
}
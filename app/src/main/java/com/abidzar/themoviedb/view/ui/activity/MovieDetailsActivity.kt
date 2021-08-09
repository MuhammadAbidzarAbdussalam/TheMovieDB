package com.abidzar.themoviedb.view.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.videos.ResultVideos
import com.abidzar.themoviedb.model.network.Instance
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.network.posterBaseURL
import com.abidzar.themoviedb.model.repository.MovieDetailsRepository
import com.abidzar.themoviedb.model.repository.VideosRepository
import com.abidzar.themoviedb.view.adapter.VideosAdapter
import com.abidzar.themoviedb.view.ui.activity.ReviewsActivity
import com.abidzar.themoviedb.viewmodel.MovieDetailsViewModel
import com.abidzar.themoviedb.viewmodel.VideosViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.videos_bottom_sheet.*
import java.text.NumberFormat
import java.util.*


class MovieDetailsActivity : AppCompatActivity(), VideosAdapter.OnItemClickListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var movieRepository: MovieDetailsRepository

    private lateinit var videosViewModel: VideosViewModel
    lateinit var videosRepository: VideosRepository

    lateinit var videosList: List<ResultVideos>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        supportActionBar?.hide()

        val movieId: Int = intent.getIntExtra("id", 1)

        val apiService: Service = Instance.getInstance()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
//            if (it == NetworkState.LOADING)
//                Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
        })

        videosRepository = VideosRepository(apiService)

        videosViewModel = getVideosViewModel(movieId)

        videosViewModel.movieVideos.observe(this, Observer {
            val videosAdapter = VideosAdapter(this, it.results, this)

            videosList = it.results

            val linearLayoutManager = LinearLayoutManager(this)

            rvSkillDetail.layoutManager = linearLayoutManager
            rvSkillDetail.setHasFixedSize(true)
            rvSkillDetail.adapter = videosAdapter
        })

        videosViewModel.networkState.observe(this, Observer {
//            if (it == NetworkState.LOADING)
//                Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show()
        })

        buttonBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_container)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setPeekHeight(0, true)
        bottomSheetBehavior.isHideable = false

        watchTrailer.setOnClickListener(View.OnClickListener {

            bottomSheetBehavior.setPeekHeight(getViewHight(), true)
            bottomSheetBehavior.isHideable = false

        })

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                println("newState = $newState")
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.peekHeight = 0
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

    }

    override fun onBackPressed() {
        if (bottomSheetBehavior.getPeekHeight() != 0) {
            bottomSheetBehavior.setPeekHeight(0, true)
        } else {
            super.onBackPressed()
        }
    }

    fun getViewHight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return height
    }

    private fun bindUI(movieDetails: MovieDetails) {
        txvTitle.text = movieDetails.title
        txvTagline.text = movieDetails.tagline
        txvReleaseDate.text = movieDetails.release_date
        txvRating.text = movieDetails.vote_average.toString()
        if (movieDetails.runtime != 0) {
            txvDuration.text = movieDetails.runtime.toString() + " minutes"
        } else {
            duration.visibility = View.GONE
        }
        txvOverview.text = movieDetails.overview

        val formatCurrency: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        txvBudget.text = formatCurrency.format(movieDetails.budget)
        txvRevenue.text = formatCurrency.format(movieDetails.revenue)

        val moviePosterUrl = posterBaseURL + movieDetails.poster_path
        Glide.with(this)
            .load(moviePosterUrl)
            .into(imvMovie)

        btnReviews.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("id", movieDetails.id)
            intent.putExtra("title", movieDetails.title)
            startActivity(intent)
        })
    }

    private fun getViewModel(movieId: Int): MovieDetailsViewModel {
        val viewModelFactory = MovieDetailsViewModelFactory(movieRepository, movieId)
        return ViewModelProvider(this, viewModelFactory).get(MovieDetailsViewModel::class.java)
    }

    private fun getVideosViewModel(movieId: Int): VideosViewModel {
        val viewModelFactory = MovieVideosViewModelFactory(videosRepository, movieId)
        return ViewModelProvider(this, viewModelFactory).get(VideosViewModel::class.java)
    }

    override fun onItemClick(position: Int) {
        val id = videosList.get(position).key

        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$id"))
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://www.youtube.com/watch?v=$id")
        )
        try {
            startActivity(appIntent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(webIntent)
        }
    }
}

class MovieDetailsViewModelFactory(
    private var movieRepository: MovieDetailsRepository,
    private var movieId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MovieDetailsViewModel(movieRepository, movieId) as T
    }
}

class MovieVideosViewModelFactory(
    private var videosRepository: VideosRepository,
    private var movieId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return VideosViewModel(videosRepository, movieId) as T
    }
}
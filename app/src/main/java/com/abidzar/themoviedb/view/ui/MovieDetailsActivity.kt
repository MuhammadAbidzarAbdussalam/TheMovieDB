package com.abidzar.themoviedb.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.network.Instance
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.network.posterBaseURL
import com.abidzar.themoviedb.model.repository.MovieDetailsRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.model.repository.PopularPagedListRepository
import com.abidzar.themoviedb.view.ui.home.HomeViewModelFactory
import com.abidzar.themoviedb.viewmodel.HomeViewModel
import com.abidzar.themoviedb.viewmodel.MovieDetailsViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.network_state_item.*
import java.text.NumberFormat
import java.util.*

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var movieRepository: MovieDetailsRepository

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

        buttonBack.setOnClickListener(View.OnClickListener {
            finish()
        })

    }

    private fun bindUI(it: MovieDetails) {
        txvTitle.text = it.title
        txvTagline.text = it.tagline
        txvReleaseDate.text = it.release_date
        txvRating.text = it.vote_average.toString()
        if (it.runtime != 0) {
            txvDuration.text = it.runtime.toString() + " minutes"
        } else {
            duration.visibility = View.GONE
        }
        txvOverview.text = it.overview

        val formatCurrency: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)
        txvBudget.text = formatCurrency.format(it.budget)
        txvRevenue.text = formatCurrency.format(it.revenue)

        val moviePosterUrl = posterBaseURL + it.poster_path
        Glide.with(this)
            .load(moviePosterUrl)
            .into(imvMovie)
    }

    private fun getViewModel(movieId: Int): MovieDetailsViewModel {
        val viewModelFactory = MovieDetailsViewModelFactory(movieRepository, movieId)
        return ViewModelProvider(this, viewModelFactory).get(MovieDetailsViewModel::class.java)
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
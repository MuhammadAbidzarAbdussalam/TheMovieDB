package com.abidzar.themoviedb.view.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.abidzar.themoviedb.R
import com.abidzar.themoviedb.model.network.Instance
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.repository.DiscoverPagedListRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.model.repository.ReviewsPagedListRepository
import com.abidzar.themoviedb.view.adapter.PopularMoviePagedListAdapter
import com.abidzar.themoviedb.view.adapter.ReviewsAdapter
import com.abidzar.themoviedb.view.ui.fragment.discover.DiscoverViewModelFactory
import com.abidzar.themoviedb.viewmodel.DiscoverViewModel
import com.abidzar.themoviedb.viewmodel.ReviewsViewModel
import kotlinx.android.synthetic.main.activity_reviews.*

class ReviewsActivity : AppCompatActivity() {

    private lateinit var reviewsViewModel: ReviewsViewModel

    lateinit var reviewsRepository: ReviewsPagedListRepository

    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        val movieId: Int = intent.getIntExtra("id", 1)
        val movieTitle: String? = intent.getStringExtra("title")

        setTitle(movieTitle)

        val apiService: Service = Instance.getInstance()

        reviewsRepository = ReviewsPagedListRepository(apiService)

        viewModelFactory = ReviewsViewModelFactory(reviewsRepository, movieId)
        reviewsViewModel = getViewModel()

        val reviewsAdapter = ReviewsAdapter(this)

        val linearLayoutManager = LinearLayoutManager(this)

        rvReviews.layoutManager = linearLayoutManager
        rvReviews.setHasFixedSize(true)
        rvReviews.adapter = reviewsAdapter

        reviewsViewModel.reviewsPagedList.observe(this, Observer {
            reviewsAdapter.submitList(it)
        })

        reviewsViewModel.networkState.observe(this, Observer {
            progressBarDiscover.visibility = if (reviewsViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE
            txvErrorDiscover.visibility = if (reviewsViewModel.listIsEmpty() && it == NetworkState.LOADING) View.VISIBLE else View.GONE

            if (!reviewsViewModel.listIsEmpty()) {
                reviewsAdapter.setNetworkState(it)
            }
        })

    }

    private fun getViewModel(): ReviewsViewModel {
        return  ViewModelProvider(this, viewModelFactory).get(ReviewsViewModel::class.java)
    }

}

class ReviewsViewModelFactory(private var reviewsRepository: ReviewsPagedListRepository, private var movieId: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ReviewsViewModel(reviewsRepository, movieId) as T
    }
}
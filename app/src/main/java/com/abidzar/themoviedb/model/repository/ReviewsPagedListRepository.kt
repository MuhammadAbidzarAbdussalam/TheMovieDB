package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.data.reviews.ReviewResult
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.network.postPerPage
import com.abidzar.themoviedb.model.source.*
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ReviewsPagedListRepository (private val apiService: Service)  {

    lateinit var reviewsPagedList: LiveData<PagedList<ReviewResult>>
    lateinit var reviewsDataSourceFactory: ReviewsDataSourceFactory

    fun fetchLiveReviewsPagedList (compositeDisposable: CompositeDisposable, movieId: Int) : LiveData<PagedList<ReviewResult>> {
        reviewsDataSourceFactory = ReviewsDataSourceFactory(apiService, compositeDisposable, movieId)

        val config:PagedList.Config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(postPerPage)
            .build()

        reviewsPagedList = LivePagedListBuilder(reviewsDataSourceFactory, config).build()

        return reviewsPagedList
    }

    fun getNetworkState(): LiveData<NetworkState>{
        return Transformations.switchMap<ReviewsDataSource, NetworkState>(reviewsDataSourceFactory.reviewsLiveDataSource, ReviewsDataSource::networkState)
    }

}
package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.data.reviews.ReviewResult
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.source.DiscoverDataSource
import com.abidzar.themoviedb.model.source.PopularDataSource
import com.abidzar.themoviedb.model.source.ReviewsDataSource
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ReviewsDataSourceFactory (
    private val apiService: Service,
    private val compositeDisposable: CompositeDisposable, private val movieId: Int
) : DataSource.Factory<Int, ReviewResult>() {

    val reviewsLiveDataSource = MutableLiveData<ReviewsDataSource>()
    lateinit var reviewsDataSource: ReviewsDataSource

    override fun create(): DataSource<Int, ReviewResult> {
        val reviewsDataSource = ReviewsDataSource(apiService, compositeDisposable, movieId)

        reviewsLiveDataSource.postValue(reviewsDataSource)
        return reviewsDataSource
    }

}
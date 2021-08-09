package com.abidzar.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.data.reviews.ReviewResult
import com.abidzar.themoviedb.model.repository.DiscoverPagedListRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.model.repository.ReviewsPagedListRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class ReviewsViewModel (private val reviewsRepository: ReviewsPagedListRepository, movieId: Int) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val reviewsPagedList : LiveData<PagedList<ReviewResult>> by lazy {
        reviewsRepository.fetchLiveReviewsPagedList(compositeDisposable, movieId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        reviewsRepository.getNetworkState()
    }

    fun listIsEmpty():Boolean {
        return reviewsPagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun invalidateDataSource() {
        reviewsRepository.reviewsDataSourceFactory.reviewsDataSource.invalidate()
    }

}
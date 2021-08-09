package com.abidzar.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.repository.DiscoverPagedListRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.model.repository.PopularPagedListRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class DiscoverViewModel(private val discoverRepository: DiscoverPagedListRepository, genreId: Int) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val moviePagedList : LiveData<PagedList<Movie>> by lazy {
        discoverRepository.fetchLiveMoviePagedList(compositeDisposable, genreId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        discoverRepository.getNetworkState()
    }

    fun listIsEmpty():Boolean {
        return moviePagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
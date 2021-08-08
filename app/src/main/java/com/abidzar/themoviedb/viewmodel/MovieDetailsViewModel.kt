package com.abidzar.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.repository.MovieDetailsRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MovieDetailsViewModel (private val movieDetailsRepository: MovieDetailsRepository, movieId: Int) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movieDetails : LiveData<MovieDetails> by lazy {
        movieDetailsRepository.fetchSingleMovieDetails(compositeDisposable, movieId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        movieDetailsRepository.getMovieDetailNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
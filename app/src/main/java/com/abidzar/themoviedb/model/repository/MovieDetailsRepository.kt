package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.LiveData
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.source.MovieDetailDataSource
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MovieDetailsRepository(private val apiService: Service) {

    lateinit var movieDetailDataSource: MovieDetailDataSource

    fun fetchSingleMovieDetails (compositeDisposable: CompositeDisposable, movieId: Int): LiveData<MovieDetails> {

        movieDetailDataSource = MovieDetailDataSource(apiService, compositeDisposable)
        movieDetailDataSource.fetchMovieDetails(movieId)

        return movieDetailDataSource.downloadedMovieResponse
    }

    fun getMovieDetailNetworkState(): LiveData<NetworkState> {
        return movieDetailDataSource.networkState
    }

}
package com.abidzar.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.videos.Videos
import com.abidzar.themoviedb.model.repository.MovieDetailsRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import com.abidzar.themoviedb.model.repository.VideosRepository
import io.reactivex.rxjava3.disposables.CompositeDisposable

class VideosViewModel (private val videosRepository: VideosRepository, movieId: Int) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val movieVideos : LiveData<Videos> by lazy {
        videosRepository.fetchMovieVideos(compositeDisposable, movieId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        videosRepository.getMovieVideosNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
package com.abidzar.themoviedb.model.repository

import androidx.lifecycle.LiveData
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.videos.Videos
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.source.MovieDetailDataSource
import com.abidzar.themoviedb.model.source.VideosDataSource
import io.reactivex.rxjava3.disposables.CompositeDisposable

class VideosRepository (private val apiService: Service) {

    lateinit var videosDataSource: VideosDataSource

    fun fetchMovieVideos (compositeDisposable: CompositeDisposable, movieId: Int): LiveData<Videos> {

        videosDataSource = VideosDataSource(apiService, compositeDisposable)
        videosDataSource.fetchMovieVideos(movieId)

        return videosDataSource.downloadedMovieResponse
    }

    fun getMovieVideosNetworkState(): LiveData<NetworkState> {
        return videosDataSource.networkState
    }

}
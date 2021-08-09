package com.abidzar.themoviedb.model.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abidzar.themoviedb.model.data.videos.ResultVideos
import com.abidzar.themoviedb.model.data.videos.Videos
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.repository.NetworkState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception

class VideosDataSource (
    private val apiService: Service,
    private val compositeDisposable: CompositeDisposable
) {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedMovieVideosResponse = MutableLiveData<Videos>()
    val downloadedMovieResponse: LiveData<Videos>
        get() = _downloadedMovieVideosResponse

    fun fetchMovieVideos(movieId: Int) {

        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.addAll(
                apiService.getMovieVideos(movieId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedMovieVideosResponse.postValue(it)
                            _networkState.postValue(NetworkState.LOADED)
                        }, {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.e("MovieDetailsDataSource", it.message.toString())
                        })
            )
        } catch (e: Exception) {
            Log.e("MovieDetailsDataSource", e.message.toString())
        }

    }

}
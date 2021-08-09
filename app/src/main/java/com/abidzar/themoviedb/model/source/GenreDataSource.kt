package com.abidzar.themoviedb.model.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.genre.Genre
import com.abidzar.themoviedb.model.data.genre.Genres
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.repository.NetworkState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception

class GenreDataSource (
    private val apiService: Service,
    private val compositeDisposable: CompositeDisposable
) {

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedGenresResponse = MutableLiveData<Genres>()
    val downloadedGenresResponse: LiveData<Genres>
        get() = _downloadedGenresResponse

    fun fetchDataDetails() {

        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.addAll(
                apiService.getGenre()
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedGenresResponse.postValue(it)
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
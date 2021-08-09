package com.abidzar.themoviedb.model.source

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.abidzar.themoviedb.model.data.popular.Movie
import com.abidzar.themoviedb.model.data.reviews.ReviewResult
import com.abidzar.themoviedb.model.network.Service
import com.abidzar.themoviedb.model.network.firstPage
import com.abidzar.themoviedb.model.repository.NetworkState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ReviewsDataSource (
    private val apiService: Service,
    private val compositeDisposable: CompositeDisposable, private val movieId: Int
) : PageKeyedDataSource<Int, ReviewResult>() {

    private var page = firstPage

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ReviewResult>) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getMovieReviews(movieId, params.key)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.total_pages >= params.key) {
                        callback.onResult(it.results, params.key + 1)
                        networkState.postValue(NetworkState.LOADED)
                    } else {
                        networkState.postValue(NetworkState.ENDOFLIST)
                    }
                }, {
                    networkState.postValue(NetworkState.ERROR)
                    Log.e("PopularDataSource", it.message.toString())
                })
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ReviewResult>) {

    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ReviewResult>
    ) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getMovieReviews(movieId, page)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    callback.onResult(it.results, null, page + 1)
                    networkState.postValue(NetworkState.LOADED)
                }, {
                    networkState.postValue(NetworkState.ERROR)
                    Log.e("PopularDataSource", it.message.toString())
                })
        )
    }
}
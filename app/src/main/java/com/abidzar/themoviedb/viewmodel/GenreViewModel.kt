package com.abidzar.themoviedb.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.abidzar.themoviedb.model.data.details.MovieDetails
import com.abidzar.themoviedb.model.data.genre.Genres
import com.abidzar.themoviedb.model.repository.GenresRepository
import com.abidzar.themoviedb.model.repository.MovieDetailsRepository
import com.abidzar.themoviedb.model.repository.NetworkState
import io.reactivex.rxjava3.disposables.CompositeDisposable

class GenreViewModel (private val genresRepository: GenresRepository) : ViewModel()  {

    private val compositeDisposable = CompositeDisposable()

    val genres : LiveData<Genres> by lazy {
        genresRepository.fetchGenres(compositeDisposable)
    }

    val networkState : LiveData<NetworkState> by lazy {
        genresRepository.getGenreNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}
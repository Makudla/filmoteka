package com.example.filmoteka.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.filmoteka.data.db.MovieDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel odpowiedzialny za operacje na filmach.
 */
class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val movieDao = MovieDB.Companion.getDatabase(application).movieDao()
    val allMovies = movieDao.getAllMovies()

    fun getMovieById(id: Int): Flow<Movie> {
        return movieDao.getMovieById(id)
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.insert(movie)
        }
    }

    fun updateMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.update(movie)
        }
    }

    fun deleteMovie(movie: Movie) {
        viewModelScope.launch {
            movieDao.delete(movie)
        }
    }

    fun clearAllMovies() {
        viewModelScope.launch {
            movieDao.clearAll()
        }
    }

    /**
     * Factory do tworzenia instancji ViewModel.
     */
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MovieViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
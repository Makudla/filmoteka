package com.example.filmoteka.data.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmoteka.data.db.MovieDB
import com.example.filmoteka.data.db.MovieDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel odpowiedzialny za operacje na filmach.
 */
class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val movieDao: MovieDao = MovieDB.getDatabase(application).movieDao()
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
}

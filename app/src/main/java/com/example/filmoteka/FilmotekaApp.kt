package com.example.filmoteka

import android.app.Application
import com.example.filmoteka.data.db.MovieDB

/**
 * Aplikacja Filmoteka - dostarcza bazę danych.
 */
class FilmotekaApp : Application() {
    val database: MovieDB by lazy {
        MovieDB.getDatabase(this)
    }
}

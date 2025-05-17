package com.example.filmoteka.data.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.data.model.MovieCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Klasa odpowiedzialna za inicjalizację bazy danych przykładowymi danymi.
 * Dane są wczytywane każdorazowo podczas uruchomienia aplikacji.
 */
class DatabaseInitializer(private val context: Context) : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            populateDatabase(context)
        }
    }
    
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        // Przy każdym otwarciu bazy danych, czyszczenie i dodanie nowych danych
        CoroutineScope(Dispatchers.IO).launch {
            val movieDao = MovieDB.getDatabase(context).movieDao()
            
            // Sprawdzenie czy baza danych jest pusta
            val count = movieDao.getMovieCount()
            
            // Czyszczenie bazy i dodanie nowych danych tylko jeśli jest pusta
            // lub jeśli to pierwsze uruchomienie aplikacji po instalacji
            if (count == 0) {
                populateDatabase(context)
            } else {
                // Czyszczenie i ponowne ładowanie przy każdym uruchomieniu
                movieDao.clearAll()
                populateDatabase(context)
            }
        }
    }
    
    private suspend fun populateDatabase(context: Context) {
        val movieDao = MovieDB.getDatabase(context).movieDao()
        
        // Lista przykładowych filmów
        val sampleMovies = listOf(
            Movie(
                title = "Incepcja",
                releaseDate = Calendar.getInstance().apply { 
                    set(2010, 6, 16) 
                }.timeInMillis,
                category = MovieCategory.FILM,
                watched = true,
                rating = 5,
                imageUri = "file:///android_asset/posters/inception.jpg"
            ),
            Movie(
                title = "Breaking Bad",
                releaseDate = Calendar.getInstance().apply { 
                    set(2008, 0, 20) 
                }.timeInMillis,
                category = MovieCategory.SERIAL,
                watched = true,
                rating = 5,
                imageUri = "file:///android_asset/posters/breakingbad.jpg"
            ),
            Movie(
                title = "Interstellar",
                releaseDate = Calendar.getInstance().apply { 
                    set(2014, 10, 7) 
                }.timeInMillis,
                category = MovieCategory.FILM,
                watched = false,
                imageUri = "file:///android_asset/posters/interstellar.jpg"
            ),
            Movie(
                title = "Stranger Things",
                releaseDate = Calendar.getInstance().apply { 
                    set(2016, 6, 15) 
                }.timeInMillis,
                category = MovieCategory.SERIAL,
                watched = true,
                rating = 4,
                imageUri = "file:///android_asset/posters/strangerthings.jpg"
            ),
            Movie(
                title = "Planet Earth II",
                releaseDate = Calendar.getInstance().apply { 
                    set(2016, 10, 6) 
                }.timeInMillis,
                category = MovieCategory.INNA,
                watched = true,
                rating = 5,
                imageUri = "file:///android_asset/posters/planetearthii.jpg"
            ),
            Movie(
                title = "Pulp Fiction",
                releaseDate = Calendar.getInstance().apply { 
                    set(1994, 9, 14) 
                }.timeInMillis,
                category = MovieCategory.FILM,
                watched = true,
                rating = 5,
                imageUri = "file:///android_asset/posters/pulpfiction.jpg"
            ),
            Movie(
                title = "Avatar",
                releaseDate = Calendar.getInstance().apply { 
                    set(2009, 11, 18) 
                }.timeInMillis,
                category = MovieCategory.FILM,
                watched = true,
                rating = 4,
                imageUri = "file:///android_asset/posters/avatar.jpg"
            )
        )
        
        // Zapis wszystkich filmów do bazy danych
        for (movie in sampleMovies) {
            movieDao.insert(movie)
        }
    }
}

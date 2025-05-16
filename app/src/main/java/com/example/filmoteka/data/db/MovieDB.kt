package com.example.filmoteka.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.data.model.MovieCategoryConverter

/**
 * Konfiguracja bazy danych Room.
 */
@Database(entities = [Movie::class], version = 1)
@TypeConverters(MovieCategoryConverter::class)
abstract class MovieDB : RoomDatabase() {
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile private var INSTANCE: MovieDB? = null

        fun getDatabase(context: Context): MovieDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MovieDB::class.java,
                    "movie_db"
                )
                // Dodanie callbacka inicjalizującego bazę przykładowymi danymi
                .addCallback(DatabaseInitializer(context.applicationContext))
                .build()
                
                INSTANCE = instance
                instance
            }
        }
    }
}

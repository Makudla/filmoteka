package com.example.filmoteka.data.db

import androidx.room.*
import com.example.filmoteka.data.model.Movie
import kotlinx.coroutines.flow.Flow
import androidx.room.OnConflictStrategy.Companion.REPLACE

/**
 * DAO - Data Access Object dla filmów.
 */
@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY releaseDate ASC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: Int): Flow<Movie>

    @Insert(onConflict = REPLACE)
    suspend fun insert(movie: Movie)

    @Update
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("DELETE FROM movies")
    suspend fun clearAll()
    
    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int
}

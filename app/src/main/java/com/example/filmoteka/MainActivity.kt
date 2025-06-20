package com.example.filmoteka

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.filmoteka.data.model.MovieViewModel
import com.example.filmoteka.ui.navigation.MovieNavigationHost
import com.example.filmoteka.ui.theme.FilmotekaTheme

/**
 * Główna aktywność aplikacji - obsługuje całą nawigację i logikę UI.
 */
class MainActivity : ComponentActivity() {

    private lateinit var movieViewModel: MovieViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicjalizacja ViewModelu
        movieViewModel = ViewModelProvider(this)[MovieViewModel::class.java]
        
        enableEdgeToEdge()
        setContent {
            FilmotekaTheme {
                MovieNavigationHost(movieViewModel)
            }
        }
    }
}

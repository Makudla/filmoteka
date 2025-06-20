package com.example.filmoteka.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.filmoteka.ui.addedit.AddEditMovieScreen
import com.example.filmoteka.ui.detail.MovieDetailScreen
import com.example.filmoteka.ui.list.MovieListScreen
import com.example.filmoteka.data.model.MovieViewModel

@Composable
fun MovieNavigationHost(
    movieViewModel: MovieViewModel,
    navController: NavHostController = rememberNavController()
) {
    // Stan przechowujący URI wybranego obrazu, używany w ekranach dodawania/edycji
    var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

    NavHost(
        navController = navController,
        startDestination = "movieList"
    ) {
        // Definicja ekranu listy filmów
        composable("movieList") {
            // Pobranie listy filmów z ViewModelu jako Flow
            val movies = movieViewModel.allMovies
            MovieListScreen(
                movies = movies,
                // Przejście do ekranu dodawania filmu po kliknięciu przycisku dodawania
                onAddMovie = { navController.navigate("addMovie") },
                // Obsługa kliknięcia na film: jeśli nie obejrzany, przejście do edycji, inaczej do szczegółów
                onMovieClick = { movie ->
                    if (!movie.watched) {
                        navController.navigate("editMovie/${movie.id}")
                    } else {
                        navController.navigate("movieDetail/${movie.id}")
                    }
                },
                // Obsługa długiego kliknięcia: usuwanie filmu przez ViewModel
                onMovieLongClick = { movie ->
                    movieViewModel.deleteMovie(movie)
                }
            )
        }

        // Definicja ekranu szczegółów filmu z argumentem movieId typu Int
        composable(
            route = "movieDetail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Pobranie movieId z argumentów nawigacji
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            // Pobranie Flow z pojedynczym filmem po ID
            val movieFlow = movieViewModel.getMovieById(movieId)
            // Zbiór danych filmu do stanu Compose
            val movie by movieFlow.collectAsState(initial = null)

            // Jeśli film istnieje, wyświetl ekran szczegółów
            movie?.let {
                MovieDetailScreen(
                    movie = it,
                    // Powrót do poprzedniego ekranu
                    onBack = { navController.popBackStack() }
                )
            }
        }

        // Definicja ekranu dodawania nowego filmu
        composable("addMovie") {
            AddEditMovieScreen(
                initialMovie = null, // brak filmu początkowego - to dodawanie
                pickedImageUri = pickedImageUri, // przekazanie stanu wybranego obrazu
                onPickImage = { uri -> pickedImageUri = uri }, // aktualizacja wybranego obrazu
                onSave = { movie ->
                    // Dodanie nowego filmu przez ViewModel
                    movieViewModel.addMovie(movie)
                    // Powrót do poprzedniego ekranu po zapisaniu
                    navController.popBackStack()
                },
                onCancel = { navController.popBackStack() } // anulowanie i powrót
            )
        }

        // Definicja ekranu edycji filmu z argumentem movieId
        composable(
            route = "editMovie/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            // Pobranie ID filmu z argumentów
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            // Pobranie Flow z danymi filmu do edycji
            val movieFlow = movieViewModel.getMovieById(movieId)
            // Obserwacja stanu filmu
            val movie by movieFlow.collectAsState(initial = null)

            // Jeśli film istnieje, wyświetl ekran edycji
            movie?.let {
                AddEditMovieScreen(
                    initialMovie = it, // przekazanie filmu do edycji
                    pickedImageUri = pickedImageUri, // aktualny stan wybranego obrazu
                    onPickImage = { uri -> pickedImageUri = uri }, // aktualizacja obrazu
                    onSave = { updatedMovie ->
                        // Aktualizacja filmu w ViewModelu
                        movieViewModel.updateMovie(updatedMovie)
                        // Powrót po zapisaniu zmian
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() } // anulowanie edycji
                )
            }
        }
    }
}

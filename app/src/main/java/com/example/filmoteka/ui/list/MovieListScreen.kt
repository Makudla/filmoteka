package com.example.filmoteka.ui.list

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.filmoteka.R
import com.example.filmoteka.data.model.Movie
import com.example.filmoteka.data.model.MovieCategory
import kotlinx.coroutines.flow.Flow

/**
 * Ekran listy filmów.
 * Wyświetla wszystkie filmy i umożliwia kliknięcia oraz filtrowanie.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    movies: Flow<List<Movie>>,                         // Strumień listy filmów do wyświetlenia
    onAddMovie: () -> Unit,                            // Akcja po kliknięciu przycisku dodawania filmu
    onMovieClick: (Movie) -> Unit,                     // Akcja po kliknięciu na film na liście
    onMovieLongClick: (Movie) -> Unit                  // Akcja po długim kliknięciu na film (np. usuwanie)
) {
    val movieList by movies.collectAsState(initial = emptyList())    // Obserwowanie stanu listy filmów z Flow
    val context = LocalContext.current                               // Kontekst lokalny do zasobów i zasobników

    // Stan dla filtrów
    var selectedCategory by remember { mutableStateOf<MovieCategory?>(null) } // Wybrana kategoria filmu lub brak
    var filterByWatched by remember { mutableStateOf<Boolean?>(null) }        // Filtr według statusu oglądania (obejrzany/nie)
    var showFilterDialog by remember { mutableStateOf(false) }                // Czy pokazać dialog filtrów

    // Stan dla usuwania filmu
    var showDeleteDialog by remember { mutableStateOf(false) }               // Czy pokazać dialog potwierdzenia usunięcia
    var movieToDelete by remember { mutableStateOf<Movie?>(null) }            // Film przeznaczony do usunięcia

    // Wczytanie logo z folderu assets (w pamięci, tylko raz)
    val logoBitmap = remember { loadBitmapFromAssets(context, "logo_2.jpg") }

    // Filtrowanie filmów wg wybranych filtrów (kategoria + status oglądania)
    val filteredMovies = movieList.filter { movie ->
        (selectedCategory == null || movie.category == selectedCategory) &&
                (filterByWatched == null || movie.watched == filterByWatched)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,          // Wyrównanie pionowe wiersza
                        horizontalArrangement = Arrangement.spacedBy(8.dp)      // Odstęp między elementami
                    ) {
                        // Pokazujemy logo jeśli zostało wczytane
                        logoBitmap?.let {
                            Image(
                                bitmap = it,
                                contentDescription = stringResource(R.string.app_name),
                                modifier = Modifier.size(40.dp)               // Rozmiar obrazka logo
                            )
                        }
                        Text(stringResource(R.string.app_name))                  // Nazwa aplikacji obok logo
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {                     // Przycisk dodawania nowego filmu
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_movie))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {                     // Główna kolumna z listą i filtrami
            // Pasek filtrów z przyciskiem do otwierania dialogu filtrowania
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filters),
                    style = MaterialTheme.typography.bodySmall
                )

                // Przycisk otwierający dialog wyboru filtrów
                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.filter_movies),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Wyświetlanie tekstu aktywnych filtrów
                Text(
                    text = when {
                        selectedCategory != null && filterByWatched != null ->
                            "${selectedCategory?.name}, ${if (filterByWatched == true) stringResource(R.string.filter_watched) else stringResource(R.string.filter_unwatched)}"
                        selectedCategory != null ->
                            selectedCategory?.name ?: ""
                        filterByWatched != null ->
                            if (filterByWatched == true) stringResource(R.string.filter_watched) else stringResource(R.string.filter_unwatched)
                        else -> stringResource(R.string.filter_all)
                    },
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.weight(1f))                    // Odstęp między tekstem filtrów a przyciskiem czyszczenia

                if (selectedCategory != null || filterByWatched != null) { // Przycisk czyszczenia filtrów, jeśli aktywne
                    TextButton(onClick = {
                        selectedCategory = null
                        filterByWatched = null
                    }) {
                        Text(stringResource(R.string.clear))
                    }
                }
            }

            // Tekst z liczbą wyświetlanych filmów po filtrowaniu
            Text(
                text = stringResource(R.string.items_count, filteredMovies.size),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            // Lista filmów (przewijalna)
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies) { movie ->                                // Każdy film wyświetlamy przez MovieListItem
                    MovieListItem(
                        movie = movie,
                        onClick = { onMovieClick(movie) },                      // Obsługa kliknięcia
                        onLongClick = {                                        // Obsługa długiego kliknięcia (usuwanie)
                            movieToDelete = movie
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }

        // Dialog do wyboru filtrów (pokazywany po kliknięciu przycisku filtrów)
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text(stringResource(R.string.filter_movies)) },
                text = {
                    Column {
                        Text(stringResource(R.string.filter_category), style = MaterialTheme.typography.titleMedium)

                        // Opcja "Wszystkie kategorie"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == null,
                                onClick = { selectedCategory = null }
                            )
                            Text(stringResource(R.string.filter_all))
                        }

                        // Opcje kategorii z enum MovieCategory
                        MovieCategory.values().forEach { category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category }
                                )
                                Text(category.name)
                            }
                        }

                        // Linie oddzielające sekcje dialogu
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )

                        Text(stringResource(R.string.filter_status), style = MaterialTheme.typography.titleMedium)

                        // Opcja "Wszystkie statusy"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterByWatched == null,
                                onClick = { filterByWatched = null }
                            )
                            Text(stringResource(R.string.filter_all))
                        }

                        // Opcja "Obejrzane"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterByWatched == true,
                                onClick = { filterByWatched = true }
                            )
                            Text(stringResource(R.string.filter_watched))
                        }

                        // Opcja "Nieobejrzane"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = filterByWatched == false,
                                onClick = { filterByWatched = false }
                            )
                            Text(stringResource(R.string.filter_unwatched))
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {     // Zastosuj filtry i zamknij dialog
                        Text(stringResource(R.string.filter_apply))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {                                 // Resetuj filtry i zamknij dialog
                        selectedCategory = null
                        filterByWatched = null
                        showFilterDialog = false
                    }) {
                        Text(stringResource(R.string.filter_reset))
                    }
                }
            )
        }

        // Dialog potwierdzenia usuwania filmu
        if (showDeleteDialog && movieToDelete != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    movieToDelete = null
                },
                title = { Text(stringResource(R.string.delete_movie_title)) },
                text = {
                    Text(stringResource(R.string.delete_movie_message, movieToDelete?.title ?: ""))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            movieToDelete?.let { onMovieLongClick(it) }      // Wywołanie akcji usuwania
                            showDeleteDialog = false
                            movieToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.delete))
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = {                                         // Anulowanie usuwania
                            showDeleteDialog = false
                            movieToDelete = null
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

// Funkcja pomocnicza do wczytania bitmapy z plików assets
private fun loadBitmapFromAssets(context: Context, path: String): ImageBitmap? {
    return try {
        val inputStream = context.assets.open(path)               // Otwórz plik z folderu assets
        android.graphics.BitmapFactory.decodeStream(inputStream)?.asImageBitmap() // Dekoduj do bitmapy Compose
    } catch (e: Exception) {
        e.printStackTrace()
        null                                                     // Jeśli błąd, zwróć null
    }
}

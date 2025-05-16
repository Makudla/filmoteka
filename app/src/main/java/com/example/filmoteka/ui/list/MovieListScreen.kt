package com.example.filmoteka.ui.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    movies: Flow<List<Movie>>,
    onAddMovie: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    onMovieLongClick: (Movie) -> Unit
) {
    val movieList by movies.collectAsState(initial = emptyList())
    
    // Stan dla filtrów
    var selectedCategory by remember { mutableStateOf<MovieCategory?>(null) }
    var filterByWatched by remember { mutableStateOf<Boolean?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    
    // Stan dla dialogu usuwania
    var showDeleteDialog by remember { mutableStateOf(false) }
    var movieToDelete by remember { mutableStateOf<Movie?>(null) }
    
    // Filtrowanie filmów
    val filteredMovies = movieList.filter { movie ->
        (selectedCategory == null || movie.category == selectedCategory) &&
        (filterByWatched == null || movie.watched == filterByWatched)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dodajemy ikonę filmu jako logo
                        Icon(
                            imageVector = Icons.Default.LocalMovies,
                            contentDescription = "Logo Filmoteki",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("Filmoteka")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {
                Icon(Icons.Default.Add, contentDescription = "Dodaj film")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Aktywne filtry z przyciskiem filtrowania obok
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtry:",
                    style = MaterialTheme.typography.bodySmall
                )
                
                // Przycisk filtrowania przeniesiony tutaj
                IconButton(
                    onClick = { showFilterDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.KeyboardArrowDown, 
                        contentDescription = "Filtruj",
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Wyświetlanie aktywnych filtrów
                Text(
                    text = when {
                        selectedCategory != null && filterByWatched != null -> 
                            "${selectedCategory?.name}, ${if (filterByWatched == true) "Obejrzane" else "Nieobejrzane"}"
                        selectedCategory != null -> 
                            selectedCategory?.name ?: ""
                        filterByWatched != null -> 
                            if (filterByWatched == true) "Obejrzane" else "Nieobejrzane"
                        else -> "Brak"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                if (selectedCategory != null || filterByWatched != null) {
                    TextButton(onClick = { 
                        selectedCategory = null
                        filterByWatched = null
                    }) {
                        Text("Wyczyść")
                    }
                }
            }

            // Podsumowanie - liczba pozycji
            Text(
                text = "Pozycji: ${filteredMovies.size}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredMovies) { movie ->
                    MovieListItem(
                        movie = movie,
                        onClick = { onMovieClick(movie) },
                        onLongClick = { 
                            // Pokaż dialog usuwania po długim przytrzymaniu
                            movieToDelete = movie
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
        
        // Dialog filtrowania
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text("Filtruj filmy") },
                text = {
                    Column {
                        Text("Kategoria:", style = MaterialTheme.typography.titleMedium)
                        
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
                            Text("Wszystkie")
                        }
                        
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
                        
                        // Zamieniam Divider na HorizontalDivider
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Text("Status:", style = MaterialTheme.typography.titleMedium)
                        
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
                            Text("Wszystkie")
                        }
                        
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
                            Text("Obejrzane")
                        }
                        
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
                            Text("Nieobejrzane")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text("Zastosuj")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        selectedCategory = null
                        filterByWatched = null
                        showFilterDialog = false
                    }) {
                        Text("Resetuj")
                    }
                }
            )
        }
        
        // Dialog usuwania
        if (showDeleteDialog && movieToDelete != null) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteDialog = false
                    movieToDelete = null
                },
                title = { Text("Usunąć film?") },
                text = { 
                    Text("Czy na pewno chcesz usunąć film \"${movieToDelete?.title}\"?\nTej operacji nie można cofnąć.")
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            movieToDelete?.let { onMovieLongClick(it) }
                            showDeleteDialog = false
                            movieToDelete = null
                        }
                    ) {
                        Text("Usuń")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { 
                            showDeleteDialog = false
                            movieToDelete = null
                        }
                    ) {
                        Text("Anuluj")
                    }
                }
            )
        }
    }
}

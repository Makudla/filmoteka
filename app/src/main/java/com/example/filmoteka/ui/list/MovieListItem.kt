package com.example.filmoteka.ui.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.filmoteka.R
import com.example.filmoteka.data.model.Movie
import java.text.SimpleDateFormat
import java.util.*

/**
 * Komponent wyświetlający pojedynczy film w liście.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieListItem(
    movie: Movie,                     // Obiekt filmu do wyświetlenia
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()            // Karta zajmuje całą szerokość dostępnego miejsca
            .padding(8.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(12.dp), // Zaokrąglone rogi karty
        colors = CardDefaults.cardColors(  // Ustawienie kolorów karty
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Cień karty
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)         // Padding wewnętrzny w wierszu
                .fillMaxWidth()        // Wiersz zajmuje całą szerokość karty
        ) {
            // Jeśli film ma obraz plakatu, wyświetl go
            movie.imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),            // Ładowanie obrazu z podanego URI
                    contentDescription = stringResource(R.string.movie_poster, movie.title), // Opis obrazka dla dostępności
                    modifier = Modifier
                        .size(64.dp)                                      // Rozmiar obrazka: 64x64 dp
                        .clip(RoundedCornerShape(8.dp)),                // Zaokrąglone rogi obrazka
                    contentScale = ContentScale.Crop                      // Obrazek przycinany do wypełnienia rozmiaru
                )
            }

            Spacer(modifier = Modifier.width(8.dp))                     // Odstęp poziomy między obrazkiem a tekstem

            // Kolumna z tekstowymi informacjami o filmie
            Column(
                modifier = Modifier.weight(1f)                           // Kolumna zajmuje pozostałą szerokość wiersza
            ) {
                Text(
                    text = movie.title,                                  // Tytuł filmu
                    style = MaterialTheme.typography.titleMedium,       // Styl tekstu
                    color = MaterialTheme.colorScheme.primary,          // Kolor tekstu (główny kolor)
                    maxLines = 1,                                        // Maksymalnie jedna linia tekstu
                    overflow = TextOverflow.Ellipsis                     // Jeśli tekst za długi, wyświetl "..."
                )

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formatowanie daty premiery
                Text(
                    text = stringResource(R.string.release_date, dateFormat.format(Date(movie.releaseDate))), // Tekst z datą premiery
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant   // Kolor tekstu z wariantem koloru powierzchni
                )

                Text(
                    text = stringResource(R.string.category, movie.category.toString()), // Tekst z kategorią filmu
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Jeśli film ma ocenę, wyświetl ją
                movie.rating?.let { ratingValue ->
                    Text(
                        text = stringResource(R.string.rating_value, ratingValue),   // Tekst z wartością oceny
                        style = MaterialTheme.typography.bodySmall,
                        color = if (ratingValue >= 4)                                 // Jeśli ocena >= 4, kolor sekundarny, inaczej wariant na powierzchni
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } ?: run {
                    // Jeśli brak oceny, wyświetl status oglądania filmu
                    val statusText = if (movie.watched)
                        stringResource(R.string.status_watched)                      // Status "obejrzany"
                    else
                        stringResource(R.string.status_unwatched)                    // Status "nieobejrzany"

                    Text(
                        text = stringResource(R.string.status, statusText),         // Tekst statusu oglądania
                        style = MaterialTheme.typography.bodySmall,
                        color = if (movie.watched)                                  // Kolor tekstu w zależności od statusu
                            MaterialTheme.colorScheme.secondary
                        else
                            MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

package com.example.filmoteka.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.filmoteka.R
import com.example.filmoteka.data.model.Movie
import java.text.SimpleDateFormat
import java.util.*

/**
 * Ekran pokazujący szczegóły wybranego filmu.
 * Brak możliwości edycji, tylko podgląd.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movie: Movie,             // Obiekt filmu przekazany do ekranu
    onBack: () -> Unit        // Callback do obsługi powrotu
) {
    Scaffold(                // Komponent układu z top barem
        topBar = {
            TopAppBar(       // Pasek u góry ekranu
                title = {
                    Text(stringResource(R.string.movie_details)) // Tytuł paska: "Szczegóły filmu"
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {               // Przycisk powrotu
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, // Ikona strzałki powrotu
                            contentDescription = stringResource(R.string.back) // Opis dla dostępności
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(      // Kolory paska
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->            // Główna zawartość ekranu z uwzględnieniem paddingu
        Column(
            modifier = Modifier
                .padding(padding)                      // Padding od Scaffolda
                .verticalScroll(rememberScrollState()) // Umożliwia przewijanie
                .fillMaxWidth(),                       // Wypełnia całą szerokość
            horizontalAlignment = Alignment.CenterHorizontally // Wyśrodkowanie w poziomie
        ) {
            // Sekcja z plakatem filmu
            Box(
                modifier = Modifier
                    .padding(top = 16.dp),             // Górny margines
                contentAlignment = Alignment.Center    // Wyśrodkowanie zawartości w Boxie
            ) {
                Box(                                   // Tło plakatu (np. jeśli brak obrazu)
                    modifier = Modifier
                        .height(250.dp)                // Wysokość plakatu
                        .aspectRatio(2f / 3f)          // Typowe proporcje plakatu (2:3)
                        .clip(RoundedCornerShape(12.dp)) // Zaokrąglone rogi
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)) // Kolor tła
                )

                movie.imageUri?.let { uri ->           // Jeśli istnieje URI do obrazka
                    Image(
                        painter = rememberAsyncImagePainter(uri), // Ładowanie obrazu asynchronicznie
                        contentDescription = stringResource(R.string.movie_poster), // Opis dla dostępności
                        modifier = Modifier
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp)), // Zaokrąglone rogi
                        contentScale = ContentScale.FillHeight // Dopasowanie obrazu do wysokości
                    )
                }
            }

            // Tytuł filmu
            Text(
                text = movie.title,                     // Tytuł z obiektu Movie
                style = MaterialTheme.typography.headlineSmall, // Styl typografii
                color = MaterialTheme.colorScheme.primary,       // Kolor tekstu
                fontWeight = FontWeight.Bold,                   // Pogrubienie
                modifier = Modifier.padding(vertical = 16.dp)   // Margines pionowy
            )

            // Karta z informacjami o filmie
            Card(
                modifier = Modifier
                    .fillMaxWidth()                     // Wypełnia całą szerokość
                    .padding(16.dp),                    // Margines zewnętrzny
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Cień karty
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface // Kolor tła karty
                ),
                shape = RoundedCornerShape(12.dp)       // Zaokrąglone rogi
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)                 // Margines wewnętrzny
                        .fillMaxWidth()                 // Wypełnia całą szerokość
                ) {
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) // Format daty

                    Text(
                        text = stringResource(R.string.release_date, dateFormat.format(Date(movie.releaseDate))), // Data premiery
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Odstęp

                    Text(
                        text = stringResource(R.string.category, movie.category.name), // Kategoria filmu
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp)) // Odstęp

                    Text(
                        text = stringResource(
                            R.string.status,                         // Status: obejrzany / nieobejrzany
                            stringResource(
                                if (movie.watched) R.string.status_watched else R.string.status_unwatched
                            )
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (movie.watched)
                            MaterialTheme.colorScheme.secondary     // Kolor jeśli obejrzany
                        else
                            MaterialTheme.colorScheme.tertiary      // Kolor jeśli nieobejrzany
                    )

                    movie.rating?.let {                            // Jeśli jest ocena filmu
                        Spacer(modifier = Modifier.height(16.dp)) // Odstęp
                        HorizontalDivider()                       // Linia oddzielająca
                        Spacer(modifier = Modifier.height(16.dp)) // Odstęp

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.rating), // Etykieta: "Ocena"
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = stringResource(R.string.rating_value, it), // Wartość oceny
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold                   // Pogrubienie oceny
                            )
                        }
                    }
                }
            }
        }
    }
}

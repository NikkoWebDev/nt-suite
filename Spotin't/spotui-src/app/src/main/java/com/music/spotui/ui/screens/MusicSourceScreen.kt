package com.music.spotui.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.music.spotui.data.preferences.MusicSource
import com.music.spotui.data.preferences.isYoutubeLoggedIn
import com.music.spotui.data.preferences.setPrimaryMusicSource
import com.music.spotui.ui.navigation.Routes
import com.music.spotui.ui.theme.BrandYoutubeRed
import com.music.spotui.ui.theme.BrandDeezerPurple

/** First-run choice made after Spotify supplies the catalog and library. */
@Composable
fun MusicSourceScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Choose your music source", color = MaterialTheme.colorScheme.onBackground, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(
                "Spotify keeps your library and recommendations. Choose where Spotui streams the audio.",
                color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(32.dp))

            SourceCard(
                title = "YouTube Music",
                subtitle = "Best coverage. Sign in for explicit and age-restricted songs.",
                color = BrandYoutubeRed,
            ) {
                if (isYoutubeLoggedIn(context)) {
                    setPrimaryMusicSource(context, MusicSource.YOUTUBE_MUSIC)
                    navController.navigate("${Routes.SpotiflacVerify.route}?next=home")
                } else {
                    navController.navigate("${Routes.YouTubeLogin.route}?next=spotiflac")
                }
            }
            Spacer(Modifier.height(14.dp))
            SourceCard(
                title = "Deezer",
                subtitle = "Fast exact-track matching. Quality depends on your Deezer plan.",
                color = BrandDeezerPurple,
            ) {
                navController.navigate("${Routes.DeezerLogin.route}?next=spotiflac")
            }
        }
    }
}

@Composable
private fun SourceCard(title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.background(color, RoundedCornerShape(10.dp)).padding(horizontal = 12.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center,
        ) { Text("♫", color = MaterialTheme.colorScheme.onBackground, fontSize = 22.sp, fontWeight = FontWeight.Bold) }
        Column(Modifier.weight(1f).padding(start = 14.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp)
        }
        Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = color)
    }
}

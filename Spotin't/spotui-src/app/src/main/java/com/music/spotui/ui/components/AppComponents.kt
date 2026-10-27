package com.music.spotui.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.music.spotui.R
import com.music.spotui.di.SongPlayer
import com.music.spotui.ui.navigation.Routes
import com.music.spotui.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun Loader() {
    Column(
        Modifier.background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(45.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun Snackbar(showMessage: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            fontWeight = FontWeight.W500,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            text = showMessage
        )
    }
}

/**
 * Asks before downloading a whole list.
 */
@Composable
fun ConfirmDownloadAllDialog(count: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text(
            "Download $count tracks?",
            color = MaterialTheme.colorScheme.onSurface,
        ) },
        text = {
            androidx.compose.material3.Text(
                "They are saved for offline playback. You can cancel from Downloads.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = { onDismiss(); onConfirm() }) {
                androidx.compose.material3.Text("Download", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                androidx.compose.material3.Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
    )
}

/**
 * Spotin't MiniPlayer: floating grade-G3 glass capsule (24dp background blur via
 * the shared app HazeState, tinted scrim fallback below API 31).
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MiniPlayer(navController: NavHostController) {
    val miniPlayerViewModel: PlayerViewModel = hiltViewModel()
    val context = LocalContext.current
    val songTitle = miniPlayerViewModel.currentSongTitle.value
    val songSinger = miniPlayerViewModel.currentSongSinger.value
    val songCoverUri = miniPlayerViewModel.currentSongCoverUri.value
    val songPlayingState = miniPlayerViewModel.currentSongPlayingState.value
    val songId = miniPlayerViewModel.currentSongId.value
    val songIndex = miniPlayerViewModel.currentSongIndex.value
    val songAlbum = miniPlayerViewModel.currentSongAlbum.value

    // SongPlayer exposes the engine as plain @Volatile vars (not Compose state),
    // so poll coarsely — same pattern as the 300ms tick on the Player screen.
    var source by remember { mutableStateOf(SongPlayer.currentSource) }
    var quality by remember { mutableStateOf(SongPlayer.currentQuality) }
    LaunchedEffect(Unit) {
        while (true) {
            source = SongPlayer.currentSource
            quality = SongPlayer.currentQuality
            delay(500)
        }
    }

    // Same lossless criterion as the Player screen source chip, minus the
    // "Spotify 320k counts green" exception: this badge literally says FLAC, so
    // it only shows when a lossless/PCM stream is actually playing.
    val lossless = source.startsWith("Lossless") ||
        listOf("FLAC", "WAV", "ALAC", "AIFF", "APE").any { quality.uppercase().contains(it) }

    GlassCard(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        grade = GlassGrade.G3,
        cornerRadius = 24.dp,
        onClick = { navController.navigate(Routes.Player.route) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Track Info: Thumbnail + Titles
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                GlideImage(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    model = songCoverUri.ifBlank { null },
                    contentScale = ContentScale.Crop,
                    failure = placeholder(R.drawable.placeholder),
                    loading = placeholder(R.drawable.placeholder),
                    contentDescription = "Cover"
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = songTitle.ifBlank { "Nothing playing" },
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = songSinger,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (lossless) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FLAC",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Right Controls: play/pause and skip-next round glass buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Play / Pause Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (songPlayingState) {
                                SongPlayer.pause()
                                miniPlayerViewModel.updateSongState(
                                    songCoverUri, songTitle, songSinger, false, songId, songIndex, songAlbum
                                )
                            } else {
                                SongPlayer.play()
                                miniPlayerViewModel.updateSongState(
                                    songCoverUri, songTitle, songSinger, true, songId, songIndex, songAlbum
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (songPlayingState)
                            painterResource(id = R.drawable.ic_playing)
                        else
                            painterResource(id = R.drawable.play_svgrepo_com),
                        contentDescription = "Play/Pause",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Skip Next Circle
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            miniPlayerViewModel.playNextSongs(miniPlayerViewModel.queue.value, context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_player_skip),
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

package com.music.spotui.ui.screens

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.music.spotui.R
import com.music.spotui.data.api.Response
import com.music.spotui.data.entity.AlbumsModel
import com.music.spotui.data.entity.ArtistsModel
import com.music.spotui.data.entity.HomeFeedModel
import com.music.spotui.data.entity.HomeItem
import com.music.spotui.data.entity.HomeSection
import com.music.spotui.data.preferences.RecentItem
import com.music.spotui.data.preferences.getRecentItems
import com.music.spotui.data.preferences.loadLastPlayback
import com.music.spotui.di.SongPlayer
import com.music.spotui.ui.components.GlassCard
import com.music.spotui.ui.components.GlassGrade
import com.music.spotui.ui.components.Loader
import com.music.spotui.ui.navigation.Routes
import com.music.spotui.ui.navigation.albumRoute
import com.music.spotui.ui.navigation.artistRoute
import com.music.spotui.ui.navigation.playlistRoute
import com.music.spotui.ui.navigation.showRoute
import com.music.spotui.ui.theme.LocalMint
import com.music.spotui.ui.viewmodel.HomeViewModel
import com.music.spotui.ui.viewmodel.PlayerViewModel

/**
 * Screens a Spotify feed subtitle: raw IDs ("1ifc5y2z4oVqZ5CJSQK7BuO") and
 * "Playlist • <id>" patterns must never reach the UI. Long identifier-looking
 * tokens inside a string collapse to the human fallback; other separators are
 * kept readable.
 */
fun sanitizeSubtitle(raw: String?, fallbackName: String = "Playlist"): String {
    if (raw.isNullOrBlank()) return fallbackName
    val trimmed = raw.trim()
    // Whole-slot hash, e.g. a radio's playlist id knocked into the subtitle slot.
    if (trimmed.matches(Regex("^[a-zA-Z0-9]{15,}$"))) return fallbackName
    // Id-like token anywhere (22-char Spotify id, or 15+ alnum run) — humanize.
    for (token in trimmed.split(Regex("[•,|/·]"))) {
        val t = token.trim()
        if (Regex("^[a-zA-Z0-9]{15,}$").matches(t)) return fallbackName
        if (t.startsWith("1ifc", ignoreCase = true) && t.length >= 15) return fallbackName
    }
    return trimmed
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val home by homeViewModel.home.collectAsState()
    val albums by homeViewModel.albums.collectAsState()
    val artists by homeViewModel.artists.collectAsState()
    val tokens = LocalMint.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(tokens.glowInner, tokens.glowOuter),
                    radius = 1600f
                )
            )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            color = Color.Transparent
        ) {
            val feed = (home as? Response.Success)?.data
            val albumsList = (albums as? Response.Success)?.data.orEmpty()
            val artistsList = (artists as? Response.Success)?.data.orEmpty()

            when {
                feed != null && feed.sections.isNotEmpty() -> {
                    HomeFeedContent(navController, feed)
                }
                home is Response.Loading -> {
                    Loader()
                }
                albumsList.isNotEmpty() || artistsList.isNotEmpty() -> {
                    SumUpHomeScreen(navController = navController, albums = albumsList, artists = artistsList)
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Couldn't load music.\nCheck your connection and try again.",
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private fun onHomeItemClick(navController: NavController, item: HomeItem) {
    when (item) {
        is HomeItem.Album -> navController.navigate(albumRoute(item.name, item.artists.ifBlank { item.subtitle }))
        is HomeItem.Artist -> navController.navigate(artistRoute(item.name, item.id))
        is HomeItem.Playlist ->
            if (item.id.isNotBlank()) navController.navigate(playlistRoute(item.id, item.name))
            else navController.navigate(albumRoute(item.name))
    }
}

/**
 * Top bar: profile pill + status badge. Both sides are weight-capped and their
 * labels ellipsize so a narrow screen truncates ("Ad-Free…") instead of pushing
 * one chip off the edge ("H…" overflow bug).
 */
@Composable
fun SpotintTopBar(onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: profile pill
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .weight(1f, fill = false)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onProfileClick
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "N",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "nikko.dev",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right: status badge
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_shield_outline),
                    contentDescription = "Shield",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ad-Free Lossless",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Now-playing hero: grade G4 scrim glass — allowBlur=false because the hero
 * lives INSIDE the haze source Box (NavHost content): a hazeChild here would
 * blur itself. Progressive scrim veil + mint edge + glow shadow instead.
 * Shows the real playing/recent state or an honest empty state — no invented
 * titles, artists or Unsplash covers.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun NowPlayingHeroCard(navController: NavController) {
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val playingTitle = playerViewModel.currentSongTitle.value
    val playingSinger = playerViewModel.currentSongSinger.value
    val playingCover = playerViewModel.currentSongCoverUri.value
    val isPlaying = playerViewModel.currentSongPlayingState.value

    val lastPlayback = remember { loadLastPlayback(context)?.first }

    val hasTrack = playingTitle.isNotBlank() || playingCover.isNotBlank() ||
        (lastPlayback != null && (lastPlayback.title.isNotBlank() || lastPlayback.coverUri.isNotBlank()))

    val trackTitle = when {
        playingTitle.isNotBlank() -> playingTitle
        lastPlayback != null && lastPlayback.title.isNotBlank() -> lastPlayback.title
        else -> ""
    }

    val trackArtist = when {
        playingSinger.isNotBlank() -> playingSinger
        lastPlayback != null && lastPlayback.singer.isNotBlank() -> lastPlayback.singer
        else -> ""
    }

    val trackAlbum = when {
        playerViewModel.currentSongAlbum.value.isNotBlank() -> playerViewModel.currentSongAlbum.value
        lastPlayback != null && lastPlayback.album.isNotBlank() -> lastPlayback.album
        else -> ""
    }

    val trackCover = when {
        playingCover.isNotBlank() -> playingCover
        lastPlayback != null && lastPlayback.coverUri.isNotBlank() -> lastPlayback.coverUri
        else -> ""
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        grade = GlassGrade.G4,
        allowBlur = false, // inside the haze source box — scrim glass, never hazeChild
        cornerRadius = 24.dp,
        onClick = { navController.navigate(Routes.Player.route) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 72dp cover, real artwork or the neutral placeholder asset.
            GlideImage(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp)),
                model = trackCover.ifBlank { null },
                contentScale = ContentScale.Crop,
                failure = placeholder(R.drawable.placeholder),
                loading = placeholder(R.drawable.placeholder),
                contentDescription = "Cover"
            )

            // Center column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(
                    text = if (hasTrack) "NOW PLAYING" else "READY TO LISTEN",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = trackTitle.ifBlank { "Nothing playing yet" },
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (trackArtist.isBlank() && trackAlbum.isBlank()) {
                        "Play something to pick up where you left off"
                    } else {
                        listOf(trackArtist, trackAlbum).filter { it.isNotBlank() }.joinToString(" · ")
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 48dp round mint play/pause
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isPlaying) {
                            SongPlayer.pause()
                            playerViewModel.updateSongState(
                                trackCover, trackTitle, trackArtist, false,
                                playerViewModel.currentSongId.value,
                                playerViewModel.currentSongIndex.value,
                                trackAlbum
                            )
                        } else {
                            SongPlayer.play()
                            playerViewModel.updateSongState(
                                trackCover, trackTitle, trackArtist, true,
                                playerViewModel.currentSongId.value,
                                playerViewModel.currentSongIndex.value,
                                trackAlbum
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (isPlaying)
                        painterResource(id = R.drawable.ic_playing)
                    else
                        painterResource(id = R.drawable.play_svgrepo_com),
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * "Recently played" horizontal hero: grade G2 soft glass. The fake
 * 24b/96k tech badge and Starboy/Unsplash defaults are gone; the card is only
 * rendered when there is real data behind it.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RecentlyPlayedHeroCard(
    title: String,
    artist: String,
    coverUrl: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently played",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            grade = GlassGrade.G2,
            cornerRadius = 22.dp,
            onClick = onClick,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    GlideImage(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        model = coverUrl.ifBlank { null },
                        contentScale = ContentScale.Crop,
                        failure = placeholder(R.drawable.placeholder),
                        loading = placeholder(R.drawable.placeholder),
                        contentDescription = "Cover"
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = artist,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeFeedContent(navController: NavController, feed: HomeFeedModel) {
    val context = LocalContext.current
    val playerViewModel: PlayerViewModel = hiltViewModel()
    val sections = feed.sections
    val gridSection = sections.firstOrNull()?.takeIf { it.title.isBlank() }
    val carousels = if (gridSection != null) sections.drop(1) else sections
    // Real user history (the same store Search's recents use). The hero picks the
    // first item Home can act on — songs replay only when their stream URL was
    // cached at open time.
    val recents = remember { getRecentItems(context) }
    val recentHero = recents.firstOrNull { it.type != "song" || it.songUrl.isNotBlank() }

    LazyColumn(
        contentPadding = PaddingValues(bottom = 180.dp), // keep content clear of the floating bars
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // 1. TopBar
        item {
            SpotintTopBar(onProfileClick = { navController.navigate(Routes.Settings.route) })
        }

        // 2. Now Playing hero (G4)
        item {
            NowPlayingHeroCard(navController)
        }

        // 3. Opening shortcuts grid (the UNTITLED lead section, like web home)
        // or the first titled carousel.
        if (gridSection != null) {
            item {
                HomeShortcutGrid(navController, gridSection.items.take(8))
            }
        } else if (carousels.isNotEmpty()) {
            item {
                HomeFeedSection(navController, carousels[0])
            }
        }

        // 4. "Recently played" horizontal hero — sourced from real user history
        // (RecentItemsPref), never from the feed's own first item (that used to
        // duplicate the first carousel entry). Only renders with actionable data.
        item {
            if (recentHero != null) {
                val fallback = when (recentHero.type) {
                    "album" -> "Album"
                    "artist" -> "Artist"
                    "show" -> "Podcast"
                    else -> "Song"
                }
                RecentlyPlayedHeroCard(
                    title = recentHero.name,
                    artist = sanitizeSubtitle(recentHero.singer.ifBlank { null }, fallback),
                    coverUrl = recentHero.image,
                    onClick = {
                        openRecentItem(context, recentHero, navController, playerViewModel)
                    },
                )
            }
        }

        // Remaining carousels
        if (carousels.size > 1) {
            items(carousels.size - 1) { index ->
                HomeFeedSection(navController, carousels[index + 1])
            }
        }
    }
}

/**
 * Routes a real-history item from the Home recently-played hero. Mirrors the
 * Search recents interaction: albums/artists/shows navigate; songs replay the
 * cached stream URL and surface playback state.
 */
private fun openRecentItem(
    context: Context,
    item: RecentItem,
    navController: NavController,
    playerViewModel: PlayerViewModel,
) {
    when (item.type) {
        "artist" -> navController.navigate(artistRoute(item.name, item.key.takeIf { it != item.name }.orEmpty()))
        "album" -> navController.navigate(albumRoute(item.name, item.singer))
        "show" -> navController.navigate(showRoute(item.key, item.name))
        "song" -> {
            SongPlayer.playSong(item.songUrl, context)
            playerViewModel.updateSongState(
                item.image, item.name, item.singer, true, item.songId, 0, item.songAlbum
            )
        }
    }
}

/**
 * Spotify-style 2-column shortcuts grid for the feed's untitled lead section:
 * 48dp thumbnail rows on G2 glass cards (no blur — this composable sits inside
 * the haze source box, so the blur ladder stays opt-in by grade only).
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeShortcutGrid(navController: NavController, items: List<HomeItem>) {
    Column(modifier = Modifier.padding(8.dp, 4.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp, 4.dp),
            ) {
                rowItems.forEach { item ->
                    GlassCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        grade = GlassGrade.G2,
                        cornerRadius = 8.dp,
                        onClick = { onHomeItemClick(navController, item) },
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            GlideImage(
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop,
                                model = item.imageUrl,
                                loading = placeholder(R.drawable.placeholder),
                                failure = placeholder(R.drawable.placeholder),
                                contentDescription = "",
                            )
                            Text(
                                text = item.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                modifier = Modifier.padding(8.dp, 4.dp),
                            )
                        }
                    }
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * Section header + card carousel. The row gets edge fades so the first/last
 * cards never look clipped mid-scroll.
 */
@Composable
private fun HomeFeedSection(navController: NavController, section: HomeSection) {
    val rawTitle = section.title.ifBlank { "Pure mixes" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rawTitle,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Box {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(section.items.size) { i ->
                HomeFeedCard(section.items[i]) { onHomeItemClick(navController, section.items[i]) }
            }
        }
        // Edge fades over the carousel.
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        0f to MaterialTheme.colorScheme.background,
                        0.06f to Color.Transparent,
                        startX = 0f
                    )
                )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        0.94f to Color.Transparent,
                        1f to MaterialTheme.colorScheme.background,
                        startX = 0f
                    )
                )
        )
    }
}

/**
 * Feed card: grade G2 soft glass (fill + gradient border via GlassCard), artist
 * rows round, playlists/albums squared. Subtitles always sanitized.
 */
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeFeedCard(item: HomeItem, onClick: () -> Unit) {
    val isArtist = item is HomeItem.Artist
    val rawSub = when (item) {
        is HomeItem.Album -> item.artists.ifBlank { item.subtitle }
        is HomeItem.Playlist -> item.subtitle
        is HomeItem.Artist -> "Artist"
    }
    val subtitle = sanitizeSubtitle(rawSub, if (isArtist) "Artist" else "Playlist")

    GlassCard(
        modifier = Modifier
            .width(168.dp)
            .padding(6.dp),
        grade = GlassGrade.G2,
        cornerRadius = 24.dp,
        onClick = onClick,
    ) {
        Column(
            horizontalAlignment = if (isArtist) Alignment.CenterHorizontally else Alignment.Start,
            modifier = Modifier
                .padding(12.dp) // inner padding before the artwork
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clip(if (isArtist) CircleShape else RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                model = item.imageUrl,
                loading = placeholder(R.drawable.placeholder),
                failure = placeholder(R.drawable.placeholder),
                contentDescription = "Cover"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (isArtist) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (isArtist) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Fallback when the full feed fails but cache has albums/artists.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SumUpHomeScreen(navController: NavController, albums: List<AlbumsModel>, artists: List<ArtistsModel>) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 180.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        item {
            SpotintTopBar(onProfileClick = { navController.navigate(Routes.Settings.route) })
        }
        item {
            NowPlayingHeroCard(navController)
        }
        if (albums.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Pure mixes", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
                    items(albums.size) { i ->
                        val album = albums[i]
                        HomeFeedCard(
                            item = HomeItem.Album(name = album.name, imageUrl = album.coverUri, subtitle = album.artists, artists = album.artists),
                            onClick = { navController.navigate(albumRoute(album.name, album.artists)) }
                        )
                    }
                }
            }
        }
        item {
            val firstAlbum = albums.firstOrNull()
            if (firstAlbum != null) {
                RecentlyPlayedHeroCard(
                    title = firstAlbum.name,
                    artist = sanitizeSubtitle(firstAlbum.artists, "Album"),
                    coverUrl = firstAlbum.coverUri,
                    onClick = { navController.navigate(albumRoute(firstAlbum.name, firstAlbum.artists)) }
                )
            }
        }
        if (artists.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Popular artists", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp)) {
                    items(artists.size) { i ->
                        val artist = artists[i]
                        HomeFeedCard(
                            item = HomeItem.Artist(name = artist.name, imageUrl = artist.coverUri, id = artist.id),
                            onClick = { navController.navigate(artistRoute(artist.name, artist.id)) }
                        )
                    }
                }
            }
        }
    }
}

package com.music.spotui.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.music.spotui.data.api.Response
import com.music.spotui.data.entity.AccountModel
import com.music.spotui.data.entity.LibraryEntry
import com.music.spotui.data.preferences.cacheFollowedArtists
import com.music.spotui.data.preferences.cacheLibraryEntries
import com.music.spotui.data.preferences.getCachedFollowedArtists
import com.music.spotui.data.preferences.getCachedLibraryEntries
import com.music.spotui.data.preferences.LibrarySongsCountCache
import com.music.spotui.ui.repository.AppRepository
import com.metrolist.spotify.Spotify
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: AppRepository,
    @ApplicationContext private val context: android.content.Context,
) : ViewModel() {

    private val _entries: MutableStateFlow<Response<List<LibraryEntry>>> = MutableStateFlow(Response.Loading())
    val entries: StateFlow<Response<List<LibraryEntry>>> = _entries

    private val _account: MutableStateFlow<Response<AccountModel>> = MutableStateFlow(Response.Loading())
    val account: StateFlow<Response<AccountModel>> = _account

    private val _followedArtists: MutableStateFlow<List<com.music.spotui.data.entity.ArtistsModel>> =
        MutableStateFlow(emptyList())
    val followedArtists: StateFlow<List<com.music.spotui.data.entity.ArtistsModel>> = _followedArtists

    /** Playlist songs counts (playlistId -> count), reactive for library rows. */
    val songCounts: StateFlow<Map<String, Int>> = LibrarySongsCountCache.counts

    init {
        // Seed UI with the last-seen library immediately so it's visible offline.
        val cached = getCachedLibraryEntries(context)
        if (cached.isNotEmpty()) _entries.value = Response.Success(cached)
        val cachedArtists = getCachedFollowedArtists(context)
        if (cachedArtists.isNotEmpty()) _followedArtists.value = cachedArtists

        // Cached song counts (24h TTL) land in the same moment, before any
        // network happens — rows never wait for the worker.
        LibrarySongsCountCache.seed(context)

        load()
        loadAccount()
        loadFollowedArtists()
        startPlaylistSongCountWorker()
    }

    fun load() = viewModelScope.launch(Dispatchers.IO) {
        repository.provideLibrary().collect { response ->
            // Offline: keep the saved library instead of replacing it with a
            // spinner/error, so downloads stay reachable after a restart (#51).
            if (response !is Response.Success && _entries.value is Response.Success) return@collect
            // Nothing saved yet and no network: offer just the downloads (not cached).
            if (response is Response.Error) {
                _entries.value = Response.Success(listOf(com.music.spotui.data.api.Api.DOWNLOADED_ENTRY))
                return@collect
            }
            _entries.value = response
            if (response is Response.Success) cacheLibraryEntries(context, response.data)
        }
    }

    private fun loadFollowedArtists() = viewModelScope.launch(Dispatchers.IO) {
        val artists = repository.provideFollowedArtists()
        _followedArtists.value = artists
        if (artists.isNotEmpty()) cacheFollowedArtists(context, artists)
    }

    private fun loadAccount() = viewModelScope.launch(Dispatchers.IO) {
        repository.provideAccount().collect { _account.value = it }
    }

    private var countWorkerStarted = false

    /**
     * Playlist "N songs" worker (LibraryStatistics series, song-count part).
     * Starts only AFTER the first library snapshot is on the table (cached or
     * fetched — `entries.first { Success }` waits for it, so loading adds no
     * extra burst in front of first paint): the calls are sequential (~1
     * in-flight, cap 50 cycle-fresh) with ≥300ms between them.
     * 429s abort the cycle in place: the existing GQL backoff inside
     * spotify/Spotify.kt (retry-after ≤3s, then SpotifyException(429)) already
     * did its waiting — we must not stack our own hammering on top.
     */
    private companion object WorkerSpec {
        const val INTER_CALL_DELAY_MS = 350L
        const val MAX_PLAYLISTS_PER_CYCLE = 50
        const val RATE_LIMIT_COOLDOWN_MS = 2L * 60 * 1000
    }

    @Volatile private var rateLimitedUntil = 0L

    private fun startPlaylistSongCountWorker() {
        if (countWorkerStarted) return
        countWorkerStarted = true
        viewModelScope.launch(Dispatchers.IO) {
            val entries = entries
                .filterIsInstance<Response.Success<List<com.music.spotui.data.entity.LibraryEntry>>>()
                .first().data
            if (System.currentTimeMillis() < rateLimitedUntil) return@launch
            val playlistIds = entries.asSequence()
                .filter { it.isPlaylist }
                .filter { it.spotifyId != com.music.spotui.data.api.Api.HomeCache.LIKED_SONGS_ID && it.spotifyId != com.music.spotui.data.api.Api.HomeCache.DOWNLOADS_ID }
                .map { it.spotifyId }
                .distinct()
                .filterNot { LibrarySongsCountCache.isFresh(it) }
                .take(MAX_PLAYLISTS_PER_CYCLE)
                .toList()
            for ((index, playlistId) in playlistIds.withIndex()) {
                if (index > 0) delay(INTER_CALL_DELAY_MS)
                val result = repository.providePlaylistTrackCount(playlistId)
                val failure = result.exceptionOrNull()
                if (failure is Spotify.SpotifyException && failure.statusCode == 429) {
                    rateLimitedUntil = System.currentTimeMillis() + RATE_LIMIT_COOLDOWN_MS
                    return@launch
                }
                result.getOrNull()?.let { LibrarySongsCountCache.put(context, playlistId, it) }
            }
        }
    }
}

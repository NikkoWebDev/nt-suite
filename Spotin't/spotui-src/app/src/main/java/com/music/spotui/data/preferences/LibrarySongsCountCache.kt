package com.music.spotui.data.preferences

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

private const val PREF = "LibrarySongsCount"
private const val ENTRIES_KEY = "counts"
/**
 * One day of trust: library playlists change rarely, so a cached song count
 * stays fresh across restarts but re-fetches after 24h.
 */
private const val TTL_MS = 24L * 60 * 60 * 1000

/**
 * Playlist songs-count cache: playlistId -> {"count": N, "ts": millis} persisted
 * in a SharedPreferences blob with a 24h TTL (same file-style contract as
 * SettingsPref / RecentItemsPref). Persistent across calendar restarts.
 */
object LibrarySongsCountCache {

    /** Reactive frontend: library rows observe this so a row refreshes in place
     *  the moment its playlist's count lands (nobody waits for the worker). */
    private val _counts = MutableStateFlow<Map<String, Int>>(emptyMap())
    val counts: StateFlow<Map<String, Int>> = _counts

    private var timestamps: Map<String, Long> = emptyMap()
    private var seeded = false

    /** Seed the reactive map from disk (fresh entries only). Idempotent. */
    fun seed(context: Context) {
        if (seeded) return
        seeded = true
        val now = System.currentTimeMillis()
        val all = readAll(context)
        val alive = all.keys().asSequence().filter { key ->
            now - all.getJSONObject(key).optLong("ts", 0L) < TTL_MS
        }.toList()
        timestamps = alive.associateWith { all.getJSONObject(it).optLong("ts", 0L) }
        _counts.value = timestamps.mapValues { (id, _) -> all.getJSONObject(id).optInt("count", 0) }
    }

    /** True when the stored count is still inside the 24h TTL. */
    fun isFresh(playlistId: String): Boolean =
        timestamps[playlistId]?.let { System.currentTimeMillis() - it < TTL_MS } == true

    /** Publish reactively and persist (caller must have verified freshness). */
    fun put(context: Context, playlistId: String, count: Int) {
        val ts = System.currentTimeMillis()
        timestamps = timestamps + (playlistId to ts)
        _counts.value = _counts.value + (playlistId to count)
        runCatching {
            val json = readAll(context)
            json.put(playlistId, JSONObject().put("count", count).put("ts", ts))
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .edit().putString(ENTRIES_KEY, json.toString()).apply()
        }
    }

    private fun readAll(context: Context): JSONObject = runCatching {
        JSONObject(
            context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
                .getString(ENTRIES_KEY, "{}") ?: "{}"
        )
    }.getOrDefault(JSONObject())
}

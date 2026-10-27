package com.music.spotui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.music.spotui.ui.components.LocalHazeState
import com.music.spotui.ui.navigation.MainBottomNavigation
import com.music.spotui.ui.navigation.MyNavHost
import com.music.spotui.ui.navigation.Routes
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun App() {
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }
    val bottomBarPlayerState = rememberSaveable { (mutableStateOf(true)) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // Control TopBar and BottomBar
    when (navBackStackEntry?.destination?.route) {
        Routes.Home.route -> {
            bottomBarState.value = true
        }
        Routes.Search.route -> {
            bottomBarState.value = true
        }
        Routes.Library.route -> {
            bottomBarState.value = true
        }
        Routes.Album.route -> {
            bottomBarState.value = false
        }
    }

    // Single centralized haze state: the NavHost content Box below runs under
    // Modifier.haze; only Scaffold bottomBar surfaces (nav pill, MiniPlayer)
    // register as haze children. Anything composed inside the NavHost Box is
    // part of the blur SOURCE and must render scrim glass instead (a hazeChild
    // here would blur itself: real self-blur API 32+, scrim veil below).
    // haze 0.7.3: real blur from API 32, scrim fallback below (haze#77).
    val hazeState = remember { HazeState() }

    CompositionLocalProvider(LocalHazeState provides hazeState) {
        Scaffold(
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                MainBottomNavigation(navController = navController, bottomBarState = bottomBarState, bottomBarPlayerState)
            }
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize().haze(
                    hazeState,
                    style = HazeDefaults.style(backgroundColor = MaterialTheme.colorScheme.background),
                )
            ) {
                MyNavHost(navHostController = navController, bottomBarState = bottomBarState, bottomBarPlayerState)
            }
        }
    }
}

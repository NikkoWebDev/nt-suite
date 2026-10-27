package com.music.spotui.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.music.spotui.ui.components.GlassCard
import com.music.spotui.ui.components.GlassGrade
import com.music.spotui.ui.components.MiniPlayer
import com.music.spotui.ui.theme.LocalMint
import com.music.spotui.ui.theme.MintPrimary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}

@Composable
fun MainBottomNavigation(
    navController: NavHostController,
    bottomBarState: MutableState<Boolean>,
    bottomBarPlayerState: MutableState<Boolean>
) {
    val navItems = listOf(
        Routes.Home,
        Routes.Search,
        Routes.Library
    )
    val tokens = LocalMint.current

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.72f),
                            ),
                            startY = 0f
                        )
                    )
            ) {
                Column(modifier = Modifier.navigationBarsPadding()) {

                    AnimatedVisibility(
                        visible = bottomBarPlayerState.value,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                        content = {
                            MiniPlayer(navController)
                        }
                    )

                    // Floating pill with active sub-capsule. Grade G3: real glass with
                    // 24dp background blur through the shared HazeState.
                    GlassCard(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .height(68.dp),
                        grade = GlassGrade.G3,
                        cornerRadius = 34.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val navStack by navController.currentBackStackEntryAsState()
                            val currentRoute = navStack?.destination?.route

                            navItems.forEach { item ->
                                val isSelected = currentRoute == item.route

                                // Active capsule: dark anchors the deep-mint glass
                                // fill; light anchors on the solid deep-ink fill
                                // (#0B2317 — tokens.textPrimary) with raw mint ink
                                // (#3DFFA0): 12.6:1 over the fill, well past AA —
                                // the light mint primary (#059B5F) never reaches AA
                                // inside a light capsule.
                                val activeFill = if (tokens.isDark) tokens.deep.copy(alpha = 0.7f) else tokens.textPrimary
                                val activeInk = if (tokens.isDark) MaterialTheme.colorScheme.primary else MintPrimary

                                val onTabClick = {
                                    navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let {
                                            popUpTo(item.route)
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }

                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .semantics {
                                                role = Role.Tab
                                                this.selected = true
                                            }
                                            .clip(RoundedCornerShape(28.dp))
                                            .background(activeFill)
                                            .border(
                                                width = 1.dp,
                                                color = tokens.glassBorderMint,
                                                shape = RoundedCornerShape(28.dp)
                                            )
                                            .clickable(
                                                interactionSource = remember { NoRippleInteractionSource() },
                                                indication = null,
                                                onClick = onTabClick
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = item.icon),
                                                contentDescription = item.label,
                                                tint = activeInk,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.label,
                                                color = activeInk,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .semantics {
                                                role = Role.Tab
                                                this.selected = false
                                            }
                                            .clickable(
                                                interactionSource = remember { NoRippleInteractionSource() },
                                                indication = null,
                                                onClick = onTabClick
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(id = item.icon),
                                                contentDescription = item.label,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = item.label,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Normal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

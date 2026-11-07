package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckInRewardsConfig
import com.example.data.DailyCheckInState
import com.example.data.RadarVisibilityPerk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyCheckInScreen(
    checkInState: DailyCheckInState,
    onClaimCheckIn: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val rewards = remember { CheckInRewardsConfig.get7DayRewards() }
        var showClaimCelebration by remember { mutableStateOf(false) }
    var showAdSimulationDialog by remember { mutableStateOf(false) }

    val currentStreak = checkInState.currentStreakDays
    val canClaimToday = !checkInState.hasCheckedInToday

    if (showAdSimulationDialog) {
        var progress by remember { mutableStateOf(0f) }
        LaunchedEffect(Unit) {
            val totalTime = 3000L
            val interval = 50L
            for (i in 0..(totalTime / interval)) {
                kotlinx.coroutines.delay(interval)
                progress = i.toFloat() / (totalTime / interval).toFloat()
            }
            showAdSimulationDialog = false
            onClaimCheckIn()
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
            showClaimCelebration = true
            Toast.makeText(
                context,
                "Check-in claimed! Radar visibility boost active for 24h ✨",
                Toast.LENGTH_LONG
            ).show()
        }

        AlertDialog(
            onDismissRequest = {},
            title = { Text("Sponsored Ad", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Watching a short video to claim your daily check-in rewards...")
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {}
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Daily Check-in & Radar Perks", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Earn unique badges & temporary radar visibility boosts",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("daily_check_in_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Streak Header Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
                    border = BorderStroke(1.5.dp, Color(0xFF00FF41).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00FF41).copy(alpha = 0.15f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔥", fontSize = 32.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${currentStreak}-Day Active Streak!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = if (canClaimToday) "Check in today to claim your daily perk and keep your streak alive"
                            else "Today's check-in complete! Next reward unlocks tomorrow.",
                            fontSize = 12.5.sp,
                            color = Color.LightGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${checkInState.totalPoints}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00FF41)
                                )
                                Text("Points Earned", fontSize = 11.sp, color = Color.Gray)
                            }
                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp),
                                color = Color.DarkGray
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${checkInState.totalCheckInsCompleted}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00E5FF)
                                )
                                Text("Total Check-ins", fontSize = 11.sp, color = Color.Gray)
                            }
                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp),
                                color = Color.DarkGray
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${checkInState.activePerks.size}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD700)
                                )
                                Text("Active Perks", fontSize = 11.sp, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Button
                        Button(
                            onClick = {
                                if (canClaimToday) {
                                    showAdSimulationDialog = true
                                }
                            },
                            enabled = canClaimToday,
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF41),
                                contentColor = Color(0xFF011A05),
                                disabledContainerColor = Color(0xFF1F2937),
                                disabledContentColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("claim_daily_checkin_button")
                        ) {
                            Text(
                                text = if (canClaimToday) "CLAIM TODAY'S RADAR PERK ⚡" else "CHECKED IN TODAY ✓",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 7-Day Reward Track
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "7-Day Streak Rewards",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Day ${(currentStreak % 7).let { if (it == 0 && currentStreak > 0) 7 else it }} of 7",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(rewards) { item ->
                            val currentCycleDay = ((currentStreak - 1) % 7) + 1
                            val isCompleted = item.dayNumber < currentCycleDay || (item.dayNumber == currentCycleDay && checkInState.hasCheckedInToday)
                            val isCurrent = item.dayNumber == currentCycleDay && !checkInState.hasCheckedInToday
                            val isUpcoming = !isCompleted && !isCurrent

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isCompleted -> Color(0xFF142B1A)
                                        isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    }
                                ),
                                border = BorderStroke(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = when {
                                        isCompleted -> Color(0xFF00FF41)
                                        isCurrent -> MaterialTheme.colorScheme.primary
                                        item.isMilestone -> Color(0xFFFFD700).copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    }
                                ),
                                modifier = Modifier
                                    .width(105.dp)
                                    .testTag("reward_day_${item.dayNumber}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = when {
                                            isCompleted -> Color(0xFF00FF41)
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            else -> Color.DarkGray
                                        }
                                    ) {
                                        Text(
                                            text = "Day ${item.dayNumber}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                isCompleted -> Color.Black
                                                isCurrent -> Color.White
                                                else -> Color.LightGray
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = item.badgeEmoji ?: (item.perk?.emoji ?: "🎁"),
                                        fontSize = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "+${item.points} pts",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 11.5.sp,
                                        color = if (isCompleted) Color(0xFF00FF41) else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.perk?.name ?: (item.badgeTitle ?: "Bonus"),
                                        fontSize = 9.5.sp,
                                        maxLines = 2,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = when {
                                            isCompleted -> "CLAIMED ✓"
                                            isCurrent -> "READY"
                                            else -> "LOCKED"
                                        },
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Black,
                                        color = when {
                                            isCompleted -> Color(0xFF00FF41)
                                            isCurrent -> MaterialTheme.colorScheme.primary
                                            else -> Color.Gray
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Active Radar Visibility Perks Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📡", fontSize = 18.sp)
                        Text(
                            text = "Active Radar Visibility Perks",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Temporary boosts currently amplifying your proximity beacon & reach",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (checkInState.activePerks.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No active radar perks right now. Check in daily to unlock 24-hour visibility boosts!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    } else {
                        checkInState.activePerks.forEach { perk ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF091410)),
                                border = BorderStroke(1.dp, Color(perk.glowColorHex).copy(alpha = 0.7f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(perk.glowColorHex).copy(alpha = 0.2f),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(perk.emoji, fontSize = 22.sp)
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = perk.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = Color.White
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = Color(perk.glowColorHex).copy(alpha = 0.3f)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color(perk.glowColorHex),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = perk.description,
                                            fontSize = 11.5.sp,
                                            color = Color.LightGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "⏱️ ${perk.remainingHours} hours remaining",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(perk.glowColorHex)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Unlocked Streak Badges
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("🎖️", fontSize = 18.sp)
                        Text(
                            text = "Streak Milestone Badges",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = "Unique emblems awarded for continuous daily presence",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(checkInState.unlockedStreakBadges) { badge ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                modifier = Modifier.width(150.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(badge.emoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = badge.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = badge.description,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

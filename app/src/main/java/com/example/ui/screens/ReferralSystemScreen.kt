package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.NeighborReferralState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReferralSystemScreen(
    referralState: NeighborReferralState,
    onRedeemFriendCode: (String) -> Boolean,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var inputFriendCode by remember { mutableStateOf("") }
    var redeemMessage by remember { mutableStateOf<String?>(null) }
    var isRedeemSuccess by remember { mutableStateOf(false) }
    var showAdSimulationDialog by remember { mutableStateOf(false) }

    val milestones = remember(referralState.totalNeighborsInvited) {
        referralState.getMilestones()
    }

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
            
            val success = onRedeemFriendCode(inputFriendCode)
            if (success) {
                isRedeemSuccess = true
                redeemMessage = "+100 Bonus points and 12h Radar boost claimed! 🎉"
                Toast.makeText(context, redeemMessage, Toast.LENGTH_LONG).show()
            } else {
                isRedeemSuccess = false
                redeemMessage = "Invalid code or already redeemed."
            }
        }

        AlertDialog(
            onDismissRequest = {},
            title = { Text("Sponsored Ad", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Watching a short video to claim your referral rewards...")
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
                        Text("Invite Neighbors & Earn", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Expand your local network & unlock visibility boosts",
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
                .testTag("referral_system_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Growth Loop Banner
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131A2B)),
                    border = BorderStroke(1.5.dp, Color(0xFF3B82F6).copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF3B82F6).copy(alpha = 0.2f),
                                modifier = Modifier.size(50.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🤝", fontSize = 26.sp)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Hyperlocal Growth Loop",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA),
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Invite Neighbors, Unlock Perks",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Every neighbor you connect to Localiiiy strengthens your hyperlocal network. Earn bonus points, unlock 2x radar scan reach, and fast-track to the $1,000 creator payout threshold!",
                            fontSize = 12.5.sp,
                            color = Color.LightGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Highlights
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${referralState.totalNeighborsInvited}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF60A5FA)
                                )
                                Text("Neighbors Joined", fontSize = 11.sp, color = Color.Gray)
                            }
                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp),
                                color = Color.DarkGray
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${referralState.bonusPointsEarned}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00FF41)
                                )
                                Text("Bonus Points", fontSize = 11.sp, color = Color.Gray)
                            }
                            Divider(
                                modifier = Modifier
                                    .height(32.dp)
                                    .width(1.dp),
                                color = Color.DarkGray
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "2x",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFFFD700)
                                )
                                Text("Radar Reach", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Share Referral Code Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Your Unique Neighbor Invite Code",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = referralState.userReferralCode,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "Tap Copy to share link with nearby friends",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Localiiiy Invite Code", referralState.userReferralCode)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Invite code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(100.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            val shareText = "Hey neighbor! Join me on Localiiiy, the tracker-free neighborhood network. Use my code ${referralState.userReferralCode} to claim +100 bonus points and unlock a 2x Radar Reach boost: ${referralState.referralLink}"
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, shareText)
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, "Invite Neighbors to Localiiiy")
                                            context.startActivity(shareIntent)
                                        },
                                        shape = RoundedCornerShape(100.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("share_invite_button")
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Redeem a Neighbor's Code
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Have a Neighbor's Invite Code?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Enter an invite code to earn +100 bonus welcome points and an instant 12h Radar boost",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        if (referralState.hasRedeemedFriendCode) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF142B1A),
                                border = BorderStroke(1.dp, Color(0xFF00FF41).copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("✓", color = Color(0xFF00FF41), fontWeight = FontWeight.Black)
                                    Text(
                                        text = "Welcome bonus claimed via code from ${referralState.redeemedReferrerName ?: "Neighbor"}",
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = inputFriendCode,
                                    onValueChange = { inputFriendCode = it.uppercase() },
                                    placeholder = { Text("e.g. LOCAL-ALEX-7821", fontSize = 12.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Button(
                                    onClick = {
                                        if (inputFriendCode.isNotBlank()) {
                                            showAdSimulationDialog = true
                                        }
                                    },
                                    enabled = inputFriendCode.isNotBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("redeem_friend_code_btn")
                                ) {
                                    Text("Redeem", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            redeemMessage?.let { msg ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = msg,
                                    fontSize = 11.sp,
                                    color = if (isRedeemSuccess) Color(0xFF00FF41) else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Milestone Growth Ladder
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Growth Milestones & Rewards",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Unlock escalating radar reach perks as more neighbors join",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    milestones.forEach { milestone ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (milestone.isAchieved) Color(0xFF112918) else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (milestone.isAchieved) Color(0xFF00FF41) else MaterialTheme.colorScheme.outlineVariant
                            ),
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
                                    color = if (milestone.isAchieved) Color(0xFF00FF41).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(milestone.badgeEmoji, fontSize = 20.sp)
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = milestone.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (milestone.isAchieved) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = if (milestone.isAchieved) Color(0xFF00FF41) else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = "${milestone.inviteCount} Neighbors",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (milestone.isAchieved) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = milestone.bonusReward,
                                        fontSize = 11.5.sp,
                                        color = if (milestone.isAchieved) Color(0xFF81C784) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = if (milestone.isAchieved) "UNLOCKED ✓" else "LOCKED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (milestone.isAchieved) Color(0xFF00FF41) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // Invited Neighbors Activity Log
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Neighbors Invited (${referralState.invitedNeighbors.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    referralState.invitedNeighbors.forEach { neighbor ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AsyncImage(
                                    model = neighbor.avatarUrl,
                                    contentDescription = neighbor.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(neighbor.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(neighbor.username, fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Text(
                                        text = "${neighbor.status} • Joined ${neighbor.joinedTimeAgo}",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "+${neighbor.bonusPointsAwarded} pts",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF00FF41)
                                    )
                                    Text(
                                        text = neighbor.perkAwarded,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.primary
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

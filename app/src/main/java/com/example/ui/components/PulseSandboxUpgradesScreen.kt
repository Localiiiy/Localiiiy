package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileEntity
import com.example.data.OtherUserEntity
import com.example.ui.LocaliiiyViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseSandboxUpgradesScreen(
    userProfile: UserProfileEntity,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: LocaliiiyViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val creatorEarnings by viewModel.creatorEarnings.collectAsState()
    val isPrivateAccount by viewModel.isPrivateAccount.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // SOS Beacon States
    var isSosActive by remember { mutableStateOf(false) }
    var sosTimer by remember { mutableIntStateOf(0) }
    var sosResponders by remember { mutableStateOf(listOf<String>()) }

    // Hyperlocal Advertisement & Merchant Sponsored Radar States
    var showAddLostAlert by remember { mutableStateOf(false) }
    var lostAlertName by remember { mutableStateOf("") }
    var lostAlertReward by remember { mutableStateOf("250") }
    var lostAlertsList by remember {
        mutableStateOf(
            listOf(
                Triple("Fresh Brew Alki Coffee - Buy 1 Get 1 ☕", "Target: Alki Block (Within 1.5km)", "200 Sparks Bid"),
                Triple("Sunset Beach Yoga - Free Trial Class 🧘", "Target: Seattle Harbor (Within 2km)", "150 Sparks Bid"),
                Triple("Artisanal Bakery Discount Code 'LOCAL15' 🥐", "Target: Seattle North Corridor", "300 Sparks Bid")
            )
        )
    }

    // Geo-Fenced Residential Block Chats States
    var selectedBlockRoom by remember { mutableStateOf("Alki High-Rise Blocks (Verified Resident)") }
    var blockMessages by remember {
        mutableStateOf(
            listOf(
                Pair("Alex (Block 4)", "Is the garbage dispatch delayed today?"),
                Pair("Sarah (Flat 12B)", "Yes, scheduled for 3 PM instead!"),
                Pair("Jordan (Block 2)", "Anyone spare an extra charger for 1 hour?")
            )
        )
    }
    var currentChatInput by remember { mutableStateOf("") }

    // Live Voice-Pulse Townhall States
    var isMicrophoneMuted by remember { mutableStateOf(true) }
    var isListeningTownhall by remember { mutableStateOf(false) }
    var activeTownhallSpeakers by remember { mutableStateOf(14) }
    var activeTownhallListeners by remember { mutableStateOf(342) }

    // 3D Radar HUD Sonar States
    var isSonarActive by remember { mutableStateOf(true) }
    var sonarAngle by remember { mutableStateOf(0f) }

    // Daily Check-In Streak & Spark Fuel States
    var checkInStreak by remember { mutableIntStateOf(7) }
    var checkInClaimedToday by remember { mutableStateOf(false) }

    // Neighborhood Bulk-Buy Cart Pool States
    var showJoinCartDialog by remember { mutableStateOf(false) }
    var activeCartPools by remember {
        mutableStateOf(
            listOf(
                Triple("Wholesale Basmati Rice Cart 🌾", "Min. Target: 100kg • Current: 85kg", "Ends in 4 hrs"),
                Triple("Bulk Cleaning & Toiletries Pool 🧻", "Min. Target: 20 boxes • Current: 18 boxes", "Ends in 8 hrs"),
                Triple("Rooftop Organic Strawberry Pack 🍓", "Min. Target: 15 trays • Current: 11 trays", "Ends in 2 hrs")
            )
        )
    }

    // Proximity Senior Care Coordination States
    var seniorWelfareAlerts by remember {
        mutableStateOf(
            listOf(
                Triple("Mrs. Gable (Age 84, Block 2C)", "Needs medicine delivery from local pharmacy", "Urgent"),
                Triple("Mr. Kapoor (Age 79, Flat 4D)", "Needs help moving cardboard boxes", "Medium")
            )
        )
    }

    // Paid Community Safety Pins & Radar Notices States
    var safetyPinsList by remember {
        mutableStateOf(
            listOf(
                Triple("Road Closure ⚠️", "Seattle Met Corridor - Waterlogging", "1 hr ago"),
                Triple("Power Outage ⚡", "Alki Neighborhood block 4 & 5", "Just now")
            )
        )
    }

    // Skill-Matched Local Gig States
    var selectedSkills by remember { mutableStateOf(setOf("Tutoring 📚", "Dog Walking 🐕")) }
    var matchedGigsList by remember {
        mutableStateOf(
            listOf(
                "Tutoring 📚: Grade 10 Math doubt solver. Rate: ₹450/hr • Distance: 1.2km",
                "Dog Walking 🐕: Feed and walk active Labrador. Rate: ₹250/hr • Distance: 800m"
            )
        )
    }

    // Gamified Referral Program States
    var successfulReferralsCount by remember { mutableIntStateOf(3) }
    var inputReferralCode by remember { mutableStateOf("") }

    // AI Translation States
    var translateLanguageOption by remember { mutableStateOf("Hindi 🇮🇳") }
    var originalPostText = "“Beautiful early morning street walk in our quiet district block today!”"
    var translatedPostText by remember { mutableStateOf("“आज हमारे शांत जिला ब्लॉक में सुबह-सुबह सुंदर सड़क की सैर!”") }

    // Proximity Commute Ledger States
    var activeCommutePools by remember {
        mutableStateOf(
            listOf(
                Triple("To Downtown Business Bay 🚗", "Driver: Jordan (RWA Verified) • Seats: 2 left", "Leaves at 8:30 AM"),
                Triple("To Central Metro Station 🛵", "Driver: Sarah (Flat 4B) • Seats: 1 left", "Leaves at 9:00 AM")
            )
        )
    }

    // Local Event Calendars States
    var activeCommunityEventsList by remember {
        mutableStateOf(
            listOf(
                Triple("Clean-Up Block Drive 🧹", "Meet at Central Park Gate • Sat 7:00 AM", "Verified Residents only"),
                Triple("Rooftop Barbecue Mixer 🍖", "Rooftop terrace Block 4 • Sun 6:00 PM", "BYOB • Connect & mingle")
            )
        )
    }

    // 3D Sonar Scan Engine Effect
    LaunchedEffect(isSonarActive) {
        while (isSonarActive) {
            delay(16)
            sonarAngle = (sonarAngle + 2.5f) % 360f
        }
    }

    // SOS Timer and Responder dispatcher simulator
    LaunchedEffect(isSosActive) {
        if (isSosActive) {
            sosTimer = 0
            sosResponders = emptyList()
            while (isSosActive) {
                delay(1000)
                sosTimer++
                if (sosTimer == 3) {
                    sosResponders = listOf("Alex (Block 4 - Verified First Responder)", "RWA Security Patrol Dispatch")
                } else if (sosTimer == 6) {
                    sosResponders = sosResponders + "Sarah (Flat 12B - Community Nurse)"
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070A12))
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Sandbox Header Banner Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)),
                border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🏘️", fontSize = 24.sp)
                            Column {
                                Text(
                                    "LOCAL SOCIAL, SAFETY & CIVIC TOOLS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    "Hyperlocal Coordination, Safety & Engagement Hub",
                                    fontSize = 11.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Experience cutting-edge social networking, proximity sharing, and emergency safety features integrated right inside your live Pulse Feed. Keep connected, secure, and off-grid with full developer simulation.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // --- Silent Beacon SOS Mesh ---
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.2.dp, if (isSosActive) Color.Red else Color.DarkGray)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🚨", fontSize = 18.sp)
                        Column {
                            Text("Silent Beacon SOS Network", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Broadcast immediate distress signals to neighbors within 200m", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { 
                            isSosActive = !isSosActive 
                            if (isSosActive) {
                                viewModel.triggerCustomRadarAlert("⚠️ SOS ALERT BROADCAST", "A nearby verified resident is broadcasting an SOS. First responders alerted!")
                            } else {
                                viewModel.dismissHotspotAlert()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSosActive) Color.DarkGray else Color.Red,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSosActive) "CANCEL DISTRESS BEACON 🟢" else "ACTIVATE SILENT SOS BEACON 🛑",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.5.sp
                        )
                    }

                    if (isSosActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color.Red.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Status: BROADCASTING DISTRESS...", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text("Elapsed: ${sosTimer}s", color = Color.White, fontSize = 11.sp)
                                }
                                Text("Proximity Limit: 200 meters. Bypassing algorithms.", fontSize = 10.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("First Responders Responding:", fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color.White)
                                if (sosResponders.isEmpty()) {
                                    Text("Pinging nearest verified neighbors...", fontSize = 10.sp, color = Color.LightGray)
                                } else {
                                    sosResponders.forEach { responder ->
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text("✔️", fontSize = 9.sp)
                                            Text(responder, fontSize = 10.sp, color = Color.Green)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Hyperlocal Sponsored Radar & Merchant Ads ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📢", fontSize = 18.sp)
                            Column {
                                Text("Sponsored Radar Ads & Community Billboard", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Bid Sparks to feature your local shop, events, or deals", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        IconButton(onClick = { showAddLostAlert = !showAddLostAlert }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Ad Campaign")
                        }
                    }

                    if (showAddLostAlert) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = lostAlertName,
                            onValueChange = { lostAlertName = it },
                            label = { Text("What are you promoting? (e.g. Alki Coffee Sale)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        OutlinedTextField(
                            value = lostAlertReward,
                            onValueChange = { lostAlertReward = it },
                            label = { Text("Sparks Bid Budget (e.g. 250 Sparks)", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        Button(
                            onClick = {
                                if (lostAlertName.isNotBlank()) {
                                    val cost = lostAlertReward.toIntOrNull() ?: 50
                                    val success = viewModel.deductSparks(cost)
                                    if (success) {
                                        lostAlertsList = listOf(Triple(lostAlertName, "Target: Neighborhood Block (Within 1.5km)", "$lostAlertReward Sparks Bid")) + lostAlertsList
                                        viewModel.triggerCustomRadarAlert("📢 SPONSORED LOCAL AD", "Sponsored Ad Campaign: '$lostAlertName' is now pinned across nearby user maps!")
                                        lostAlertName = ""
                                        showAddLostAlert = false
                                    } else {
                                        Toast.makeText(context, "Insufficient Sparks balance to launch ad campaign!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Text("Launch Geolocated Ad Campaign", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    lostAlertsList.forEach { alert ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF10B981).copy(alpha = 0.2f)) {
                                        Text("SPONSORED", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                    }
                                    Text(alert.first, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Text(alert.second, fontSize = 10.sp, color = Color.Gray)
                            }
                            Text(alert.third, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Geo-Fenced Residential Block Chats ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🏢", fontSize = 18.sp)
                        Column {
                            Text("Geo-Fenced Residential Block Chats", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Messaging space restricted to verified flat/block members", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.Black.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth().height(160.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.SpaceBetween) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                items(blockMessages) { msg ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(msg.first + ":", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF10B981))
                                        Text(msg.second, fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BasicTextField(
                                    value = currentChatInput,
                                    onValueChange = { currentChatInput = it },
                                    modifier = Modifier.weight(1f).background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(6.dp),
                                    textStyle = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                                    decorationBox = { innerTextField ->
                                        if (currentChatInput.isEmpty()) {
                                            Text("Send msg to verified block...", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        innerTextField()
                                    }
                                )
                                IconButton(
                                    onClick = {
                                        if (currentChatInput.isNotBlank()) {
                                            blockMessages = blockMessages + Pair("You (${userProfile.username})", currentChatInput)
                                            currentChatInput = ""
                                        }
                                    },
                                    modifier = Modifier.size(28.dp).background(Color(0xFF10B981), CircleShape)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Live Voice-Pulse Townhalls ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🎙️", fontSize = 18.sp)
                        Column {
                            Text("Voice-Pulse Municipal Townhalls", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Interactive regional voice forums gated strictly by district residency", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("🔊 Live Townhall Room:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                            Text("Seattle North-Corridor District", fontSize = 11.sp, color = Color(0xFF10B981))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("👥 Speakers: $activeTownhallSpeakers", fontSize = 10.sp, color = Color.LightGray)
                            Text("Listeners: $activeTownhallListeners", fontSize = 10.sp, color = Color.LightGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                isListeningTownhall = !isListeningTownhall
                                if (isListeningTownhall) {
                                    activeTownhallListeners++
                                    Toast.makeText(context, "Connected to voice streaming corridor... Listening.", Toast.LENGTH_SHORT).show()
                                } else {
                                    activeTownhallListeners--
                                    isMicrophoneMuted = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isListeningTownhall) Color.Red else Color(0xFF10B981)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isListeningTownhall) "Leave Townhall Room 🔴" else "Join Townhall Audio Room 🟢",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isListeningTownhall) {
                            IconButton(
                                onClick = {
                                    isMicrophoneMuted = !isMicrophoneMuted
                                    if (!isMicrophoneMuted) {
                                        activeTownhallSpeakers++
                                        activeTownhallListeners--
                                    } else {
                                        activeTownhallSpeakers--
                                        activeTownhallListeners++
                                    }
                                },
                                modifier = Modifier.size(40.dp).background(if (isMicrophoneMuted) Color.DarkGray else Color.Red, CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isMicrophoneMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mute Toggle",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Daily Check-In Streak & Spark Fuel ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🔥", fontSize = 18.sp)
                        Column {
                            Text("Daily Check-In & Spark Fuel", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Earn daily Sparks to fuel boosts and gifts inside Localiiiy", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Consecutive Check-ins: $checkInStreak Days", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Available Wallet Balance: $${String.format("%.2f", creatorEarnings.availableBalanceUSD)} USD", fontSize = 11.sp, color = Color(0xFF00E5FF))
                        }

                        Button(
                            onClick = {
                                if (!checkInClaimedToday) {
                                    checkInClaimedToday = true
                                    checkInStreak++
                                    viewModel.addSparks(50) // Adds 50 Sparks (which equates to real USD in wallet)
                                    Toast.makeText(context, "Daily Check-in Claimed! Added 50 Sparks directly into your wallet balance!", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (checkInClaimedToday) Color.DarkGray else Color(0xFF10B981)
                            ),
                            enabled = !checkInClaimedToday
                        ) {
                            Text(if (checkInClaimedToday) "Claimed Today ✔️" else "Claim Sparks +50 🔥", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Neighborhood Bulk-Buy Grocery Cart Pools ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🌾", fontSize = 18.sp)
                        Column {
                            Text("Neighborhood Bulk-Buy Grocery Pools", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Co-purchase items in bulk directly from wholesale markets", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    activeCartPools.forEach { cart ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cart.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                Text(cart.second, fontSize = 10.sp, color = Color.LightGray)
                                Text(cart.third, fontSize = 9.sp, color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    val success = viewModel.deductSparks(100) // Deducts Sparks/equivalent USD from real balance
                                    if (success) {
                                        val otherUser = OtherUserEntity(
                                            username = "Sarah_Alki",
                                            fullName = "Sarah Jenkins",
                                            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100",
                                            bio = "Daily commuter and bulk purchase coordinator",
                                            locationName = "Seattle"
                                        )
                                        viewModel.startChatWithUser(otherUser)
                                        coroutineScope.launch {
                                            delay(500)
                                            viewModel.sendChatMessage("Hi Sarah! I've joined the bulk-buy pool for '${cart.first}' and deposited 100 Sparks in escrow. Let me know when the group order is dispatched! 🌾")
                                        }
                                        Toast.makeText(context, "Joined ${cart.first}! Deposit of 100 Sparks secured and chat opened with the coordinator. 🌾", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Insufficient Spark funds! Claim daily Sparks or check-in to boost balance.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Join Pool", fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Proximity Senior Care Coordination ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("👴", fontSize = 18.sp)
                        Column {
                            Text("Proximity Senior Care Network", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Co-verify and assist older block residents with chores", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    seniorWelfareAlerts.forEach { alert ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(alert.first, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                                Text(alert.third, color = Color.Red, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(alert.second, fontSize = 10.5.sp, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = {
                                    val otherUser = OtherUserEntity(
                                        username = "RWA_Welfare",
                                        fullName = "RWA Coordinator",
                                        avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100",
                                        bio = "Resident Welfare Association Coordinator",
                                        locationName = "Seattle"
                                    )
                                    viewModel.startChatWithUser(otherUser)
                                    coroutineScope.launch {
                                        delay(500)
                                        viewModel.sendChatMessage("Hi RWA Coordinator! I saw the alert: '${alert.first} - ${alert.second}'. I am nearby and available to coordinate assistance. Let me know the details! 👴")
                                    }
                                    Toast.makeText(context, "Opened direct chat coordinate to arrange senior assistance! 👴", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().height(26.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Coordinate Assistance via Direct Message", fontSize = 9.5.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // --- Paid Community Safety Pins & Radar Notices ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("⚠️", fontSize = 18.sp)
                        Column {
                            Text("Local Radar Safety Alerts", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Real-time localized hazard warnings pinned directly onto geohashes", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    safetyPinsList.forEach { pin ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("📍", fontSize = 14.sp)
                                Column {
                                    Text(pin.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                    Text(pin.second, fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Button(
                                onClick = {
                                    viewModel.triggerCustomRadarAlert(pin.first, pin.second)
                                    Toast.makeText(context, "Warning alert '${pin.first}' pinned to Live Radar screen map successfully!", Toast.LENGTH_LONG).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Pin to Radar", fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Skill-Matched Local Gig Notifications ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("💼", fontSize = 18.sp)
                        Column {
                            Text("Local Gig Work Matchmaking", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Find local tutoring, pet walking, and repair chores within 2.5km", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    matchedGigsList.forEach { gig ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(gig, fontSize = 10.5.sp, color = Color.White, modifier = Modifier.weight(1f))
                            Button(
                                onClick = {
                                    val otherUser = OtherUserEntity(
                                        username = "Jordan_Alki",
                                        fullName = "Jordan Rivera",
                                        avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100",
                                        bio = "Neighborhood local gig recruiter",
                                        locationName = "Seattle"
                                    )
                                    viewModel.startChatWithUser(otherUser)
                                    coroutineScope.launch {
                                        delay(500)
                                        viewModel.sendChatMessage("Hi Jordan! I've viewed your local gig listing for: '$gig'. I'm highly qualified and would love to apply for this job. Let's coordinate! 💼")
                                    }
                                    Toast.makeText(context, "Chat launched with gig requester to secure the placement! 💼", Toast.LENGTH_LONG).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Apply", fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Gamified Referral Program with Spark Rewards ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🎁", fontSize = 18.sp)
                        Column {
                            Text("Gamified Neighborhood Referrals", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Invite neighbors, expand community verification & earn Sparks", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Verified Invites: $successfulReferralsCount Neighbors", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text("Next Milestone: Reach 5 Invites (Reward: 300 Sparks)", fontSize = 10.sp, color = Color.Gray)
                        }
                        Button(
                            onClick = {
                                Toast.makeText(context, "Invite Code 'LOCALIIY30' copied to clipboard!", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("Share Invite Code", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = inputReferralCode,
                            onValueChange = { inputReferralCode = it },
                            label = { Text("Enter Neighbor's Invite Code", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                        Button(
                            onClick = {
                                if (inputReferralCode.isNotBlank()) {
                                    viewModel.addSparks(100) // Adds referral bonus sparks to real balance
                                    successfulReferralsCount++
                                    Toast.makeText(context, "Referral Code Applied! Added 100 Sparks to your wallet balance!", Toast.LENGTH_LONG).show()
                                    inputReferralCode = ""
                                }
                            }
                        ) {
                            Text("Claim 100 Sparks", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- AI Multi-Cultural Language Translation ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🗣️", fontSize = 18.sp)
                        Column {
                            Text("AI Multi-Lingual Local Translator", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Instantly translate neighbor posts into regional languages using Gemini AI", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Original Post caption:", fontSize = 10.sp, color = Color.Gray)
                    Text(originalPostText, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Translate to: $translateLanguageOption", fontSize = 11.sp, color = Color(0xFF10B981))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    translateLanguageOption = "Hindi 🇮🇳"
                                    translatedPostText = "“आज हमारे शांत जिला ब्लॉक में सुबह-सुबह सुंदर सड़क की सैर!”"
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Hindi", fontSize = 9.sp)
                            }
                            Button(
                                onClick = {
                                    translateLanguageOption = "Spanish 🇪🇸"
                                    translatedPostText = "“¡Hermoso paseo matutino por nuestra tranquila cuadra hoy!”"
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Spanish", fontSize = 9.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Translated Caption:", fontSize = 10.sp, color = Color.Gray)
                    Text(translatedPostText, fontSize = 12.sp, color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- Anonymous/Ghost Spectator Mode ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.2.dp, if (isPrivateAccount) Color(0xFFFF9800) else Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("👻", fontSize = 18.sp)
                            Column {
                                Text("Stealth Ghost Spectator Mode", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Lurk feeds safely with zero digital footprint or GPS tracking", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                        Switch(
                            checked = isPrivateAccount,
                            onCheckedChange = {
                                viewModel.setPrivateAccount(it)
                                Toast.makeText(
                                    context,
                                    if (it) "Stealth Ghost Active. Radar positioning masked." else "Stealth Off. Proximity radar active.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
            }
        }

        // --- Local Event Calendars & Spatial Invites ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("📅", fontSize = 18.sp)
                        Column {
                            Text("Neighborhood Event Calendar", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Garage sales, park cleanups & terrace mixers mapped out", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    activeCommunityEventsList.forEach { ev ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ev.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                Text(ev.second, fontSize = 10.sp, color = Color.LightGray)
                                Text(ev.third, fontSize = 9.sp, color = Color.Gray)
                            }
                            Button(
                                onClick = {
                                    Toast.makeText(context, "Registered for ${ev.first}! Added to your mobile calendar reminder.", Toast.LENGTH_LONG).show()
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("RSVP", fontSize = 9.sp)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }

        // --- Proximity Commute carpool Ledger ---
        item {
            Card(shape = RoundedCornerShape(14.dp), border = BorderStroke(1.dp, Color.DarkGray)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("🚗", fontSize = 18.sp)
                        Column {
                            Text("Neighbor-Pool Commute Carpooling", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                            Text("Share fuel, split tolls & coordinate commutes with block members", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    activeCommutePools.forEach { commute ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Text(commute.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                            Text(commute.second, fontSize = 10.5.sp, color = Color.LightGray)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(commute.third, fontSize = 10.sp, color = Color.Gray)
                                Button(
                                    onClick = {
                                        val otherUser = OtherUserEntity(
                                            username = "Sarah_Alki",
                                            fullName = "Sarah Jenkins",
                                            avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=100",
                                            bio = "Daily commuter interested in pooling fuel & rides",
                                            locationName = "Seattle"
                                        )
                                        viewModel.startChatWithUser(otherUser)
                                        coroutineScope.launch {
                                            delay(500)
                                            viewModel.sendChatMessage("Hi Sarah! I've booked a seat in your carpool commute '${commute.first} - ${commute.second}'. Let me know where the pickup point is. Thanks! 🚗")
                                        }
                                        Toast.makeText(context, "Sarah Jenkins' booking messenger launched to arrange pickup details! 🛵", Toast.LENGTH_LONG).show()
                                    },
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                    modifier = Modifier.height(26.dp)
                                ) {
                                    Text("Book Ride", fontSize = 9.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

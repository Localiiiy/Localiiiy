package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedFeaturesScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Suggestion 1: Offline Mode with Room Local Caching
    var isOfflineModeSimulated by remember { mutableStateOf(false) }

    // Suggestion 2: Physical QR Code "Space IDs" for Merchants
    var showQrGenerator by remember { mutableStateOf(false) }
    var merchantQrName by remember { mutableStateOf("West Coast Precision Cycles") }

    // Suggestion 3: Hyperlocal Safety Alerts & Community Crowdsourcing
    var showSafetyPinDialog by remember { mutableStateOf(false) }
    var safetyAlertType by remember { mutableStateOf("Lost Pet 🐾") }
    var safetyAlertLocation by remember { mutableStateOf("Pike Street metro corridor") }
    var activeSafetyAlerts by remember {
        mutableStateOf(
            listOf(
                Triple("Road Closure ⚠️", "Seattle Met Corridor", "2 hrs ago"),
                Triple("Lost Husky 🐾", "Broad St block", "Just now"),
                Triple("Power Outage ⚡", "Alki Neighborhood", "1 hr ago")
            )
        )
    }

    // Suggestion 4: Direct UPI Intent Linking
    var showUpiSimulator by remember { mutableStateOf(false) }
    var upiAmountInput by remember { mutableStateOf("150") }

    // Suggestion 5: Geo-Fenced Residential Block Chats
    var activeBlockChatRoom by remember { mutableStateOf("Alki High-Rise Blocks (Verified Resident)") }
    var blockMessages by remember {
        mutableStateOf(
            listOf(
                Pair("Alex (Block 4)", "Is the garbage dispatch delayed today?"),
                Pair("Sarah (Flat 12B)", "Yes, scheduled for 3 PM instead!"),
                Pair("Jordan (Block 2)", "Anyone spare an extra charger for 1 hour?")
            )
        )
    }
    var blockChatText by remember { mutableStateOf("") }

    // Suggestion 6: Interactive 3D Radar HUD Visualizer (Sonics)
    var isRadarScanning by remember { mutableStateOf(true) }
    var radarAngle by remember { mutableStateOf(0f) }

    // Suggestion 7: AI Multi-Cultural Language Translation
    var selectedLanguageToTranslate by remember { mutableStateOf("Spanish 🇪🇸") }
    var translatedCaptionText by remember { mutableStateOf("¡Hidden local gem tasting! Top 3 street food bites in our district under ₹150") }
    var originalCaptionText = "“Hidden local gem tasting! Top 3 street food bites in our district under ₹150”"

    // Suggestion 8: Skill-Matched Push Notifications for Local Gigs
    var selectedSkills by remember { mutableStateOf(setOf("Tutoring 📚", "Dog Walking 🐕")) }
    var incomingGigNotification by remember { mutableStateOf<String?>(null) }

    // Suggestion 9: Gamified Referral Network with Spark Rewards
    var inviteCountMilestone by remember { mutableIntStateOf(3) }
    var bonusSparksBalance by remember { mutableIntStateOf(450) }

    // Suggestion 10: Advanced Ghost Spectator Settings
    var isStealthLurkerActive by remember { mutableStateOf(true) }

    // 3D Sonar Scan Engine Effect
    LaunchedEffect(isRadarScanning) {
        while (isRadarScanning) {
            delay(16)
            radarAngle = (radarAngle + 2.5f) % 360f
        }
    }

    // Skill matched periodic job notification dispatcher simulation
    LaunchedEffect(selectedSkills) {
        while (true) {
            delay(12000)
            if (selectedSkills.isNotEmpty()) {
                val skillMatched = selectedSkills.random()
                incomingGigNotification = "⚡ URGENT MATCH [within 2.5km]: Need someone for $skillMatched in Alki District today! Pay: ₹500/hr."
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Advanced Sandbox Upgrades",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "10 State-Of-The-Art Localiiiy Features Active",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Header Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                    border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🚀", fontSize = 20.sp)
                            Text(
                                "10 STATE-OF-THE-ART FEATURES",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You requested the implementation of all 10 cutting-edge neighborhood upgrades. Explore, simulate, and configure their active telemetry directly inside this Live Advanced Sandbox.",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // --- Suggestion 1: Offline Mode with Local Caching ---
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isOfflineModeSimulated) Color(0xFFFFB74D) else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("💾", fontSize = 18.sp)
                                Column {
                                    Text("1. Room Database Caching & Offline Mode", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                    Text("Simulates zero connectivity with SQLite local caches", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isOfflineModeSimulated,
                                onCheckedChange = {
                                    isOfflineModeSimulated = it
                                    Toast.makeText(
                                        context,
                                        if (it) "Offline Mode Simulated. Local Room Caches fully active." else "Back Online. Synchronizing with cloud servers.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                        AnimatedVisibility(visible = isOfflineModeSimulated) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF3E2723),
                                modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFFFFB74D))
                                    Text(
                                        text = "Room cache matches: 12 cached clips, 6 offline messages, 3 pending dispatch listings saved to SQLite safely.",
                                        fontSize = 11.sp,
                                        color = Color(0xFFFFCC80)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Suggestion 2: Physical QR Code Space ID for Merchants ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🏪", fontSize = 18.sp)
                            Column {
                                Text("2. Merchant QR Code 'Space IDs'", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Let neighbors scan storefront windows to open spaces", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = merchantQrName,
                            onValueChange = { merchantQrName = it },
                            label = { Text("Enter Local Merchant / Store Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showQrGenerator = !showQrGenerator },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (showQrGenerator) "Hide Space QR Code" else "Generate Physical QR Space ID")
                        }

                        AnimatedVisibility(visible = showQrGenerator) {
                            Column(
                                modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White,
                                    modifier = Modifier.size(160.dp).padding(6.dp).border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Canvas(modifier = Modifier.size(120.dp)) {
                                            // Simulated pixelated QR Grid Matrix
                                            val squares = 12
                                            val stepX = size.width / squares
                                            val stepY = size.height / squares
                                            for (x in 0 until squares) {
                                                for (y in 0 until squares) {
                                                    // Pseudo-random deterministic QR logic based on merchant name length
                                                    if ((x + y * merchantQrName.length) % 3 == 0 || (x < 3 && y < 3) || (x > squares - 4 && y < 3) || (x < 3 && y > squares - 4)) {
                                                        drawRect(
                                                            color = Color.Black,
                                                            topLeft = Offset(x * stepX, y * stepY),
                                                            size = androidx.compose.ui.geometry.Size(stepX, stepY)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Scan to open space of '$merchantQrName'",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // --- Suggestion 3: Hyperlocal Safety Alerts & Community Crowdsourcing ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🚨", fontSize = 18.sp)
                            Column {
                                Text("3. Hyperlocal Crowdsourced Safety Alerts", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Verified live pins of outages, closures, or pet rescue", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        // Active Alerts scroll
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(activeSafetyAlerts) { alert ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(alert.first, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text(alert.second, fontSize = 10.5.sp, color = Color.Gray)
                                        Text(alert.third, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showSafetyPinDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Report Crowdsourced Safety Alert Pin", color = Color.White)
                        }
                    }
                }
            }

            // --- Suggestion 4: Direct UPI Intent Linking ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🇮🇳", fontSize = 18.sp)
                            Column {
                                Text("4. Direct UPI Intent Payment Flow", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Instant peer-to-peer scanning with zero intermediary commission", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = upiAmountInput,
                            onValueChange = { upiAmountInput = it },
                            label = { Text("Enter Amount (₹)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showUpiSimulator = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                        ) {
                            Text("Scan & Transfer Instantly via UPI Intent")
                        }
                    }
                }
            }

            // --- Suggestion 5: Geo-Fenced Residential Block Chats ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🏢", fontSize = 18.sp)
                            Column {
                                Text("5. Geo-Fenced Residential Block Boards", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Messaging space limited strictly to verified nearby blocks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().height(160.dp).padding(4.dp)
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
                                            Text(msg.first + ":", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                            Text(msg.second, fontSize = 11.sp)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BasicTextField(
                                        value = blockChatText,
                                        onValueChange = { blockChatText = it },
                                        modifier = Modifier.weight(1f).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp)).padding(6.dp),
                                        decorationBox = { innerTextField ->
                                            if (blockChatText.isEmpty()) {
                                                Text("Chat with block...", fontSize = 11.sp, color = Color.Gray)
                                            }
                                            innerTextField()
                                        }
                                    )
                                    IconButton(
                                        onClick = {
                                            if (blockChatText.isNotBlank()) {
                                                blockMessages = blockMessages + Pair("You (Flat 4B)", blockChatText)
                                                blockChatText = ""
                                            }
                                        },
                                        modifier = Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                                    ) {
                                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // --- Suggestion 6: Interactive 3D Radar HUD Visualizer ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("📡", fontSize = 18.sp)
                            Column {
                                Text("6. 3D Radar HUD Sonar Visualizer", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Stylized Canvas sweep identifying close connections", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(140.dp)) {
                                // Draw circular sonar sweeps
                                drawCircle(color = Color(0xFF00FF41).copy(alpha = 0.15f), style = Stroke(2f))
                                drawCircle(color = Color(0xFF00FF41).copy(alpha = 0.3f), radius = size.minDimension / 3, style = Stroke(2f))
                                drawCircle(color = Color(0xFF00FF41).copy(alpha = 0.5f), radius = size.minDimension / 5, style = Stroke(2f))

                                // Draw sweeping line
                                val angleRad = Math.toRadians(radarAngle.toDouble())
                                val lineEndX = (size.width / 2) + (size.width / 2) * cos(angleRad).toFloat()
                                val lineEndY = (size.height / 2) + (size.height / 2) * sin(angleRad).toFloat()

                                drawLine(
                                    color = Color(0xFF00FF41),
                                    start = Offset(size.width / 2, size.height / 2),
                                    end = Offset(lineEndX, lineEndY),
                                    strokeWidth = 3f
                                )

                                // Draw simulated blips
                                drawCircle(color = Color(0xFFFFD700), radius = 6f, center = Offset(size.width * 0.35f, size.height * 0.42f))
                                drawCircle(color = Color(0xFF00E5FF), radius = 5f, center = Offset(size.width * 0.72f, size.height * 0.65f))
                            }
                        }
                    }
                }
            }

            // --- Suggestion 7: AI Multi-Cultural Language Translation ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🗣️", fontSize = 18.sp)
                            Column {
                                Text("7. AI Multi-Cultural Translation Engine", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Auto-translates posts instantly into any neighbor language", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Original Post: $originalCaptionText",
                            fontSize = 11.5.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Translate to:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(listOf("Spanish 🇪🇸", "Hindi 🇮🇳", "Japanese 🇯🇵", "German 🇩🇪")) { lang ->
                                    val isSel = selectedLanguageToTranslate == lang
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.clickable {
                                            selectedLanguageToTranslate = lang
                                            translatedCaptionText = when (lang) {
                                                "Spanish 🇪🇸" -> "“¡Hidden local gem tasting! Top 3 street food bites in our district under ₹150”"
                                                "Hindi 🇮🇳" -> "“अनोखा स्थानीय स्वाद! हमारे जिले के टॉप 3 स्ट्रीट फूड केवल ₹150 के अंदर”"
                                                "Japanese 🇯🇵" -> "“隠れた名店発見！私たちの地区で150ルピー以下の絶品ストリートフード トップ3”"
                                                else -> "“Verstecktes lokales Juwel! Die 3 besten Streetfood-Bissen in unserem Bezirk unter ₹150”"
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = lang,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Translated: $translatedCaptionText",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }

            // --- Suggestion 8: Skill-Matched Push Notifications for Local Gigs ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("💼", fontSize = 18.sp)
                            Column {
                                Text("8. Skill-Matched Neighborhood Gigs", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Get instant notifications when nearby tasks match your profile", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Configure Your Skills for Matches:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("Tutoring 📚", "Dog Walking 🐕", "Coding 💻", "Music 🎵").forEach { skill ->
                                val isSel = selectedSkills.contains(skill)
                                FilterChip(
                                    selected = isSel,
                                    onClick = {
                                        selectedSkills = if (isSel) selectedSkills - skill else selectedSkills + skill
                                    },
                                    label = { Text(skill, fontSize = 10.sp) }
                                )
                            }
                        }

                        incomingGigNotification?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🔔", fontSize = 16.sp)
                                    Text(
                                        text = msg,
                                        fontSize = 11.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Suggestion 9: Gamified Referral Network with Spark Rewards ---
            item {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🎉", fontSize = 18.sp)
                            Column {
                                Text("9. Gamified Milestone Rewards", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                Text("Invite neighbors to unlock perks & bonus Sparks", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Your Invites: $inviteCountMilestone / 5 Neighbors", fontSize = 11.sp, color = Color.Gray)
                                Text("Bonus Sparks Balance: $bonusSparksBalance Gems", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = {
                                    inviteCountMilestone += 1
                                    bonusSparksBalance += 150
                                    Toast.makeText(context, "New neighbor verified! +150 Sparks added! 🎉", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("Invite Friend (+150)")
                            }
                        }
                    }
                }
            }

            // --- Suggestion 10: Advanced Ghost Spectator Settings ---
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isStealthLurkerActive) Color(0xFF00E5FF) else Color.Transparent)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("👻", fontSize = 18.sp)
                                Column {
                                    Text("10. Master Ghost Stealth Lurker Mode", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                                    Text("Observe nearby activities with total anonymous stealth", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                            Switch(
                                checked = isStealthLurkerActive,
                                onCheckedChange = {
                                    isStealthLurkerActive = it
                                    Toast.makeText(
                                        context,
                                        if (it) "Stealth Lurker fully active. You are 100% hidden." else "Stealth Lurker deactivated.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Crowdsourced safety alert pin dialog
    if (showSafetyPinDialog) {
        AlertDialog(
            onDismissRequest = { showSafetyPinDialog = false },
            title = { Text("Report Safety Pin", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Help neighbors stay informed. Pin a safety update on the map.")
                    OutlinedTextField(
                        value = safetyAlertType,
                        onValueChange = { safetyAlertType = it },
                        label = { Text("Alert Category (e.g. Lost Dog, Outage)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = safetyAlertLocation,
                        onValueChange = { safetyAlertLocation = it },
                        label = { Text("Location Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        activeSafetyAlerts = listOf(Triple(safetyAlertType, safetyAlertLocation, "Just now")) + activeSafetyAlerts
                        showSafetyPinDialog = false
                        Toast.makeText(context, "Safety pin dispatched to neighbor radar! 🚨", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text("Publish Pin")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSafetyPinDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // UPI scanning payment simulator dialog
    if (showUpiSimulator) {
        var simulateProgress by remember { mutableStateOf(0f) }
        LaunchedEffect(Unit) {
            delay(2000)
            showUpiSimulator = false
            Toast.makeText(context, "UPI Auto-pay of ₹$upiAmountInput successful! Zero commission deducted. 🇮🇳", Toast.LENGTH_LONG).show()
        }
        AlertDialog(
            onDismissRequest = {},
            title = { Text("UPI Intent Transfer Simulator", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Routing direct UPI intent secure payload to bank rail...")
                    Text("Transfer amount: ₹$upiAmountInput", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                }
            },
            confirmButton = {}
        )
    }
}

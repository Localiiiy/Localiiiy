package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.ui.components.CreatorSupportModal
import com.example.ui.components.PaymentGatewayDialog
import com.example.ui.components.PaymentGatewayProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.LocaliiiyCurrency
import com.example.util.PremiumPricingHelper
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumSubscriptionScreen(
    isSubscribed: Boolean = false,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.INR,
    onNavigateBack: () -> Unit,
    onSubscribe: (isAnnual: Boolean) -> Unit,
    onCancelSubscription: () -> Unit = {}
) {
    var selectedPlanIsAnnual by remember { mutableStateOf(false) }
    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    var showCelebrationDialog by remember { mutableStateOf(false) }
    var activeCurrency by remember { mutableStateOf(currentCurrency) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    // Subscribed User Interactive Cockpit Features
    var selectedAuraIndex by remember { mutableIntStateOf(0) }
    var selectedTagline by remember { mutableStateOf("⚡ Open for Collabs") }
    var isFiringSonarPing by remember { mutableStateOf(false) }
    var sonarPingFiredTime by remember { mutableLongStateOf(0L) }
    var generatedAiPromptCategory by remember { mutableStateOf("☕ Cafe & Foodie") }
    var promptCopiedToast by remember { mutableStateOf(false) }
    var showPaymentGatewayDialog by remember { mutableStateOf(false) }
    var showCreatorSupportPreview by remember { mutableStateOf(false) }
    var capturedTransactionId by remember { mutableStateOf("") }
    var capturedGatewayProviderName by remember { mutableStateOf("Razorpay Fast Pay") }
    var isTippingJarEnabled by remember { mutableStateOf(true) }
    var isPatronTierEnabled by remember { mutableStateOf(true) }

    val clipboardManager = LocalClipboardManager.current
    val planPrice = remember(activeCurrency) {
        PremiumPricingHelper.getPlanPrice(activeCurrency)
    }

    // Glowing animation for energetic vibe
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Auto-reset sonar animation
    LaunchedEffect(isFiringSonarPing) {
        if (isFiringSonarPing) {
            delay(3000)
            isFiringSonarPing = false
            sonarPingFiredTime = System.currentTimeMillis()
        }
    }

    LaunchedEffect(promptCopiedToast) {
        if (promptCopiedToast) {
            delay(2000)
            promptCopiedToast = false
        }
    }

    val auraOptions = listOf(
        Triple("🌟 Solar Gold", Color(0xFFFFD700), Color(0xFFFF9100)),
        Triple("⚡ Cyber Cyan", Color(0xFF00E5FF), Color(0xFF0077C2)),
        Triple("💜 Neon Violet", Color(0xFFD500F9), Color(0xFF651FFF)),
        Triple("🔥 Emerald Fire", Color(0xFF00E676), Color(0xFF1B5E20))
    )

    val quickCurrencies = listOf(
        LocaliiiyCurrency.INR,
        LocaliiiyCurrency.USD,
        LocaliiiyCurrency.EUR,
        LocaliiiyCurrency.GBP,
        LocaliiiyCurrency.CAD,
        LocaliiiyCurrency.AED
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isSubscribed) "Titan Cockpit" else "Localiiiy Premium",
                            fontWeight = FontWeight.Black,
                            fontSize = 19.sp
                        )
                        Surface(
                            color = Color(0xFFFFD700).copy(alpha = 0.25f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "TITAN PRO",
                                color = Color(0xFFFFD700),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Currency selector chip in top bar
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFF262626),
                        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable { showCurrencyDialog = true }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(activeCurrency.flag, fontSize = 13.sp)
                            Text(
                                activeCurrency.code,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFFFD700)
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Change Currency",
                                tint = Color.LightGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF101010),
                    titleContentColor = Color(0xFFFFD700),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F0F0F)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Global Currency Quick-Switcher Bar
            item {
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(15.dp))
                            Text(
                                text = "Affordable Worldwide Pricing",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            )
                        }
                        Text(
                            text = "All 160+ Currencies ▾",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64B5F6),
                            modifier = Modifier.clickable { showCurrencyDialog = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickCurrencies) { curr ->
                            val isSelected = curr == activeCurrency
                            val p = remember(curr) { PremiumPricingHelper.getPlanPrice(curr) }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) Color(0xFFFFD700) else Color(0xFF1E1E1E),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) Color(0xFFFFD700) else Color.DarkGray
                                ),
                                modifier = Modifier.clickable { activeCurrency = curr }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(curr.flag, fontSize = 12.sp)
                                    Text(
                                        text = "${curr.code} ${p.monthlyPriceDisplay}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Energetic Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = if (isSubscribed) {
                                    listOf(Color(0xFF2A2208), Color(0xFF181504), Color(0xFF121212))
                                } else {
                                    listOf(Color(0xFF332906), Color(0xFF1F1803), Color(0xFF101010))
                                }
                            )
                        )
                        .border(
                            1.5.dp,
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFD700), Color(0xFFFF9100), Color(0xFF00E5FF))
                            ),
                            RoundedCornerShape(20.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Pulsing Icon Ring
                        Box(contentAlignment = Alignment.Center) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFD700).copy(alpha = 0.15f * glowAlpha),
                                modifier = Modifier
                                    .size(92.dp)
                                    .scale(pulseScale)
                            ) {}
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFF9100).copy(alpha = 0.25f),
                                modifier = Modifier.size(72.dp)
                            ) {}
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFD700),
                                modifier = Modifier.size(54.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isSubscribed) Icons.Default.FlashOn else Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isSubscribed) "⚡ HYPERLOCAL TITAN: ACTIVE 👑" else "⚡ UNLEASH HYPERLOCAL TITAN",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFFD700),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = if (isSubscribed)
                                "Your profile radiates 5x priority reach across neighbor feeds, 0% market escrow, and a glowing sonar radar."
                            else
                                "Reduced to ${planPrice.monthlyPriceDisplay}/mo so every neighbor and creator worldwide can supercharge their local reach full-time.",
                            fontSize = 13.sp,
                            color = Color(0xFFE0E0E0),
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
                        )

                        // Free Ghost Mode Guarantee Badge
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFF0D2533),
                            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.4f)),
                            modifier = Modifier.padding(top = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "Permanent Guarantee: Master Ghost Privacy Shield is 100% Free for all",
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF80D8FF),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // SUBSCRIBED USER COCKPIT: Live Telemetry, Radar Sonar Ping & Customizer
            if (isSubscribed) {
                // Live Telemetry Matrix
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616)),
                        border = BorderStroke(1.5.dp, Color(0xFF00E676))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                                    Text("LIVE POWER MATRIX", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFF00E676))
                                }
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color(0xFF00E676).copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${planPrice.monthlyPriceDisplay} / mo Active",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF00E676),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // 4 Matrix Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PowerMatrixCell(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Bolt,
                                    title = "5x Boost",
                                    value = "ACTIVE",
                                    valueColor = Color(0xFFFFD700)
                                )
                                PowerMatrixCell(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Storefront,
                                    title = "Escrow Fee",
                                    value = "0% FEE",
                                    valueColor = Color(0xFF00E676)
                                )
                                PowerMatrixCell(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.Radar,
                                    title = "Radar Sonar",
                                    value = "12.5 KM",
                                    valueColor = Color(0xFF00E5FF)
                                )
                                PowerMatrixCell(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Default.VideoCall,
                                    title = "4K HDR",
                                    value = "UNCAPPED",
                                    valueColor = Color(0xFFFF80AB)
                                )
                            }
                        }
                    }
                }

                // Interactive Radar Sonar Ping Trigger
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A0E)),
                        border = BorderStroke(1.2.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFFF9100), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "Interactive Radar Sonar Ping",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                            Text(
                                text = "Fire an instant high-energy sonar ping to light up your beacon with an animated pulse on all nearby neighbor radars for 60 minutes.",
                                fontSize = 12.sp,
                                color = Color(0xFFD4C8A5),
                                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                            )

                            Button(
                                onClick = { isFiringSonarPing = true },
                                enabled = !isFiringSonarPing,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFiringSonarPing) Color(0xFF00E5FF) else Color(0xFFFFD700),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isFiringSonarPing) Icons.Default.Radar else Icons.Default.FlashOn,
                                        contentDescription = null,
                                        tint = Color.Black
                                    )
                                    Text(
                                        text = if (isFiringSonarPing) "⚡ BROADCASTING SONAR PULSE..." else "⚡ Fire Hyperlocal Sonar Ping",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            if (sonarPingFiredTime > 0L) {
                                Text(
                                    text = "✓ Sonar Beacon Pulsing! Boosted 150% in neighbor radars for the next 60m",
                                    fontSize = 11.5.sp,
                                    color = Color(0xFF81C784),
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                    }
                }

                // Radar Beacon Aura Customizer
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
                        border = BorderStroke(1.dp, Color.DarkGray)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(19.dp))
                                Text(
                                    text = "Custom Radar Aura & Status",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Select your signature glowing blip halo displayed across the neighborhood radar:",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )

                            // 4 Aura Options
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                auraOptions.forEachIndexed { index, (name, c1, _) ->
                                    val isSelected = selectedAuraIndex == index
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) c1.copy(alpha = 0.2f) else Color(0xFF222222),
                                        border = BorderStroke(
                                            if (isSelected) 2.dp else 1.dp,
                                            if (isSelected) c1 else Color.DarkGray
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedAuraIndex = index }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(c1)
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = name.split(" ").last(),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) c1 else Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Radar Status Tagline:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.LightGray
                            )

                            val taglines = listOf(
                                "⚡ Open for Collabs",
                                "☕ Coffee & Tech Chat",
                                "🛍️ Flash Neighborhood Deals",
                                "🛠️ Available for Freelance"
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                items(taglines) { tag ->
                                    val isSelected = selectedTagline == tag
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) Color(0xFFFFD700) else Color(0xFF242424),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFD700) else Color.DarkGray),
                                        modifier = Modifier.clickable { selectedTagline = tag }
                                    ) {
                                        Text(
                                            text = tag,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // AI Hyperlocal Studio & Creative Prompt Suggester
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141C24)),
                        border = BorderStroke(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(19.dp))
                                Text(
                                    text = "AI Hyperlocal Studio & Prompts",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF00E5FF)
                                )
                            }
                            Text(
                                text = "Titan perk: Generate viral hyperlocal clip hooks, trending neighbor hashtags, and marketplace copy in 1 tap.",
                                fontSize = 12.sp,
                                color = Color(0xFFB0BEC5),
                                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                            )

                            val samplePrompts = when (generatedAiPromptCategory) {
                                "☕ Cafe & Foodie" -> "“Hidden local gem tasting! Top 3 street food bites in our district under ${planPrice.currency.symbol}150 #LocalEats #NeighborFood #DistrictFinds”"
                                "💻 Tech & Indie" -> "“Building open-source tools right from our metro district. Looking for 2 local dev collaborators! #LocalTech #IndieHacker #OpenSource”"
                                "🛍️ Vintage & Deals" -> "“Flash weekend clean-out! Mint condition retro gear. Free local pickup near metro anchor. #LocalMarket #ThriftFinds”"
                                else -> "“Weekend bike trail discovery meetup at 7:00 AM! Comment if you want to join the group ride. #LocalTrails #CommunityFitness”"
                            }

                            // Category selector
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(bottom = 10.dp)
                            ) {
                                items(listOf("☕ Cafe & Foodie", "💻 Tech & Indie", "🛍️ Vintage & Deals", "🚲 Weekend Meetup")) { cat ->
                                    val isSelected = generatedAiPromptCategory == cat
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF1D2833),
                                        modifier = Modifier.clickable { generatedAiPromptCategory = cat }
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.Black else Color.LightGray,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF0F151B),
                                border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = samplePrompts,
                                        fontSize = 12.5.sp,
                                        color = Color.White,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        TextButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(samplePrompts))
                                                promptCopiedToast = true
                                            }
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(15.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (promptCopiedToast) "Copied ✓" else "Copy Hook",
                                                color = Color(0xFF00E5FF),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Creator Support & Neighborhood Patron Suite (Tipping Jar & ₹99/mo Sub-Tier)
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1A0C)),
                        border = BorderStroke(1.2.dp, Color(0xFFFFD700).copy(alpha = 0.6f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("☕", fontSize = 20.sp)
                                Text(
                                    text = "Creator Support & Patron Suite",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.5.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                            Text(
                                text = "Exclusive Titan privilege: Activate your neighborhood tipping jar and offer a ₹99/month ($0.99) patron tier to your local followers with 0% platform fee.",
                                fontSize = 12.sp,
                                color = Color(0xFFD4C8A5),
                                lineHeight = 17.sp
                            )

                            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f))

                            // Tipping Jar Toggle & Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Local Tipping Jar", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                                    Text("Accept instant chai, snack & super tips via UPI / Cards", fontSize = 11.sp, color = Color.Gray)
                                }
                                Switch(
                                    checked = isTippingJarEnabled,
                                    onCheckedChange = { isTippingJarEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700), checkedTrackColor = Color(0xFF382F0A))
                                )
                            }

                            if (isTippingJarEnabled) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF26210E),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Tips Earned: ₹3,850", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFFFFD700))
                                        Text("64 tips received ✓", fontSize = 11.sp, color = Color(0xFF81C784))
                                    }
                                }
                            }

                            // ₹99/mo Patron Tier Toggle & Details
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Neighborhood Patron (₹99/mo)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
                                    Text("Offer exclusive perks to loyal neighborhood supporters", fontSize = 11.sp, color = Color.Gray)
                                }
                                Switch(
                                    checked = isPatronTierEnabled,
                                    onCheckedChange = { isPatronTierEnabled = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFD700), checkedTrackColor = Color(0xFF382F0A))
                                )
                            }

                            if (isPatronTierEnabled) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF26210E),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Active Patrons: 18", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color(0xFFFFD700))
                                        Text("₹1,782/mo recurring income ✓", fontSize = 11.sp, color = Color(0xFF81C784))
                                    }
                                }
                            }

                            OutlinedButton(
                                onClick = { showCreatorSupportPreview = true },
                                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFD700)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("☕")
                                    Text("Preview My Supporter & Patron Sheet", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Cancel Subscription Option
                item {
                    OutlinedButton(
                        onClick = { showCancelConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF8A80)),
                        border = BorderStroke(1.dp, Color(0xFFFF8A80).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cancel Subscription", fontSize = 13.sp)
                    }
                }
            } else {
                // PLAN SELECTION FOR UPGRADING USERS (Reduced 299 INR / $2.99 USD)
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Choose Your Affordable Plan",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFFFD700).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "ROUNDED FOR ALL COUNTRIES",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Monthly Plan Card
                        PlanSelectionCard(
                            title = "Monthly Membership",
                            price = "${planPrice.monthlyPriceDisplay} / mo",
                            subtext = planPrice.dailyCostBreakdown,
                            badge = planPrice.monthlySavingsTag,
                            badgeColor = Color(0xFFFFD700),
                            isSelected = !selectedPlanIsAnnual,
                            onClick = { selectedPlanIsAnnual = false }
                        )

                        // Annual Plan Card
                        PlanSelectionCard(
                            title = "Annual Titan Membership",
                            price = "${planPrice.annualPriceDisplay} / yr",
                            subtext = "${planPrice.annualMonthlyBreakdown} • Full 12 Months",
                            badge = planPrice.annualSavingsTag,
                            badgeColor = Color(0xFF00E676),
                            isSelected = selectedPlanIsAnnual,
                            onClick = { selectedPlanIsAnnual = true }
                        )
                    }
                }

                // COMPREHENSIVE FEATURES LIST (What is added in Titan)
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF181818)),
                        border = BorderStroke(1.2.dp, Color(0xFFFFD700).copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(20.dp))
                                Text(
                                    text = "All Titan Pro Benefits Included",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFFFFD700)
                                )
                            }
                            Text(
                                text = "Every benefit below is unlocked instantly across the app:",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                            )

                            HorizontalDivider(color = Color.DarkGray.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 6.dp))

                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Bolt,
                                title = "5x Hyperlocal Reach & Feed Multiplier",
                                description = "Your posts, clips, and marketplace goods receive 5x priority distribution on neighbor feeds and live radar spotlight."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Storefront,
                                title = "0% Safe-Haven Market Escrow Platform Fee",
                                description = "Keep 100% of your selling price ($0 seller fee instead of 5%) with complimentary Escrow Protection & instant buyer QR handoff."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Radar,
                                title = "Interactive Radar Sonar Beacon & Trade Filters",
                                description = "Broadcast glowing radar pulses, customize your neon halo aura, and filter nearby locals by skill and emergency tags."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.AutoAwesome,
                                title = "AI Hyperlocal Studio & Smart Caption Generator",
                                description = "Instant AI generator for local clip captions, trending neighborhood hashtags, and video hooks to grow your brand."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Stars,
                                title = "Gold Holographic Halo & Verified Titan Badge",
                                description = "Stand out with an animated golden halo and verified badge on your avatar, comments, blips, and marketplace listings."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Send,
                                title = "1x Weekly Hyperlocal Blast Broadcast",
                                description = "Drop a high-priority neighborhood pin broadcast to all active neighbors within a 10 km radius."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Insights,
                                title = "Neighborhood Footprint & Heatmap Telemetry",
                                description = "View real-time foot-traffic heatmaps, listing viewer counts, and peak neighborhood engagement hours."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.VideoCall,
                                title = "4K Ultra-HD Long Studio & Uncapped Bitrate",
                                description = "Upload high-bitrate 4K HDR long-form video podcasts up to 240 minutes with zero video compression loss."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Headphones,
                                title = "100% Ad-Free & Background Reel Audio",
                                description = "Listen to clips, local DJ sets, and audio podcasts in the background with screen locked and zero ads."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Radar,
                                title = "Hyperlocal Radar Precision (1k, 3k & 10k Ranges)",
                                description = "Authorized access to 1 km, 3 km, and 10 km precision radar filters to discover close neighbors. (Free tier is 50 km and above)."
                            )
                            PremiumAdvancedFeatureItem(
                                icon = Icons.Default.Stars,
                                title = "Local Tipping Jar & ₹99/mo Patron Sub-Tier",
                                description = "Activate your own neighborhood tipping jar and offer ₹99/mo patron subscriptions to your local fans with 0% platform commissions."
                            )
                        }
                    }
                }

                // CTA Subscribe Button
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF3B330F), Color(0xFF26210A), Color(0xFF181818))
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(2.dp, Color(0xFFFFD700), RoundedCornerShape(20.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (selectedPlanIsAnnual) planPrice.annualPeriodDisplay else planPrice.monthlyPeriodDisplay,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (selectedPlanIsAnnual)
                                    "${planPrice.annualMonthlyBreakdown} • Save 22% • Cancel anytime"
                                else
                                    "${planPrice.dailyCostBreakdown} • Cancel anytime",
                                fontSize = 12.sp,
                                color = Color(0xFFFFE082),
                                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                                textAlign = TextAlign.Center
                            )

                            Button(
                                onClick = {
                                    showPaymentGatewayDialog = true
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFD700),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.Black)
                                    Text(
                                        text = if (selectedPlanIsAnnual)
                                            "Pay Now (${planPrice.annualPriceDisplay}/yr)"
                                        else
                                            "Pay Now (${planPrice.monthlyPriceDisplay}/mo)",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                                    Text("Instant UPI / GPlay / Stripe", fontSize = 11.sp, color = Color.LightGray)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(14.dp))
                                    Text("256-Bit Encrypted", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Cancellation Dialog
    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            title = { Text("Cancel Premium Subscription?", fontWeight = FontWeight.Bold) },
            text = { Text("Your 5x Hyperlocal Reach, 0% Market Escrow, and Gold Halo will remain active until the end of your billing cycle.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCancelSubscription()
                        showCancelConfirmDialog = false
                    }
                ) {
                    Text("Confirm Cancel", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelConfirmDialog = false }) {
                    Text("Keep Premium", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Celebration Dialog on subscribing
    if (showCelebrationDialog) {
        AlertDialog(
            onDismissRequest = { showCelebrationDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text(
                    text = "Welcome to Titan Pro! 👑",
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your 5x Hyperlocal Multiplier and 0% Market Escrow are now fully energized. Your profile blip now radiates across neighbor feeds!",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF262626)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (selectedPlanIsAnnual)
                                    "Billed ${planPrice.annualPriceDisplay}/year"
                                else
                                    "Billed ${planPrice.monthlyPriceDisplay}/month",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700),
                                fontSize = 13.sp
                            )
                            if (capturedTransactionId.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$capturedGatewayProviderName • $capturedTransactionId",
                                    fontSize = 10.sp,
                                    color = Color(0xFF81C784)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showCelebrationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black)
                ) {
                    Text("Enter Titan Cockpit", fontWeight = FontWeight.Black)
                }
            }
        )
    }

    // Payment Gateway Dialog (Google Play, Razorpay UPI, Stripe)
    if (showPaymentGatewayDialog) {
        PaymentGatewayDialog(
            planTitle = if (selectedPlanIsAnnual) "Localiiiy Titan Annual Membership" else "Localiiiy Titan Monthly Membership",
            amountDisplay = if (selectedPlanIsAnnual) planPrice.annualPeriodDisplay else planPrice.monthlyPeriodDisplay,
            onDismissRequest = { showPaymentGatewayDialog = false },
            onPaymentSuccess = { provider, txnId ->
                capturedGatewayProviderName = provider.title
                capturedTransactionId = txnId
                showPaymentGatewayDialog = false
                onSubscribe(selectedPlanIsAnnual)
                showCelebrationDialog = true
            }
        )
    }

    // Creator Support Modal Preview
    if (showCreatorSupportPreview) {
        CreatorSupportModal(
            creatorName = "Alex Rivera",
            creatorUsername = "alex_creative",
            creatorAvatar = "",
            onDismissRequest = { showCreatorSupportPreview = false },
            onSendTip = { _, _ -> },
            onJoinPatron = { }
        )
    }

    // Full 160+ Currency Selection Dialog
    if (showCurrencyDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredCurrencies: List<LocaliiiyCurrency> = remember(searchQuery) {
            val allList = LocaliiiyCurrency.entries
            if (searchQuery.isBlank()) {
                allList
            } else {
                allList.filter { c ->
                    c.code.contains(searchQuery, ignoreCase = true) ||
                            c.country.contains(searchQuery, ignoreCase = true) ||
                            c.currencyName.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = {
                Column {
                    Text("Select Your Local Currency", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Rounded affordable equivalent for every country", fontSize = 11.5.sp, color = Color.Gray)
                }
            },
            text = {
                Column(modifier = Modifier.height(380.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search country or code (USD, INR, EUR)...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        singleLine = true
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(filteredCurrencies) { curr: LocaliiiyCurrency ->
                            val isSelected = curr == activeCurrency
                            val p = remember(curr) { PremiumPricingHelper.getPlanPrice(curr) }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Color(0xFFFFD700).copy(alpha = 0.15f) else Color.Transparent,
                                border = if (isSelected) BorderStroke(1.dp, Color(0xFFFFD700)) else null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        activeCurrency = curr
                                        showCurrencyDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(curr.flag, fontSize = 18.sp)
                                        Column {
                                            Text("${curr.code} • ${curr.country}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(curr.currencyName, fontSize = 10.5.sp, color = Color.Gray)
                                        }
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(p.monthlyPriceDisplay, fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color(0xFFFFD700))
                                        Text("per month", fontSize = 9.5.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PowerMatrixCell(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFF202020),
        border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, fontSize = 9.sp, color = Color.LightGray, maxLines = 1)
            Text(text = value, fontSize = 10.5.sp, fontWeight = FontWeight.Black, color = valueColor, maxLines = 1)
        }
    }
}

@Composable
private fun PlanSelectionCard(
    title: String,
    price: String,
    subtext: String,
    badge: String,
    badgeColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) Color(0xFF2A2610) else Color(0xFF1E1E1E),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFFFFD700) else Color.DarkGray
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color.White)
                Text(text = subtext, fontSize = 11.5.sp, color = Color(0xFFAAAAAA))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = price, fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (isSelected) Color(0xFFFFD700) else Color.White)
                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFFFD700), unselectedColor = Color.Gray)
                )
            }
        }
    }
}

@Composable
private fun PremiumAdvancedFeatureItem(
    icon: ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.padding(vertical = 9.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFFFD700).copy(alpha = 0.15f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(17.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = Color.White)
            Text(
                text = description,
                fontSize = 11.5.sp,
                color = Color(0xFFB0B0B0),
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

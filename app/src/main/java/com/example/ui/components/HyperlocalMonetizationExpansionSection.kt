package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FlashPinDropCampaign
import com.example.data.HyperlocalDealPassport
import com.example.data.NeighborhoodSquadGoal
import com.example.data.SafeHavenEscrowShieldState
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency

@Composable
fun HyperlocalMonetizationExpansionSection(
    animFactor: Float,
    currentCurrency: LocaliiiyCurrency,
    flashPinDrops: List<FlashPinDropCampaign>,
    dealPassports: List<HyperlocalDealPassport>,
    squadGoals: List<NeighborhoodSquadGoal>,
    safeHavenShieldState: SafeHavenEscrowShieldState,
    onLaunchFlashPinDrop: (title: String, headline: String, landmark: String, durationHours: Int) -> Unit,
    onCreateDealPassport: (title: String, neighborhood: String, spots: List<String>, priceUSD: Double) -> Unit,
    onPurchaseDealPassport: (String) -> Unit,
    onCreateSquadGoal: (title: String, description: String, landmark: String, targetUSD: Double) -> Unit,
    onChipInToSquadGoal: (String, Double) -> Unit,
    onToggleSafeHavenShield: (Boolean) -> Unit,
    onRecordProtectedHandshake: () -> Unit
) {
    var showLaunchPinDialog by remember { mutableStateOf(false) }
    var showCreatePassportDialog by remember { mutableStateOf(false) }
    var showCreateSquadGoalDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hyperlocal_monetization_expansion_section"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hyperlocal Commerce & Radar Suite",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "0km-to-World High-Margin Creator & Merchant Engines",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = Color(0xFF10B981).copy(alpha = 0.16f),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
            ) {
                Text(
                    text = "4 NEW ENGINES",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF10B981),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // 1. Flash Pin Drops (1–6 Hour Live Radar Surge)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("flash_pin_drop_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF59E0B).copy(alpha = 0.10f)
            ),
            border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚡", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Flash Pin Drops (1–6h Radar Surge)",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF59E0B)
                            )
                            Text(
                                text = "${flashPinDrops.size} active glowing radar pins • $4.99–$19.99 instant surge",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { showLaunchPinDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF59E0B),
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("launch_flash_pin_button")
                    ) {
                        Text("Drop Pin", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                flashPinDrops.take(2).forEach { pin ->
                    val animClaims = (pin.claimsCount * animFactor).toInt()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = pin.offerHeadline,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "📍 ${pin.landmark} • ${pin.durationHours}h window • ${pin.radiusKm}km radius",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFFF59E0B).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "$animClaims/${pin.maxClaims} claimed",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF59E0B),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Hyperlocal Deal Passports (70% Merchant / 15% Creator Curator / 15% Platform)
        val totalCuratorPassportEarnedUSD = dealPassports.sumOf { it.curatorEarnedUSD } * animFactor
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hyperlocal_deal_passports_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF06B6D4).copy(alpha = 0.10f)
            ),
            border = BorderStroke(1.dp, Color(0xFF06B6D4).copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🎟️", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Hyperlocal Deal Passports",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF06B6D4)
                            )
                            Text(
                                text = "70% Merchant • 15% Creator Curator • 15% Platform",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = CurrencyHelper.format(totalCuratorPassportEarnedUSD, currentCurrency),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF06B6D4)
                        )
                        Text(
                            text = "+ Curate Pass",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { showCreatePassportDialog = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                dealPassports.take(2).forEach { passport ->
                    val animSold = (passport.soldCount * animFactor).toInt()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = passport.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${passport.neighborhood} • $animSold passes sold • ${CurrencyHelper.format(passport.priceUSD, currentCurrency)}",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(
                                onClick = { onPurchaseDealPassport(passport.id) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+1 Sale", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // 3. Community Crowd-Tipping ("Neighborhood Squad Goals")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("neighborhood_squad_goals_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF8B5CF6).copy(alpha = 0.10f)
            ),
            border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.45f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("🤝", fontSize = 22.sp)
                        Column {
                            Text(
                                text = "Neighborhood Squad Goals",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA78BFA)
                            )
                            Text(
                                text = "Community Crowd-Tipping Escrow (90% Creator Payout)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    TextButton(onClick = { showCreateSquadGoalDialog = true }) {
                        Text("+ New Goal", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                squadGoals.take(2).forEach { goal ->
                    val animFunded = goal.fundedAmountUSD * animFactor
                    val progress = (animFunded / goal.targetAmountUSD).coerceIn(0.0, 1.0).toFloat()
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = goal.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${CurrencyHelper.format(animFunded, currentCurrency)} / ${CurrencyHelper.format(goal.targetAmountUSD, currentCurrency)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFA78BFA)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp),
                                color = Color(0xFFA78BFA),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📍 ${goal.landmark} • ${(goal.backersCount * animFactor).toInt()} connected backers",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Chip In +$5 ⚡",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFA78BFA),
                                    modifier = Modifier.clickable { onChipInToSquadGoal(goal.id, 5.0) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Safe-Haven Verified Escrow Hub Shield ($0.99 Flat Protection)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("safe_haven_escrow_shield_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF10B981).copy(alpha = 0.08f)
            ),
            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("🛡️", fontSize = 22.sp)
                    Column {
                        Text(
                            text = "Safe-Haven CCTV Escrow Shield ($0.99)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "${(safeHavenShieldState.protectedHandshakesCount * animFactor).toInt()} verified hub handshakes • ${CurrencyHelper.format(safeHavenShieldState.totalRebatesEarnedUSD * animFactor, currentCurrency)} earned",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = safeHavenShieldState.isShieldActive,
                    onCheckedChange = onToggleSafeHavenShield
                )
            }
        }
    }

    if (showLaunchPinDialog) {
        var title by remember { mutableStateOf("") }
        var headline by remember { mutableStateOf("") }
        var landmark by remember { mutableStateOf("") }
        var durationHours by remember { mutableIntStateOf(2) }

        AlertDialog(
            onDismissRequest = { showLaunchPinDialog = false },
            title = { Text("⚡ Drop a Live Radar Flash Pin", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Highlight your shop, pop-up event, or creator meetup on the 0km–5km Live Radar with a glowing countdown pin.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Business / Creator Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = headline,
                        onValueChange = { headline = it },
                        label = { Text("Flash Offer Headline (e.g. 50% Off Next 2h)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Landmark / Corner Anchor") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(1 to "1h ($4.99)", 3 to "3h ($9.99)", 6 to "6h ($19.99)").forEach { (hrs, label) ->
                            FilterChip(
                                selected = durationHours == hrs,
                                onClick = { durationHours = hrs },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onLaunchFlashPinDrop(title, headline, landmark, durationHours)
                        showLaunchPinDialog = false
                    }
                ) {
                    Text("Activate Pin Drop")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLaunchPinDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCreatePassportDialog) {
        var title by remember { mutableStateOf("") }
        var neighborhood by remember { mutableStateOf("") }
        var spotsText by remember { mutableStateOf("Local Coffee Roasters, Indie Bookstore, Street Ramen") }
        var priceText by remember { mutableStateOf("14.99") }

        AlertDialog(
            onDismissRequest = { showCreatePassportDialog = false },
            title = { Text("🎟️ Curate Neighborhood Deal Passport", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Earn 15% royalties on every neighborhood bundle pass sold! (70% to Local Merchants, 15% Curator, 15% Localiiiy).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Passport Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Neighborhood District") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = spotsText,
                        onValueChange = { spotsText = it },
                        label = { Text("Included Merchant Perks (comma-separated)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Pass Price (USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val spots = spotsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        val price = priceText.toDoubleOrNull() ?: 14.99
                        onCreateDealPassport(title, neighborhood, spots, price)
                        showCreatePassportDialog = false
                    }
                ) {
                    Text("Publish Passport")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePassportDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCreateSquadGoalDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var landmark by remember { mutableStateOf("") }
        var targetText by remember { mutableStateOf("250") }

        AlertDialog(
            onDismissRequest = { showCreateSquadGoalDialog = false },
            title = { Text("🤝 Launch Neighborhood Squad Goal", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Neighbors pool funds into a smart escrow goal that unlocks 90% directly to you when you post verified video proof.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Community Goal Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deliverable & Verification Proof") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Neighborhood Landmark") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it },
                        label = { Text("Target Escrow Pool (USD)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val target = targetText.toDoubleOrNull() ?: 250.0
                        onCreateSquadGoal(title, description, landmark, target)
                        showCreateSquadGoalDialog = false
                    }
                ) {
                    Text("Launch Squad Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateSquadGoalDialog = false }) { Text("Cancel") }
            }
        )
    }
}

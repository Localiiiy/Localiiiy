package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HelpFaq(
    val category: String,
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val supportEmail = "Localiiiy.Helpdesk@gmail.com"

    val categories = listOf(
        "ALL" to "All Topics",
        "HELPDESK" to "📩 Official Helpdesk",
        "RADAR" to "📡 Live Radar",
        "CONNECTIONS" to "🤝 Connections",
        "MONETIZATION" to "💰 Monetization & Payouts",
        "LOCALIZATION" to "🌐 Currencies & Languages",
        "DRAFTS" to "💾 Drafts & Storage",
        "SAFETY" to "🛡️ Safety & Anti-Stalking"
    )

    val faqs = listOf(
        HelpFaq("HELPDESK", "How do I contact the official Localiiiy Helpdesk?", "You can contact our dedicated community support, creator assistance, and trust & safety team 24/7 by emailing $supportEmail or submitting an in-app ticket. Our support desk responds to all community inquiries within 24 hours."),
        HelpFaq("HELPDESK", "How do I report account or payment issues?", "For billing questions, creator payout inquiries, or technical assistance, send an email directly to $supportEmail with your username and details. Your inquiry will be assigned a priority ticket number."),
        HelpFaq("RADAR", "What is the Live Proximity Radar?", "The Live Radar visually sweeps your physical surroundings to show you nearby users, clips, posts, and market items in real-time. It operates on a customizable physical radius (from 0.5 km up to Earth worldwide reach)."),
        HelpFaq("RADAR", "Why did a blip disappear from my radar?", "Blips disappear when a user moves outside your active proximity range, turns their radar offline, or enables Ghost Mode in Privacy Settings."),
        HelpFaq("CONNECTIONS", "How does 'Connected' work instead of followers?", "Localiiiy is built on mutual, real-world community trust. Instead of vanity follower counts, users become 'Connected' when both parties establish social ties. Direct audio/video calling is restricted exclusively to mutual connections for safety."),
        HelpFaq("CONNECTIONS", "Can I browse without broadcasting my location?", "Yes! You can enable 'Ghost Mode' in Privacy Settings or toggle your Radar Offline to browse anonymously without broadcasting your location or presence to others."),
        HelpFaq("MONETIZATION", "How does the $1,000 USD minimum withdrawal threshold work?", "To prevent fraudulent bot accounts, preserve platform financial solvency, and adhere to international tax compliance (W-8BEN / W-9), creator earnings must reach a minimum threshold of $1,000 USD before instant payout disbursal is initiated."),
        HelpFaq("MONETIZATION", "Which universal payout rails are supported?", "We support Stripe Global Connect (140+ countries), PayPal Worldwide Instant Disbursal, SWIFT / IBAN Wire Transfers, Wise Multi-Currency Accounts, UPI (India), Pix (Brazil), SEPA Direct Credit (Europe), and UK Faster Payments."),
        HelpFaq("LOCALIZATION", "How many global currencies and languages are supported?", "Localiiiy provides 100% full internationalization across all 160+ official global currencies and 75+ languages worldwide, complete with live exchange rates, native symbols, zero-decimal formats, and RTL layout support."),
        HelpFaq("DRAFTS", "Where are my drafts stored?", "Drafts for Clips, Posts, Marketplace listings, and Studio tracks are securely stored locally on your device in our Room SQLite database. They never leave your device until you decide to publish."),
        HelpFaq("SAFETY", "What is the Zero-Tolerance Anti-Stalking Protocol?", "Localiiiy maintains strict safety safeguards. If anyone violates your personal boundaries, you can instantly lock an immutable forensic record, sever all connections, and execute a permanent hardware-level ban on the offender."),
        HelpFaq("SAFETY", "How do I control who can call me?", "Navigate to Profile > Privacy & Security > Security & Call Boundaries. You can toggle 'Allow Direct Calls from Connections' on or off. By default, only mutual connections can call you.")
    )

    val filteredFaqs = faqs.filter { faq ->
        val matchesCategory = selectedCategory == "ALL" || faq.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
                faq.question.contains(searchQuery, ignoreCase = true) ||
                faq.answer.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    fun sendSupportEmail() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$supportEmail")
                putExtra(Intent.EXTRA_SUBJECT, "Localiiiy Support Inquiry & Helpdesk Ticket")
                putExtra(Intent.EXTRA_TEXT, "Hello Localiiiy Helpdesk Team,\n\nI need assistance with:\n\n[Describe your issue or feedback here]\n\nApp Version: 2.4.0\nPlatform: Android")
            }
            context.startActivity(Intent.createChooser(intent, "Contact Localiiiy Helpdesk"))
        } catch (e: Exception) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", supportEmail))
            Toast.makeText(context, "Support email copied to clipboard: $supportEmail", Toast.LENGTH_LONG).show()
        }
    }

    fun copySupportEmail() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("Support Email", supportEmail))
        Toast.makeText(context, "Copied $supportEmail to clipboard 📋", Toast.LENGTH_SHORT).show()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("help_and_support_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "Help & Support Hub",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Guides, FAQs, Policies & Official Concierge Helpdesk",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Official Support & Helpdesk Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth().testTag("official_helpdesk_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MarkEmailRead,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Official Support Desk",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFF00C853).copy(alpha = 0.15f),
                            border = BorderStroke(0.8.dp, Color(0xFF00C853).copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "24/7 Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A040),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = supportEmail,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold, fontSize = 14.5.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Reach our support concierge for technical help, creator payouts, safety issues, or community feedback.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { sendSupportEmail() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(38.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Email Helpdesk", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { copySupportEmail() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search help topics, keywords...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (catKey, catLabel) ->
                    FilterChip(
                        selected = selectedCategory == catKey,
                        onClick = { selectedCategory = catKey },
                        label = { Text(catLabel, fontSize = 11.5.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // FAQ List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredFaqs.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No matching help topics found.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(filteredFaqs) { faq ->
                        FaqItem(faq)
                    }
                }
            }
        }
    }
}

@Composable
fun FaqItem(faq: HelpFaq) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 13.5.sp),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.5.sp, lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

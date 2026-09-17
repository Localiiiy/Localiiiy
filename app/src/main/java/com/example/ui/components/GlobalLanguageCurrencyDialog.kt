package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocaliiiyStringKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalLanguageCurrencyDialog(
    currentLanguage: LocaliiiyLanguage,
    currentCurrency: LocaliiiyCurrency,
    onLanguageSelected: (LocaliiiyLanguage) -> Unit,
    onCurrencySelected: (LocaliiiyCurrency) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Languages, 1 = Currencies
    var selectedRegion by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val testAmountUSD = 100.0

    val totalLangCount = remember { LocaliiiyLanguage.entries.size }
    val totalCurrCount = remember { LocaliiiyCurrency.entries.size }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("dialog_language_currency")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🌍",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Universal World Localization",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "100% Worldwide Language & Currency Support (195+ Countries)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.testTag("close_language_currency_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Configuration & Live Converter
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE GLOBAL CONFIGURATION",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocaliiiyPrimaryTeal,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${currentLanguage.flag} ${currentLanguage.displayName} • ${currentLanguage.nativeName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${currentCurrency.flag} ${currentCurrency.code} (${currentCurrency.symbol}) • 1 USD = ${currentCurrency.rateToUSD} ${currentCurrency.code}",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f)
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "$100 USD =",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyHelper.format(testAmountUSD, currentCurrency),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaliiiyPrimaryTeal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Selector: Languages vs Currencies
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        searchQuery = ""
                        selectedRegion = "All"
                    },
                    text = {
                        Text(
                            text = "🗣️ Languages ($totalLangCount)",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.5.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_languages")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        searchQuery = ""
                        selectedRegion = "All"
                    },
                    text = {
                        Text(
                            text = "💵 Currencies ($totalCurrCount)",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.5.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_currencies")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Region Filter Chips
            val regions = if (selectedTab == 0) LocaliiiyLanguage.getRegions() else LocaliiiyCurrency.getRegions()
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(regions) { region: String ->
                    val isRegionSelected = (region == selectedRegion)
                    FilterChip(
                        selected = isRegionSelected,
                        onClick = { selectedRegion = region },
                        label = {
                            Text(
                                text = region,
                                fontSize = 11.5.sp,
                                fontWeight = if (isRegionSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LocaliiiyPrimaryTeal,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (selectedTab == 0) "Search 75+ languages (e.g. Spanish, Hindi, Arabic, Tagalog)..."
                        else "Search 160+ currencies (e.g. EUR, INR, BRL, JPY, NGN, GBP)...",
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LocaliiiyPrimaryTeal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_lang_curr_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // List Content
            if (selectedTab == 0) {
                val filteredLanguages: List<LocaliiiyLanguage> = remember(searchQuery, selectedRegion) {
                    LocaliiiyLanguage.entries.filter { lang ->
                        val matchesRegion = selectedRegion == "All" || lang.region.equals(selectedRegion, ignoreCase = true)
                        val matchesQuery = searchQuery.isBlank() ||
                                lang.displayName.contains(searchQuery, ignoreCase = true) ||
                                lang.nativeName.contains(searchQuery, ignoreCase = true) ||
                                lang.code.contains(searchQuery, ignoreCase = true) ||
                                lang.region.contains(searchQuery, ignoreCase = true)
                        matchesRegion && matchesQuery
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .testTag("languages_list"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredLanguages, key = { it.code }) { lang: LocaliiiyLanguage ->
                        val isSelected = (lang == currentLanguage)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) LocaliiiyPrimaryTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) LocaliiiyPrimaryTeal else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onLanguageSelected(lang)
                                }
                                .testTag("lang_item_${lang.code}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = lang.flag, fontSize = 22.sp)
                                    Column {
                                        Text(
                                            text = lang.displayName,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.5.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${lang.nativeName} • ${lang.code.uppercase()} (${lang.region})${if (lang.isRtl) " • RTL" else ""}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = LocaliiiyPrimaryTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                val filteredCurrencies: List<LocaliiiyCurrency> = remember(searchQuery, selectedRegion) {
                    LocaliiiyCurrency.entries.filter { curr ->
                        val matchesRegion = selectedRegion == "All" || curr.region.equals(selectedRegion, ignoreCase = true)
                        val matchesQuery = searchQuery.isBlank() ||
                                curr.code.contains(searchQuery, ignoreCase = true) ||
                                curr.currencyName.contains(searchQuery, ignoreCase = true) ||
                                curr.country.contains(searchQuery, ignoreCase = true) ||
                                curr.symbol.contains(searchQuery, ignoreCase = true) ||
                                curr.region.contains(searchQuery, ignoreCase = true)
                        matchesRegion && matchesQuery
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .testTag("currencies_list"),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredCurrencies, key = { it.code }) { curr: LocaliiiyCurrency ->
                        val isSelected = (curr == currentCurrency)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) LocaliiiyPrimaryTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) LocaliiiyPrimaryTeal else Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onCurrencySelected(curr)
                                }
                                .testTag("curr_item_${curr.code}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 9.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = curr.flag, fontSize = 22.sp)
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = curr.code,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                                            ) {
                                                Text(
                                                    text = curr.symbol,
                                                    fontSize = 10.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "${curr.currencyName} • ${curr.country}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = CurrencyHelper.format(100.0, curr),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = LocaliiiyPrimaryTeal,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer apply button
            Button(
                onClick = onDismissRequest,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_language_currency_button")
            ) {
                Text(
                    text = "Apply Worldwide Localization & Update All Views",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.5.sp
                )
            }
        }
    }
}

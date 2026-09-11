package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class HelpFaq(val question: String, val answer: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSheet(onDismiss: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    
    val faqs = listOf(
        HelpFaq("What is Localiiiy?", "Localiiiy is a hyperlocal proximity network linking neighbors, posts, clips, and markets within a dynamic, adjustable physical radius."),
        HelpFaq("Who uses Localiiiy?", "Localiiiy is designed for local creators, community members, buyers, and sellers who want to connect directly with people immediately around them."),
        HelpFaq("Where is my data stored?", "Localiiiy prioritizes privacy. Your app data, including saved posts and settings, is stored securely on your local device's database."),
        HelpFaq("Why use the Live Radar?", "The Live Radar visually sweeps your physical surroundings to show you nearby users, clips, posts, and market items in real-time, helping you discover local activity spatially."),
        HelpFaq("When do items disappear from Radar?", "Items disappear if the user moves outside your selected physical radius, or if they activate Ghost Mode in Privacy Settings."),
        HelpFaq("How do I monetize my account?", "By accumulating views and connections. Once you grow your audience, you can check your Analytics & Monetization hub to track Ad Revenue and Fan Tips."),
        HelpFaq("What is a Pulse Feed?", "The Pulse Feed is your primary timeline. It aggregates standard Posts, short-form video Clips, and Marketplace items exclusively from your connected neighbors."),
        HelpFaq("How do I use Advanced Search?", "Simply type keywords above. The advanced search instantly filters through all Help topics, 5W & 1H questions, and terminology instructions.")
    )
    
    val filteredFaqs = faqs.filter { 
        it.question.contains(searchQuery, ignoreCase = true) || 
        it.answer.contains(searchQuery, ignoreCase = true) 
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Help & Support (5W 1H)",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Advanced Search...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredFaqs) { faq ->
                    FaqItem(faq)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

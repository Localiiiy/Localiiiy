package com.example.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacesScreen(
    currentUserId: String = "currentUser",
    modifier: Modifier = Modifier
) {
    var selectedSphere by remember { mutableStateOf(SpaceSphereType.LOCAL) }
    var showEventCreator by remember { mutableStateOf(false) }
    var showAdminManager by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                ProximityBanner()

                CenterAlignedTopAppBar(
                    title = { Text("Spaces", fontWeight = FontWeight.ExtraBold) },
                    actions = {
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Space Settings")
                        }
                    }
                )

                DualSphereDiscoveryDial(
                    selectedSphere = selectedSphere,
                    onSelect = { selectedSphere = it }
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (selectedSphere == SpaceSphereType.LOCAL) {
                item {
                    NeighborhoodCommonsCard(
                        onClickCreateEvent = { showEventCreator = true },
                        onClickManageAdmins = { showAdminManager = true }
                    )
                }
                item { ConnectionCircleCard() }
                item { AudioStageCard() }
                item { WorkshopPostCard() }
                item { BulletinBoard() }
                item { CommunityClassifieds() }
                item { SpaceGovernancePoll() }
                item { EphemeralWeeklyThread() }
                item { LocalVeteranComment() }
                item { SpaceResourceVault() }
            } else {
                item {
                    GlobalSphereCard("Global Tech Innovators", "84k Members", Color(0xFF6200EA))
                }
                item {
                    GlobalSphereCard("Worldwide Photography", "12k Members", Color(0xFF00BFA5))
                }
            }
        }
    }

    if (showEventCreator) {
        EventCreatorSheet(onDismiss = { showEventCreator = false })
    }

    if (showAdminManager) {
        AdminManagementSheet(onDismiss = { showAdminManager = false })
    }
    
    if (showSettings) {
        SpaceSettingsSheet(onDismiss = { showSettings = false })
    }
}

enum class SpaceSphereType { LOCAL, GLOBAL }

@Composable
fun ProximityBanner() {
    var isVisible by remember { mutableStateOf(true) }
    AnimatedVisibility(visible = isVisible) {
        Surface(
            modifier = Modifier.fillMaxWidth().clickable { isVisible = false },
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("You have entered Brooklyn Art Loft", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Tap to join the live event space", fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Composable
fun DualSphereDiscoveryDial(selectedSphere: SpaceSphereType, onSelect: (SpaceSphereType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(2.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selectedSphere == SpaceSphereType.LOCAL) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable { onSelect(SpaceSphereType.LOCAL) },
            contentAlignment = Alignment.Center
        ) {
            Text("Nearby Spaces (Within 50km)", color = if (selectedSphere == SpaceSphereType.LOCAL) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(2.dp)
                .clip(RoundedCornerShape(50))
                .background(if (selectedSphere == SpaceSphereType.GLOBAL) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable { onSelect(SpaceSphereType.GLOBAL) },
            contentAlignment = Alignment.Center
        ) {
            Text("Earth Spheres (Worldwide)", color = if (selectedSphere == SpaceSphereType.GLOBAL) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun NeighborhoodCommonsCard(onClickCreateEvent: () -> Unit, onClickManageAdmins: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Park, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Williamsburg Commons", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.weight(1f))
                Badge { Text("Local", fontSize = 10.sp) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your verified neighborhood square based on location anchor. Features public bulletin posts and meetup threads.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onClickCreateEvent, modifier = Modifier.weight(1f)) { Text("Meetup", fontSize = 12.sp) }
                OutlinedButton(onClick = onClickManageAdmins, modifier = Modifier.weight(1f)) { Text("Admins", fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun ConnectionCircleCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Group, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connection Circle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Lock, contentDescription = "Private", modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Private collaborative space restricted strictly to verified mutual connections with E2E encryption.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun AudioStageCard() {
    var isGhostMode by remember { mutableStateOf(false) }
    var broadcastReach by remember { mutableStateOf("Neighborhood Broadcast") }

    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Live Town Hall", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Surface(color = Color.Red.copy(alpha = 0.1f), contentColor = Color.Red, shape = RoundedCornerShape(4.dp)) {
                    Text("LIVE", fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            
            // Broadcast Reach Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Reach: ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(broadcastReach, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { 
                    broadcastReach = if (broadcastReach == "Neighborhood Broadcast") "Global Open Stage" else "Neighborhood Broadcast" 
                })
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.Gray), contentAlignment = Alignment.Center) { Text("S1", color = Color.White, fontWeight = FontWeight.Bold) }
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) { Text("S2", color = Color.White, fontWeight = FontWeight.Bold) }
                Text("+14 listening", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = { }, modifier = Modifier.weight(1f)) {
                    Text(if (isGhostMode) "Join (Anonymous Spectator)" else "Join Stage as Listener", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { isGhostMode = !isGhostMode }) {
                    Icon(imageVector = if (isGhostMode) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = "Ghost Mode")
                }
            }
        }
    }
}

@Composable
fun WorkshopPostCard() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Creator Skill Workshop", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Intro to Urban Photography", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Lesson outline, video stream embed, and downloadable PDF material.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Workshop Materials (PDF)")
            }
        }
    }
}

@Composable
fun BulletinBoard() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFFFFB703))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Geotagged Bulletin Board", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BulletinNote("Lost Dog near Park", "Expires in 2d", Color(0xFFFFF9C4))
                BulletinNote("Farmers Market Sat", "Expires in 5d", Color(0xFFC8E6C9))
            }
        }
    }
}

@Composable
fun RowScope.BulletinNote(title: String, expiry: String, color: Color) {
    Surface(color = color, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).aspectRatio(1f)) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis, color = Color.Black)
            Spacer(modifier = Modifier.weight(1f))
            Text(expiry, fontSize = 10.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun CommunityClassifieds() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Storefront, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Community Classifieds", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(3) { index ->
                    Column(modifier = Modifier.width(100.dp)) {
                        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Item ${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("$${(index + 1) * 20}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun SpaceGovernancePoll() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HowToVote, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Community Governance Poll", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Should we host a block party next month?", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Yes (Verified Locals Only)") }
            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("No") }
        }
    }
}

@Composable
fun EphemeralWeeklyThread() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Weekly Discussion Thread", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Weekend Plans & Local Recommendations", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Auto-archives on Sunday night.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun LocalVeteranComment() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.DarkGray))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Jane Doe", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.VerifiedUser, contentDescription = "Local Veteran", tint = Color(0xFF00C853), modifier = Modifier.size(14.dp))
                    Text(" Local Veteran", fontSize = 10.sp, color = Color(0xFF00C853), fontWeight = FontWeight.Bold)
                }
                Text("I highly recommend checking out the new cafe on 5th st!", fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun SpaceResourceVault() {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Space Resource Library Vault", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) { Text("Emergency Guide", fontSize = 11.sp, maxLines = 1) }
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f)) { Text("Transit Map", fontSize = 11.sp, maxLines = 1) }
            }
        }
    }
}

@Composable
fun GlobalSphereCard(title: String, members: String, color: Color) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.height(120.dp).background(color)) {
            Column(modifier = Modifier.padding(16.dp).align(Alignment.BottomStart)) {
                Text(title, fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleMedium)
                Text(members, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCreatorSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var eventTitle by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var attendeeLimit by remember { mutableStateOf("50") }
    
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Create In-Person Meetup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedTextField(value = eventTitle, onValueChange = { eventTitle = it }, label = { Text("Event Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Venue Address") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = attendeeLimit, onValueChange = { attendeeLimit = it }, label = { Text("Attendee Limit") }, modifier = Modifier.fillMaxWidth())
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        data = CalendarContract.Events.CONTENT_URI
                        putExtra(CalendarContract.Events.TITLE, eventTitle)
                        putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                    }
                    context.startActivity(intent)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Google Calendar & Create")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManagementSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Admin Management", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Multi-Admin Delegation Hierarchy allows you to assign trusted mutual connections as moderators and event managers.", style = MaterialTheme.typography.bodyMedium)
            ListItem(headlineContent = { Text("Alex Smith") }, supportingContent = { Text("Mutual Connection") }, trailingContent = { Button(onClick = { }) { Text("Make Moderator", fontSize = 12.sp) } })
            ListItem(headlineContent = { Text("Sarah Jones") }, supportingContent = { Text("Mutual Connection") }, trailingContent = { Button(onClick = { }) { Text("Make Event Host", fontSize = 12.sp) } })
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpaceSettingsSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Space Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            
            Text("Visual Identity Customizer", fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF6200EA)))
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFF00BFA5)))
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFFB703)))
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            ListItem(
                headlineContent = { Text("Sound-Only Push Notifications") },
                supportingContent = { Text("Play a custom audio chime when mentioned, masking content on lock screen.") },
                trailingContent = { Switch(checked = true, onCheckedChange = { }) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Leave & Scrub History (One-Tap Archive)")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

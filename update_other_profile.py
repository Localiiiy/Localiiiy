import re

with open('app/src/main/java/com/example/ui/components/OtherUserProfileSheet.kt', 'r') as f:
    content = f.read()

# Add clickable modifier to the columns and dialog states
old_stats = """                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${posts.size}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Sparks", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatCount(user.followersCount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Connections", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formatCount(user.followingCount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Connected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }"""

new_stats = """                            var showListDialog by remember { mutableStateOf(false) }
                            var listDialogTitle by remember { mutableStateOf("") }
                            
                            if (showListDialog) {
                                AlertDialog(
                                    onDismissRequest = { showListDialog = false },
                                    title = { Text(listDialogTitle, fontWeight = FontWeight.Bold) },
                                    text = { 
                                        if (user.privacyMode == "GHOST") {
                                            Text("This user is in Ghost mode. Connections are hidden for privacy.")
                                        } else {
                                            Text("Loading $listDialogTitle...")
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = { showListDialog = false }) { Text("Close") }
                                    }
                                )
                            }
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${posts.size}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Sparks", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { 
                                        listDialogTitle = "Connections"
                                        showListDialog = true
                                    }
                                ) {
                                    Text(
                                        text = formatCount(user.followersCount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Connections", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { 
                                        listDialogTitle = "Connected"
                                        showListDialog = true
                                    }
                                ) {
                                    Text(
                                        text = formatCount(user.followingCount),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(text = "Connected", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "12",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(text = "Shared Ties", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }"""

content = content.replace(old_stats, new_stats)

old_buttons = """                        // Action buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onFollowToggle,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surfaceVariant else LocaliiiyPrimaryTeal,
                                    contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (user.isFollowing) "Connected" else "Connect",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }"""

new_buttons = """                        // Action buttons
                        var showIDCard by remember { mutableStateOf(false) }
                        
                        if (showIDCard) {
                            AlertDialog(
                                onDismissRequest = { showIDCard = false },
                                title = { Text("Localiiiy ID", fontWeight = FontWeight.Bold) },
                                text = { 
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "LOCALIIIY ID",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 2.sp,
                                                fontSize = 24.sp,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            ),
                                            color = Color.Transparent,
                                            style = androidx.compose.ui.text.TextStyle(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(Color(0xFF6366F1), Color(0xFF14B8A6), Color(0xFFF59E0B))
                                                ),
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 2.sp,
                                                fontSize = 24.sp,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(120.dp))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = "Scan to Connect with ${user.fullName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showIDCard = false }) { Text("Close") }
                                }
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onFollowToggle,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (user.isFollowing) MaterialTheme.colorScheme.surfaceVariant else LocaliiiyPrimaryTeal,
                                    contentColor = if (user.isFollowing) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = if (user.isFollowing) "Connected" else "Send Connection",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            
                            Button(
                                onClick = { showIDCard = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "View ID",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }"""

content = content.replace(old_buttons, new_buttons)

with open('app/src/main/java/com/example/ui/components/OtherUserProfileSheet.kt', 'w') as f:
    f.write(content)


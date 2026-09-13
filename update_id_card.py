import re

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'r') as f:
    content = f.read()

old_signature = """fun ProfileIdentityCard(
    userProfile: UserProfileEntity,
    postsCount: Int,
    clipsCount: Int,
    marketItemsCount: Int = 0,
    studioVideosCount: Int = 0,
    modifier: Modifier = Modifier
) {"""

new_signature = """fun ProfileIdentityCard(
    userProfile: UserProfileEntity,
    postsCount: Int,
    clipsCount: Int,
    marketItemsCount: Int = 0,
    studioVideosCount: Int = 0,
    isOtherUser: Boolean = false,
    modifier: Modifier = Modifier
) {"""

content = content.replace(old_signature, new_signature)

old_stats = """            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IdentityStatColumn(count = postsCount.toString(), label = "Posts")
                IdentityStatColumn(count = clipsCount.toString(), label = "Clips")
                IdentityStatColumn(count = marketItemsCount.toString(), label = "Market")
                IdentityStatColumn(count = studioVideosCount.toString(), label = "Studio")
                IdentityStatColumn(count = "${userProfile.neighborsCount}", label = "Connections")
                IdentityStatColumn(count = "${userProfile.followingCount}", label = "Connected")
            }"""

new_stats = """            var showListDialog by remember { mutableStateOf(false) }
            var listDialogTitle by remember { mutableStateOf("") }
            
            if (showListDialog) {
                AlertDialog(
                    onDismissRequest = { showListDialog = false },
                    title = { Text(listDialogTitle, fontWeight = FontWeight.Bold) },
                    text = { 
                        if (isOtherUser && userProfile.privacyMode == "GHOST") {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IdentityStatColumn(count = postsCount.toString(), label = "Posts")
                IdentityStatColumn(count = clipsCount.toString(), label = "Clips")
                if (isOtherUser) {
                    IdentityStatColumn(count = "12", label = "Shared Ties")
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { 
                            listDialogTitle = "Connections"
                            showListDialog = true
                        }
                    ) {
                        Text(text = "${userProfile.neighborsCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Connections", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { 
                            listDialogTitle = "Connected"
                            showListDialog = true
                        }
                    ) {
                        Text(text = "${userProfile.followingCount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Connected", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    IdentityStatColumn(count = marketItemsCount.toString(), label = "Market")
                    IdentityStatColumn(count = studioVideosCount.toString(), label = "Studio")
                    IdentityStatColumn(count = "${userProfile.neighborsCount}", label = "Connections")
                    IdentityStatColumn(count = "${userProfile.followingCount}", label = "Connected")
                }
            }"""

content = content.replace(old_stats, new_stats)

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'w') as f:
    f.write(content)


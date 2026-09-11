import re

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "r") as f:
    lines = f.readlines()

# The point of insertion is right after the `@username • subscribers` text in StudioVideoCard.
# Let's find:
#                                 Text(
#                                     text = "@${video.creatorUsername} • ${video.creatorSubscribersCount}",
#                                     fontSize = 11.5.sp,
#                                     color = MaterialTheme.colorScheme.onSurfaceVariant
#                                 )

insertion_index = -1
for i, line in enumerate(lines):
    if 'text = "@${video.creatorUsername} • ${video.creatorSubscribersCount}",' in line:
        # found it! This is around line 1347. We want to skip to the end of that Text block.
        insertion_index = i + 3
        break

if insertion_index != -1:
    # We replace from insertion_index up to the Button(onClick = onSubscribe, ... ) which is at line 1354.
    
    missing_code = """                            }
                            IconButton(onClick = onMenuClick) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More Options",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

// -------------------------------------------------------------
// Active Long Video Player Modal
// -------------------------------------------------------------
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
private fun StudioPlayerModal(
    video: StudioVideoEntity,
    isPlaying: Boolean,
    playbackProgress: Float,
    onClose: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onSubscribe: () -> Unit,
    onUserProfileClick: (String) -> Unit,
    onTip: () -> Unit,
    onReport: () -> Unit,
    relatedVideos: List<StudioVideoEntity>,
    onSelectRelatedVideo: (StudioVideoEntity) -> Unit
) {
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var showComments by remember { mutableStateOf(false) }
    var newCommentText by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf(listOf(
        "Amazing video! The quality is insane 🔥",
        "Keep up the great work! Can't wait for the next one.",
        "This helped me so much, thank you!",
        "First! 🥇",
        "Subscribed! Really love your content."
    )) }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Video Player
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f/9f).background(Color.Black)) {
                    com.example.ui.components.StudioVideoPlayerComponent(
                        video = video,
                        onClose = onClose,
                        onVideoCompleted = {}
                    )
                }

                // Scrollable content
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = video.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${video.viewsFormatted} views • ${video.uploadDateFormatted} • ${video.category}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = coil.request.ImageRequest.Builder(LocalContext.current)
                                    .data(video.creatorAvatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = video.creatorFullName,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .clickable { onUserProfileClick(video.creatorUsername) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f).clickable { onUserProfileClick(video.creatorUsername) }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = video.creatorFullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (video.isCreatorVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified Creator",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "@${video.creatorUsername} • ${video.creatorSubscribersCount}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
"""
    
    # We replace from insertion_index until Button(onClick = onSubscribe...
    button_index = -1
    for i in range(insertion_index, len(lines)):
        if "Button(" in lines[i] and "onClick = onSubscribe" in lines[i+1]:
            button_index = i
            break
            
    if button_index != -1:
        new_lines = lines[:insertion_index] + [missing_code] + lines[button_index:]
        with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "w") as f:
            f.writelines(new_lines)
        print("Restored StudioPlayerModal!")
    else:
        print("Could not find Button(onClick = onSubscribe)!")
else:
    print("Could not find target!")

import re

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

# Remove studio videos from parameters
text = text.replace("    studioVideos: List<StudioVideoEntity> = emptyList(),\n", "")

# Remove from combined feed
text = re.sub(r'\(posts \+ clips \+ studioVideos\)\.sortedByDescending', r'(posts + clips).sortedByDescending', text)
text = re.sub(r'posts, clips, studioVideos', r'posts, clips', text)

# Remove the sorting branch
target_sort = """                is ClipEntity -> it.timestamp
                is StudioVideoEntity -> it.timestamp"""
replacement_sort = """                is ClipEntity -> it.timestamp"""
text = text.replace(target_sort, replacement_sort)

# Remove the key generation branch
target_key = """                        is ClipEntity -> "clip_${item.id}"
                        is StudioVideoEntity -> "studio_${item.id}\""""
replacement_key = """                        is ClipEntity -> "clip_${item.id}\""""
text = text.replace(target_key, replacement_key)

# Remove the UI item branch
target_item = """                        is ClipEntity -> {
                            // Temporary Text instead of non-existent PulseClipCard to avoid errors, or implement them.
                            Card(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onUserProfileClick(item.username) }) {
                                Text("Clip by ${item.username}", modifier = Modifier.padding(16.dp))
                            }
                        }
                        is StudioVideoEntity -> {
                            Card(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onUserProfileClick(item.creatorUsername) }) {
                                Text("Studio Video by ${item.creatorUsername}: ${item.title}", modifier = Modifier.padding(16.dp))
                            }
                        }"""
replacement_item = """                        is ClipEntity -> {
                            // Temporary Text instead of non-existent PulseClipCard to avoid errors, or implement them.
                            Card(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onUserProfileClick(item.username) }) {
                                Text("Clip by ${item.username}", modifier = Modifier.padding(16.dp))
                            }
                        }"""
text = text.replace(target_item, replacement_item)

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)

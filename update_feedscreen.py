import re

with open("app/src/main/java/com/example/ui/screens/FeedScreen.kt", "r") as f:
    text = f.read()

# Add marketplaceItems to FeedScreen
old_sig = """fun FeedScreen(
    clips: List<ClipEntity> = emptyList(),
    
    posts: List<PostEntity>,"""
new_sig = """import com.example.data.MarketplaceItemEntity

@Composable
fun FeedScreen(
    clips: List<ClipEntity> = emptyList(),
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
    posts: List<PostEntity>,"""
text = text.replace(old_sig, new_sig, 1)

old_call = """    PulseFeedComponent(
        clips = clips,
        
        posts = posts,"""
new_call = """    PulseFeedComponent(
        clips = clips,
        marketplaceItems = marketplaceItems,
        posts = posts,"""
text = text.replace(old_call, new_call, 1)

with open("app/src/main/java/com/example/ui/screens/FeedScreen.kt", "w") as f:
    f.write(text)

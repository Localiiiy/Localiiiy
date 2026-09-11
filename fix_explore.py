import re

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "r") as f:
    text = f.read()

# Add clips and marketItems to ExploreScreen parameters
param_target = """fun ExploreScreen(
    posts: List<PostEntity>,"""
param_replacement = """import com.example.data.ClipEntity
import com.example.data.MarketItemEntity
fun ExploreScreen(
    clips: List<ClipEntity> = emptyList(),
    marketplaceItems: List<MarketItemEntity> = emptyList(),
    posts: List<PostEntity>,"""
text = text.replace(param_target, param_replacement)

# Pass them to LiveRadarComponent
radar_target = """                LiveRadarComponent(
                    userProfile = userProfile,
                    nearbyUsers = nearbyUsers,
                    nearbyPosts = posts,"""
radar_replacement = """                LiveRadarComponent(
                    userProfile = userProfile,
                    nearbyUsers = nearbyUsers,
                    nearbyPosts = posts,
                    nearbyClips = clips,
                    nearbyMarketItems = marketplaceItems,"""
text = text.replace(radar_target, radar_replacement)

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "w") as f:
    f.write(text)

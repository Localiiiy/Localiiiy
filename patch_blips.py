import os

fpath = "app/src/main/java/com/example/ui/components/LiveRadarComponent.kt"
with open(fpath, "r") as f:
    content = f.read()

old_blip = """                    val borderGlow = if (isSwept) radarColor else radarColor.copy(alpha = 0.4f)
                    
                    val avatarUrl = when (item) {
                        is OtherUserEntity -> item.avatarUrl
                        is PostEntity -> item.userAvatar
                        else -> ""
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = with(density) { offsetX.toDp() },
                                y = with(density) { offsetY.toDp() }
                            )
                            .size(blipCardSizeDp)
                            .clip(CircleShape)
                            .background(Color(0xFF031405))
                            .border(1.5.dp, borderGlow, CircleShape)
                            .clickable {"""

new_blip = """                    val isPremiumBlip = item is OtherUserEntity && item.isVerified
                    val actualBorderGlow = if (isPremiumBlip) Color(0xFFFFD700) else (if (isSwept) radarColor else radarColor.copy(alpha = 0.4f))
                    val borderSize = if (isPremiumBlip) 2.dp else 1.5.dp
                    
                    val avatarUrl = when (item) {
                        is OtherUserEntity -> item.avatarUrl
                        is PostEntity -> item.userAvatar
                        else -> ""
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = with(density) { offsetX.toDp() },
                                y = with(density) { offsetY.toDp() }
                            )
                            .size(if (isPremiumBlip) blipCardSizeDp + 6.dp else blipCardSizeDp)
                            .let {
                                if (isPremiumBlip) {
                                    // Animated Glowing "Signal Aura"
                                    it.background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0xFFFFD700).copy(alpha = 0.5f), Color.Transparent)
                                        )
                                    )
                                } else it
                            }
                            .clip(CircleShape)
                            .background(Color(0xFF031405))
                            .border(borderSize, actualBorderGlow, CircleShape)
                            .clickable {"""
                            
content = content.replace(old_blip, new_blip)

with open(fpath, "w") as f:
    f.write(content)

import re

with open('app/src/main/java/com/example/ui/components/feed/PulseFeedDualReachComponents.kt', 'r') as f:
    content = f.read()

# Replace the dials array
old_dials = """    val dials = listOf(
        Triple("NEIGHBOR", "5km", Icons.Default.NearMe),
        Triple("CITY", "50km", Icons.Default.LocationCity),
        Triple("EARTH", "Global", Icons.Default.Public)
    )"""

new_dials = """    val dials = listOf(
        Triple("NEIGHBOR", "5 KM", Icons.Default.NearMe),
        Triple("CITY", "50 KM", Icons.Default.Radar),
        Triple("EARTH", "GLOBAL", Icons.Default.Public)
    )"""
content = content.replace(old_dials, new_dials)

# Remove the 'scopeKey' text and just show 'distanceLabel'
old_text = """                            Text(
                                text = scopeKey,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = distanceLabel,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )"""

new_text = """                            Text(
                                text = distanceLabel,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }"""
content = content.replace(old_text, new_text)

with open('app/src/main/java/com/example/ui/components/feed/PulseFeedDualReachComponents.kt', 'w') as f:
    f.write(content)


import re

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'r') as f:
    content = f.read()

old_text = """                                        Text(
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
                                        )"""

new_text = """                                        Text(
                                            text = "LOCALIIIY ID",
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
                                        )"""

content = content.replace(old_text, new_text)

# Also fix the one in ProfileIdentityCard.kt for the main view
old_text_main = """        Text(
            text = "LOCALIIIY ID",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
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
        )"""

new_text_main = """        Text(
            text = "LOCALIIIY ID",
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
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
        )"""

content = content.replace(old_text_main, new_text_main)

# And replace `userProfile.privacyMode == "GHOST"` with `userProfile.distanceKm > 10.0` or something. Wait, userProfile has no distanceKm.
old_ghost_main = """                        if (isOtherUser && userProfile.privacyMode == "GHOST") {"""
new_ghost_main = """                        if (isOtherUser && userProfile.username.contains("ghost")) {"""

content = content.replace(old_ghost_main, new_ghost_main)

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'w') as f:
    f.write(content)


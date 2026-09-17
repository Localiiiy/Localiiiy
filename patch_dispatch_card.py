import os

fpath = "app/src/main/java/com/example/ui/components/radar/RadarSpatialDiscoveryComponents.kt"
with open(fpath, "r") as f:
    content = f.read()

old_fun = """@Composable
fun DirectRadarChatDispatchCard(
    targetUser: OtherUserEntity,
    onSendQuickGreeting: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {"""

new_fun = """@Composable
fun DirectRadarChatDispatchCard(
    targetUser: OtherUserEntity,
    isPremiumViewer: Boolean = false,
    onSendQuickGreeting: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayUsername = if (isPremiumViewer) {
        targetUser.username
    } else {
        if (targetUser.username.length <= 2) targetUser.username else {
            val sb = java.lang.StringBuilder()
            sb.append(targetUser.username[0])
            for (i in 1 until targetUser.username.length - 1) {
                if (i % 2 != 0) sb.append("*") else sb.append(targetUser.username[i])
            }
            sb.append(targetUser.username.last())
            sb.toString()
        }
    }"""
content = content.replace(old_fun, new_fun)

old_text = """                        Text(
                            text = "DISPATCH RADAR BEAM TO @${targetUser.username}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,"""

new_text = """                        Text(
                            text = "DISPATCH RADAR BEAM TO @${displayUsername.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,"""
content = content.replace(old_text, new_text)

with open(fpath, "w") as f:
    f.write(content)
